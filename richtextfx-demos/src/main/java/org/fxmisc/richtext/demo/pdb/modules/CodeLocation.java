package org.fxmisc.richtext.demo.pdb.modules;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;

public class CodeLocation {
    String filePath;
    int lineNumber;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }


}
