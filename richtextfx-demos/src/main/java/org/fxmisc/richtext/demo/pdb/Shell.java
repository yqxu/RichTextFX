package org.fxmisc.richtext.demo.pdb;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineParser;
import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;



public class Shell implements Closeable{
    volatile boolean stopProcess = false;
    Process process;
    InputStream errorResponse;
    InputStream normalResponse;
    OutputStream cmdPipe;

    BlockingQueue<String> responseQ = new ArrayBlockingQueue<String>(30);
    BlockingQueue<String> errorQ = new ArrayBlockingQueue<String>(30);;
    BlockingQueue<Deserializer> deserializerQ  = new ArrayBlockingQueue<Deserializer>(60);;

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
        process = exec(cmd);
        cmdPipe = process.getOutputStream();
        normalResponse = process.getInputStream();
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
                while(!stopProcess&&(line = reader.readLine()) != null){
                    responseQ.offer(line);
                }

            }catch (Exception e){
            }

        },  thread);
        CompletableFuture.runAsync(()->{
            try {
                String line;
                while(!stopProcess){
                    while ((line = readerError.readLine()) != null) {
                        errorQ.offer(line);
                    }
                }

            }catch (Exception e){
            }

        },  threadError);
        CompletableFuture.runAsync(()->{
            while(!stopProcess){
                try {
                    String line = responseQ.take();
                    Deserializer deserializer = deserializerQ.take();
                    Queue<LineParser> parsers = deserializer.parsers();
                    if (parsers == null){
                        deserializer.future().complete(null);
                        continue;
                    }
                    Map<String,Object> resp = new HashMap<>();
                    do {
                        LineParser parser = parsers.poll();
                        if (parser!=null&&line!=null){
                            resp.putAll(parser.parse(line));
                        }
                        if (parsers.peek()!=null){
                            line = responseQ.poll(100,TimeUnit.MICROSECONDS);
                        }
                    }while(parsers.peek()!=null);
                    CMDResp re = deserializer.parse(resp);
                    deserializer.future().complete(re);
                }catch (Exception e){
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


    private static Process exec(Command cmd) {

        try {
            return Runtime.getRuntime().exec(cmd.getCMDStr());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        this.stopProcess = true;
        process.getOutputStream().close();
        process.getInputStream().close();
        process.getErrorStream().close();
    }
}
