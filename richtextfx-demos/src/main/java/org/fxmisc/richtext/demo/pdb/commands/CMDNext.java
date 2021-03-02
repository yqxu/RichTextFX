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

public class CMDNext implements Command {

    @Override
    public String getCMDStr(String... args) {
        return "next";
    }

    @Override
    public NextDeserializer newDeserializer() {
        return  new NextDeserializer();
    }

    public static CMDNext newInstance(){
        CMDNext ret = new CMDNext();
        return ret;
    }


    public class NextResp implements CMDResp<CMDNext> {
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

    public class NextDeserializer implements Deserializer<CMDNext>{
        private CompletableFuture<NextResp> future = new CompletableFuture<>();

        @Override
        public CMDResp<CMDNext> parse(Map<String, Object> parts) {
            NextResp resp = new NextResp();
            resp.setCodeLocation((CodeLocation) parts.get("codeLocation"));
            resp.setCodeStr((String) parts.get("codeStr"));
            return resp;
        }

        @Override
        public Exception parseError(String str, BlockingQueue<String> errorQ) {
            while (errorQ.poll()!=null){
            }
            return new RuntimeException("start failed.");
        }

        @Override
        public CompletableFuture<NextResp> future() {
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
