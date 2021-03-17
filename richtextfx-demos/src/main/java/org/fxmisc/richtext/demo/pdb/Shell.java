package org.fxmisc.richtext.demo.pdb;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineSegment;
import org.fxmisc.richtext.demo.pdb.commands.CMDStartDebug;
import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

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



    public synchronized  <T extends Command> CompletableFuture<? extends CMDResp<T>> executeCMD(T cmd) {
        Deserializer deserializer = cmd.newDeserializer();
        try {
            deserializerQ.put(deserializer);
        } catch (InterruptedException e) {
            deserializer.future().completeExceptionally(e);
            return deserializer.future();
        }
        return deserializer.future();
    }

    Executor thread = Executors.newSingleThreadExecutor();
    Executor threadError = Executors.newSingleThreadExecutor();
    Executor threadRespDeserializer = Executors.newSingleThreadExecutor();
    Executor threadErrorDeserializer = Executors.newSingleThreadExecutor();


    public  CompletableFuture<CMDResp>  startWithCmd(Command cmd) {
        try {
            File file = new File("log.txt");
            file.delete();
            process = exec(cmd);
        } catch (IOException e) {
            CompletableFuture<CMDResp>  fu = new CompletableFuture();
            fu.completeExceptionally(e);
            return fu;
        }
        cmdPipe = process.getOutputStream();
        try {
            File file = new File("log.txt");
            if (!file.exists()){
                file.createNewFile();
            }
            FileInputStream in = new FileInputStream(file);
            normalResponse = in;
        } catch (IOException e) {
            e.printStackTrace();
        }
        errorResponse = process.getErrorStream();
        InputStreamReader reader = new InputStreamReader(normalResponse);
        BufferedReader readerError = new BufferedReader(new InputStreamReader(errorResponse));
        StringBuilder ll = new StringBuilder();
        CompletableFuture.runAsync(()->{
            try {
                Character c;
                while(!stopProcess){
                    int i = reader.read();
                    if (i == -1 ){
                        if (ll.toString().equals("(Pdb) ")){
                            responseQ.put(ll.toString());
                            ll.delete(0,ll.length());
                        }
                        sleep(500);
                        continue;
                    }
                    c = (char)i;
                    if (c == '\n'){
                        responseQ.put(ll.toString());
                        ll.delete(0,ll.length());
                    }else{
                        ll.append(c);
                    }
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

        try {
            while(true){
                if(responseQ.take().contains("(Pdb)")){
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CompletableFuture.runAsync(()->{
            while(!stopProcess){
                try {
                    Deserializer deserializer = deserializerQ.take();
                    Command c = deserializer.getCMD();
                    runAsync(c.getCMDStr());
                    List<String> resultLines = new ArrayList<>();
                    while(true){
                        String line = responseQ.take();
                        if(LineSegment.needSegment(line)){
                            break;
                        }
                        resultLines.add(line);
                    }
                    if (resultLines.size()==1&&resultLines.get(0).equals("(Pdb) ")){
                        continue;
                    }
                    System.out.println(resultLines);
                    CMDResp resp;
                    try{
                        resp = deserializer.parse(resultLines);
                        deserializer.future().complete(resp);
                    }catch (Exception e){
                        deserializer.future().completeExceptionally(e);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        },  threadRespDeserializer);
        return CompletableFuture.completedFuture(new CMDStartDebug.StartDebugResp());
    }

    synchronized void runAsync(String cmd){
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
