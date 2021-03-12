package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.List;

public class TempToken implements Token<String>{
    String       wrapper;
    String       date;
    boolean      needSplit;
    @Override public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override public String getWrapper() {
        return wrapper;
    }

    public boolean isNeedSplit() {
        return needSplit;
    }

    public void setNeedSplit(boolean needSplit) {
        this.needSplit = needSplit;
    }

    @Override public void setWrapper(String wrapper) {
        this.wrapper = wrapper;
    }
    @Override public Character endCharacter() {
        return wrapper.charAt(1);
    }

    @Override public void addElement(TempToken token) {
        this.date = token.date;
    }
}
