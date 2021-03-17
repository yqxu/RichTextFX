package org.fxmisc.richtext.demo.pdb.commands;

import org.fxmisc.richtext.demo.pdb.CMDResp;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.codec.segment.UnionToken;
import org.fxmisc.richtext.demo.pdb.deserializer.ParamValueDeserializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return  new ParamValueDeserializer(this);
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
        Map<String,Token> date = new HashMap<>();

        public Map<String,Token> getDate() {
            return date;
        }

        public void setDate(Map<String,Token> date) {
            this.date = date;
        }

        public void setData(List<String> args,Token token) {
            if (args.size()>1){
                Map<String,Token> tokenMap = ((UnionToken)token).getMap(args);
                this.date = tokenMap;
            }else {
                date.put(args.get(0), token);
            }
        }
    }

}
