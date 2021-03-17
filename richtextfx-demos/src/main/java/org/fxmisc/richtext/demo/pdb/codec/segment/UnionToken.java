package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yq
 */
public class UnionToken implements Token<List<Token>>{
    List<Token> date;
    String     wrapper = "()";

    @Override public List<Token> getDate() {
        return date;
    }

    @Override public String getWrapper() {
        return wrapper;
    }

    @Override public void setWrapper(String str) {

    }

    @Override public Character endCharacter() {
        return wrapper.charAt(1);
    }

    @Override public void addElement(TempToken token) {
        List<Token> tokenList = TokenSegment.split(token.getDate());
        this.date = tokenList;
    }

    public Map<String,Token> getMap(List<String> keys){
        Map<String,Token> ret = new HashMap<>();
        for (int i = 0 ; i < keys.size(); i++){
            ret.put(keys.get(i),date.get(i));
        }
        return ret;
    }
}
