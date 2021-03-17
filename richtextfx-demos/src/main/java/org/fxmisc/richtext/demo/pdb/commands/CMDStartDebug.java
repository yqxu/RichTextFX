package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.*;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class CMDStartDebug implements Command {

    public static String defaultDebugCmd = "python3.9 gdpdb.py ";

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
        return  new StartDebugDeserializer(this);
    }

    public static CMDStartDebug newInstance(String fileLocation){
        CMDStartDebug ret = new CMDStartDebug();
        ret.setFileLocation(fileLocation);
        return ret;
    }

    public static class StartDebugResp implements CMDResp<CMDStartDebug> {
        String info;
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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public class StartDebugDeserializer implements Deserializer<CMDStartDebug>{
        StartDebugDeserializer(CMDStartDebug cmd){
            this.cmd = cmd;
        }
        private CompletableFuture<StartDebugResp> future = new CompletableFuture<>();
        private CMDStartDebug cmd;

        @Override public CMDResp<CMDStartDebug> parse(List<String> lines) {
            StartDebugResp resp = new StartDebugResp();
            CodeLocationParser codeLocationParser = CodeLocationParser.newInstance(future);
            CodeStrParser codeStrParser = CodeStrParser.newInstance();
            lines.stream().findFirst();
            int infoEndLineNumber = 0;
            if (lines.size() > 2){
                for (int i = 0; i < lines.size() ; i++) {
                    if (codeLocationParser.match(lines.get(i))){
                        infoEndLineNumber = i;
                        break;
                    }
                }
            }
            StringBuilder info = new StringBuilder();
            for (int i = 0; i < infoEndLineNumber; i++ ){
                info.append(lines.get(i));
                info.append("\n");
            }
            resp.setInfo(info.toString());
            CodeLocation codeLocation = codeLocationParser.parse(lines.get(infoEndLineNumber));
            String code =  codeStrParser.parse(lines.get(infoEndLineNumber+1));
            resp.setCodeStr(code);
            resp.setCodeLocation(codeLocation);
            return resp;
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

        @Override public CMDStartDebug getCMD() {
            return cmd;
        }

    }
}
