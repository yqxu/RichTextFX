package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

/**
 * @author yq
 */
public class CMDParam implements Command {

    List<String> args;

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        if (args == null || args.size() <= 0 ){
            throw new RuntimeException("args can't be empty.");
        }
        this.args = args;
    }

    @Override
    public String getCMDStr(String... args) {
        if (args == null || args.length <= 0 ){
            throw new RuntimeException("args can't be empty.");
        }
        this.args = Arrays.asList(args.clone());
        String arg = this.args.stream().reduce((arg1,arg2)->arg1+","+arg2).orElse("");
        return "p " + arg;
    }

    @Override
    public ArgsDeserializer newDeserializer() {
        return  new ArgsDeserializer();
    }

    public static CMDParam newInstance(){
        CMDParam ret = new CMDParam();
        return ret;
    }


    public class ParamResp implements CMDResp<CMDParam> {
        Map<String,String> valueDisplayMap;
    }

    public class ArgsDeserializer implements Deserializer<CMDParam>{
        private CompletableFuture<ParamResp> future = new CompletableFuture<>();


        @Override public CMDResp<CMDParam> parse(List<String> lines) {
            ParamResp resp = new ParamResp();
            CodeLocationParser codeLocationParser = CodeLocationParser.newInstance();
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
            System.out.println(lines.get(infoEndLineNumber));
            CodeLocation codeLocation = codeLocationParser.parse(lines.get(infoEndLineNumber));
            String code =  codeStrParser.parse(lines.get(infoEndLineNumber+1));
            return resp;
        }

        @Override
        public Exception parseError(String str, BlockingQueue<String> errorQ) {
            while (errorQ.poll()!=null){
            }
            return new RuntimeException("start failed.");
        }

        @Override
        public CompletableFuture<ParamResp> future() {
            return future;
        }

    }
}
