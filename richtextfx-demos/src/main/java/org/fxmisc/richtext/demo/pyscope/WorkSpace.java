package org.fxmisc.richtext.demo.pyscope;

import com.sun.javafx.collections.ImmutableObservableList;
import org.fxmisc.richtext.demo.filesys.LoadedPyFile;
import org.fxmisc.richtext.demo.filesys.PyFile;
import org.fxmisc.richtext.demo.filesys.PyFileReader;
import org.fxmisc.richtext.demo.framwork.IdeaFrame;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkSpace {
    private static volatile PyFile                   currentFile;
    private static          Map<String,LoadedPyFile> openedFiles = new ConcurrentHashMap<>();
    public static PyFile activedFile(){
        return currentFile;
    }

    public LoadedPyFile load(PyFile pyfile){
        LoadedPyFile loadedPyFile = new LoadedPyFile();
        loadedPyFile.setAbsPath(pyfile.getAbsPath());
        loadedPyFile.setFileName(pyfile.getFileName());
        Optional<List<String>> fileContent = PyFileReader.readFileAsCode(pyfile);
        fileContent.map((list)->{
            String str = list.stream().collect(Collectors.joining("\n"));
            ReadOnlyStyledDocument readOnlyStyledDocument = ReadOnlyStyledDocument.fromString(str, frame.getCodeArea().getParagraphStyleForInsertionAt(0), frame.getCodeArea().getTextStyleForInsertionAt(0), frame.getCodeArea().getSegOps());
            return readOnlyStyledDocument;
        }).ifPresent(doc -> loadedPyFile.setDocument(doc));
        return loadedPyFile;
    }

    public  void showFile(PyFile pyFile){
        LoadedPyFile loadedPyFile = openedFiles.get(pyFile.getAbsPath());
        if (loadedPyFile == null){
            loadedPyFile = load(pyFile);
            openedFiles.put(pyFile.getAbsPath(),loadedPyFile);
        }
        if(loadedPyFile.getDocument()!=null){
            frame.getCodeArea().showCode(loadedPyFile.getDocument());
            currentFile = pyFile;
        }

    }


    private IdeaFrame frame;

    public WorkSpace(String root,String defaultOpenFile,IdeaFrame frame){
        this.rootPath = root;
        this.defaultOpenFile = new PyFile(defaultOpenFile);
        this.frame = frame;
        WorkSpace.setCurrentFile(this.defaultOpenFile);
    }
    String rootPath;
    PyFile defaultOpenFile;


    public PyFile getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(PyFile currentFile) {
        WorkSpace.currentFile = currentFile;
    }

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
