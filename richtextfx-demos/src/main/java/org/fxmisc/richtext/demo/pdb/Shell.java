package org.fxmisc.richtext.demo.pdb;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineSegment;
import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;

import static java.lang.Thread.sleep;

/**
 * @author yq
 */
public class Shell implements Closeable{
    volatile boolean stopProcess = false;
    Process process;
    InputStream errorResponse;
    InputStream normalResponse;
    OutputStream cmdPipe;

    BlockingQueue<String> responseQ = new ArrayBlockingQueue<>(30);
    BlockingQueue<String> errorQ = new ArrayBlockingQueue<>(30);
    BlockingQueue<Deserializer> deserializerQ  = new ArrayBlockingQueue<>(60);

    public BlockingQueue<String> getRespQ(){
        return responseQ;
    }
    public BlockingQueue<String> getErrorQ(){
        return errorQ;
    }



    public <T extends Command> CompletableFuture<? extends CMDResp<T>> executeCMD(T cmd,String... args) {
        Deserializer deserializer = cmd.newDeserializer();
        try {
            deserializerQ.put(deserializer);
        } catch (InterruptedException e) {
            deserializer.future().completeExceptionally(e);
            return deserializer.future();
        }
        runAsync(cmd.getCMDStr(args));
        return deserializer.future();
    }

    Executor thread = Executors.newSingleThreadExecutor();
    Executor threadError = Executors.newSingleThreadExecutor();
    Executor threadRespDeserializer = Executors.newSingleThreadExecutor();
    Executor threadErrorDeserializer = Executors.newSingleThreadExecutor();


    public  CompletableFuture<CMDResp>  startWithCmd(Command cmd) {
        try {
            process = exec(cmd);
        } catch (IOException e) {
            CompletableFuture<CMDResp>  fu = new CompletableFuture();
            fu.completeExceptionally(e);
            return fu;
        }
        cmdPipe = process.getOutputStream();
        try {
            File file = new File("log.txt");
            FileInputStream in = new FileInputStream(file);
            normalResponse = in;
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorResponse = process.getErrorStream();
        Deserializer de = cmd.newDeserializer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(normalResponse));
        BufferedReader readerError = new BufferedReader(new InputStreamReader(errorResponse));
        try {
            deserializerQ.put(de);
        } catch (InterruptedException e) {
            de.future().completeExceptionally(e);
            return de.future();
        }
        CompletableFuture.runAsync(()->{
            try {
                String line;
                while(true){
                    boolean readded = false;
                    while(!stopProcess&&(line = reader.readLine()) != null){
                        responseQ.put(line);
                        readded = true;
                    }
                    if (readded){
                        responseQ.put("\n");
                        readded = false;
                    }
                    sleep(500);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        },  thread);
        CompletableFuture.runAsync(()->{
            try {
                String line;
                while ((line = readerError.readLine()) != null) {
                    errorQ.offer(line);
                }

            }catch (Exception e){
            }

        },  threadError);
        CompletableFuture.runAsync(()->{
            while(!stopProcess){
                try {
                    List<String> resultLines = new ArrayList<>();
                    String line = responseQ.take();
                    if ("\n".equals(line)){
                        continue;
                    }
                    if("(Pdb) ".equals(line)){
                        String l = responseQ.take();
                        while ("\n".equals(l)){
                            l = responseQ.take();
                        }
                        line = line + l;
                    }
                    while(true){
                        resultLines.add(line);
                        if(LineSegment.needSegment(line,responseQ.peek())){
                            break;
                        }
                        line = responseQ.take();
                    }
                    if (resultLines.size()==1&&resultLines.get(0).equals("(Pdb) ")){
                        continue;
                    }
                    Deserializer deserializer = deserializerQ.take();
                    System.out.println(resultLines);
                    CMDResp resp = deserializer.parse(resultLines);
                    if (!deserializer.future().isCompletedExceptionally()){
                        deserializer.future().complete(resp);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        },  threadRespDeserializer);
        CompletableFuture.runAsync(()->{
            try {
                while(!stopProcess){
                    String line = errorQ.take();
                    Deserializer deserializer = deserializerQ.take();
                    Exception exp = deserializer.parseError(line,errorQ);
                    deserializer.future().completeExceptionally(exp);
                }
            }catch (Exception e){
            }
        },  threadErrorDeserializer);
        return de.future();
    }

    void runAsync(String cmd){
        try {
            cmdPipe.write((cmd+"\n").getBytes());
            cmdPipe.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Process exec(Command cmd) throws IOException {

        return Runtime.getRuntime().exec(cmd.getCMDStr());
    }

    @Override
    public void close() {
        try{
            this.stopProcess = true;
            process.getOutputStream().close();
            normalResponse.close();
            process.getErrorStream().close();
        }catch (Exception e){
            //TODO:
        }
    }
}
