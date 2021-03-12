package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.CodeLocationParser;
import org.fxmisc.richtext.demo.pdb.codec.CodeStrParser;
import org.fxmisc.richtext.demo.pdb.codec.Deserializer;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.codec.segment.UnionToken;
import org.fxmisc.richtext.demo.pdb.deserializer.ParamValueDeserializer;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;
import org.fxmisc.richtext.demo.pdb.modules.PyType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

/**
 * @author yq
 */
public class CMDParam implements Command {

    public CMDParam(String... args){
        this.args =  Arrays.asList(args.clone());
    }
    public CMDParam(){
    }
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
        if (this.args==null||this.args.size()<=0){
            if (args == null || args.length <= 0 ){
                throw new RuntimeException("args can't be empty.");
            }
            this.args = Arrays.asList(args.clone());
        }
        String arg = this.args.stream().reduce((arg1,arg2)->arg1+","+arg2).orElse("");
        return "p " + arg;
    }

    @Override
    public ParamValueDeserializer newDeserializer() {
        return  new ParamValueDeserializer(args);
    }

    public static CMDParam newInstance(){
        CMDParam ret = new CMDParam();
        return ret;
    }
    public static CMDParam newInstance(String... args){
        CMDParam ret = new CMDParam(args);
        return ret;
    }

    public static class ParamResp implements CMDResp<CMDParam> {
        Map<String,Token> data = new HashMap<>();


        public Map<String,Token> getData() {
            return data;
        }

        public void setData(Map<String,Token> data) {
            this.data = data;
        }

        public void setData(List<String> args,Token token) {
            if (args.size()>1){
                List<Token> tokenList = ((UnionToken)token).getDate();
                for (int i = 0; i < args.size(); i++) {
                    data.put(args.get(i),tokenList.get(i));
                }
            }else {
                data.put(args.get(0),token);
            }
        }
    }

}
