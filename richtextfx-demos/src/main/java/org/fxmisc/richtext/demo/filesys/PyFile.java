package org.fxmisc.richtext.demo.filesys;

import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;

import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yq
 */
public class PyFile {
    public PyFile(){}
    private String fileSep = "/";
    public PyFile(String absPath){
        this.absPath = absPath;
        this.fileName = absPath.substring(absPath.lastIndexOf(fileSep)+1);
    }
    String absPath;
    String fileName;

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


}
