package org.fxmisc.richtext.demo.pdb.modules;

import org.fxmisc.richtext.demo.pdb.codec.Deserializer;

public class CodeLocation {
    String filePath;
    long lineNumber;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }


}
