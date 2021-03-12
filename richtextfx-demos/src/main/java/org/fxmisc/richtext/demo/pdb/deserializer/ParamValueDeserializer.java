package org.fxmisc.richtext.demo.pdb.deserializer;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.codec.segment.TokenSegment;
import org.fxmisc.richtext.demo.pdb.commands.CMDParam;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class ParamValueDeserializer implements Deserializer<CMDParam> {

    List<String> paramNames;
    CompletableFuture<CMDParam.ParamResp> future = new CompletableFuture<>();
    public ParamValueDeserializer(List<String> args){
        this.paramNames = args;
    }

    @Override public CMDResp<CMDParam> parse(List<String> lines) {
        String line = lines.get(0);
        if (lines.size()>1){
            line = lines.stream().reduce((str1,str2)->str1+str2).orElse("");
        }
        if (line.startsWith("(Pdb) ")){
            line = line.substring("(Pdb) ".length()).trim();
        }
        if (line.startsWith("***")){
            future.completeExceptionally(new RuntimeException(line));
            return null;
        }
        List<Token> token = TokenSegment.split(line);
        CMDParam.ParamResp resp = new CMDParam.ParamResp();
        resp.setData(paramNames,token.get(0));
        return resp;
    }

    @Override public Exception parseError(String str, BlockingQueue<String> errorQ) {
        return null;
    }

    @Override public CompletableFuture<? extends CMDResp<CMDParam>> future() {
        return future;
    }
}
