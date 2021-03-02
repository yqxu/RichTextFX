package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.LineParser;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.LinkedList;
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

    public class StepDeserializer implements Deserializer<CMDStep>{
        private CompletableFuture<StepResp> future = new CompletableFuture<>();

        @Override
        public CMDResp<CMDStep> parse(Map<String, Object> parts) {
            StepResp stepResp = new StepResp();
            stepResp.setCodeLocation((CodeLocation) parts.get("codeLocation"));
            stepResp.setCodeStr((String) parts.get("codeStr"));
            return stepResp;
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

        @Override
        public Queue<LineParser> parsers() {
            Queue ret = new LinkedList();
            ret.offer(CodeLocationParser.newInstance());
            ret.offer(CodeStrParser.newInstance());
            return ret;
        }
    }
}
