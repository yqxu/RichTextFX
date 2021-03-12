package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.List;

/**
 * @author yq
 */
public class UnionToken implements Token<List<Token>>{
    List<Token> date;
    String wrapper = "()";

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
        this.date = TokenSegment.split(token.getDate());
    }
}
