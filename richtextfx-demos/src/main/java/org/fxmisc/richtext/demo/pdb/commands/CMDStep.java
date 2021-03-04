package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineParser;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class CMDStep implements Command {

    @Override
    public String getCMDStr(String... args) {
        return "step";
    }

    @Override
    public StepDeserializer newDeserializer() {
        return  new StepDeserializer();
    }

    public static CMDStep newInstance(){
        CMDStep ret = new CMDStep();
        return ret;
    }


    public class StepResp implements CMDResp<CMDStep> {
        CodeLocation codeLocation;
        String codeStr;
        String info;
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

    public class StepDeserializer implements Deserializer<CMDStep>{
        private CompletableFuture<StepResp> future = new CompletableFuture<>();


        @Override public CMDResp<CMDStep> parse(List<String> lines) {
            CMDStep.StepResp resp = new CMDStep.StepResp();
            CodeLocationParser codeLocationParser = CodeLocationParser.newInstance();
            CodeStrParser codeStrParser = CodeStrParser.newInstance();
            lines.stream().findFirst();
            int infoEndLineNumber = 0;
            for (int i = 0; i < lines.size() ; i++) {
                if (codeLocationParser.match(lines.get(i))){
                    infoEndLineNumber = i;
                    break;
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
        public CompletableFuture<StepResp> future() {
            return future;
        }

    }
}
