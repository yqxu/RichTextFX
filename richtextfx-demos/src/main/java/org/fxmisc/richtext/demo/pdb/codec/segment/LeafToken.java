package org.fxmisc.richtext.demo.pdb.codec.segment;

public class LeafToken implements Token<String>{

    String wrapper;
    String date;

    @Override public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override public String getWrapper() {
        return wrapper;
    }

    @Override public void setWrapper(String str) {

    }

    @Override public Character endCharacter() {
        return null;
    }

    @Override public void addElement(TempToken token) {
        this.date = token.date;
    }
}
