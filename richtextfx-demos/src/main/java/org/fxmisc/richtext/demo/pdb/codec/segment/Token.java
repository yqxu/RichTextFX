package org.fxmisc.richtext.demo.pdb.codec.segment;

public interface Token<T> {
    T getDate();
    String getWrapper();
    void setWrapper(String str);
    Character endCharacter();
    void addElement(TempToken token);
}
