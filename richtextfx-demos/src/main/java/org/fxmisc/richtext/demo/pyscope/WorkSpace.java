package org.fxmisc.richtext.demo.pyscope;

import org.fxmisc.richtext.demo.filesys.PyFile;

public class WorkSpace {
    public WorkSpace(){

    }
    public WorkSpace(String root){
        this.rootPath = root;
    }
    public WorkSpace(String root,String defaultOpenFile){
        this.rootPath = root;
        this.defaultOpenFile = new PyFile(defaultOpenFile);
    }
    String rootPath;
    PyFile defaultOpenFile;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String path) {
        this.rootPath = path;
    }

    public PyFile getDefaultOpenFile() {
        return defaultOpenFile;
    }

    public void setDefaultOpenFile(PyFile defaultOpenFile) {
        this.defaultOpenFile = defaultOpenFile;
    }
}
