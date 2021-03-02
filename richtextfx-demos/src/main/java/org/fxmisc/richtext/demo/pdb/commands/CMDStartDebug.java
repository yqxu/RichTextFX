package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineParser;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class CMDStartDebug implements Command {

    public static String defaultDebugCmd = "python3.9 -m pdb ";

    String fileLocation;

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    @Override
    public String getCMDStr(String... args) {
        return defaultDebugCmd+fileLocation;
    }

    @Override
    public StartDebugDeserializer newDeserializer() {
        return  new StartDebugDeserializer();
    }

    public static CMDStartDebug newInstance(String fileLocation){
        CMDStartDebug ret = new CMDStartDebug();
        ret.setFileLocation(fileLocation);
        return ret;
    }

    public class StartDebugResp implements CMDResp<CMDStartDebug> {
        CodeLocation codeLocation;
        String codeStr;

        public CodeLocation getCodeLocation() {
            return codeLocation;
        }

        public void setCodeLocation(CodeLocation codeLocation) {
            this.codeLocation = codeLocation;
        }

        public String getCodeStr() {
            return codeStr;
        }

        public void setCodeStr(String codeStr) {
            this.codeStr = codeStr;
        }
    }

    public class StartDebugDeserializer implements Deserializer<CMDStartDebug>{
        private CompletableFuture<StartDebugResp> future = new CompletableFuture<>();

        @Override
        public CMDResp<CMDStartDebug> parse(Map<String, Object> parts) {
            StartDebugResp startDebugResp = new StartDebugResp();
            startDebugResp.setCodeLocation((CodeLocation) parts.get("codeLocation"));
            startDebugResp.setCodeStr((String) parts.get("codeStr"));
            return startDebugResp;
        }

        @Override
        public Exception parseError(String str, BlockingQueue<String> errorQ) {
            while (errorQ.poll()!=null){
            }
            return new RuntimeException("start failed.");
        }

        @Override
        public CompletableFuture<StartDebugResp> future() {
            return future;
        }

        @Override
        public Queue<LineParser> parsers() {
            Queue ret = new LinkedList();
            ret.offer(CodeLocationParser.newInstance());
            ret.offer(CodeStrParser.newInstance());
            return ret;
        }
    }
}
