package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.List;

/**
 * @author yq
 */
public class RefToken implements Token<String>{
    String date;
    String wrapper = "<>";

    @Override public String getDate() {
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
        this.date = token.date;
    }
}
