package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.ArrayList;
import java.util.List;

public class ArrayToken implements Token<List<Token>>{
    List<Token> date = new ArrayList<>();
    String wrapper = "[]";

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
        List<Token> tokens = TokenSegment.split(token.getDate());
        this.date = tokens;
    }

}
