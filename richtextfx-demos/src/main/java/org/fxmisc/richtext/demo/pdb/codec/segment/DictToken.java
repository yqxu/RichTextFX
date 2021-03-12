package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yq
 */
public class DictToken implements Token<Map<String,Token>>{
    Map<String,Token> date = new HashMap<>();
    String wrapper = "{}";

    @Override public Map<String,Token> getDate() {
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
        String[] strs = token.getDate().split(":");
        String value = strs[1].trim();
        Token t = TokenFactory.tokenInstance(value);

        TempToken tempToken = new TempToken();
        tempToken.setDate(value);

        t.addElement(tempToken);
        date.put(strs[0].trim(),t);
    }
}
