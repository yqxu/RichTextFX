package org.fxmisc.richtext.demo.pdb.view;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.ToolBar;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.ParagraphBox;
import org.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;
import org.fxmisc.richtext.demo.filesys.PyFile;
import org.fxmisc.richtext.demo.framwork.IdeaFrame;
import org.fxmisc.richtext.demo.pdb.DebugEndException;
import org.fxmisc.richtext.demo.pdb.PDBContext;
import org.fxmisc.richtext.demo.pdb.PDBShell;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;
import org.fxmisc.richtext.model.Paragraph;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DebugToolBar extends ToolBar {


    CodeArea codeArea;
    private volatile Integer oldDebugLine = null;

    //暂时只支持一个default dubug 进程，没资源画debug 配置页面

    private void debugHighLight(CodeLocation codeLocation, CodeArea codeArea){
        if (!codeLocation.getFilePath().equals(IdeaFrame.currentWorkSpace().getCurrentFile().getAbsPath())){
            IdeaFrame.currentWorkSpace().showFile(new PyFile(codeLocation.getFilePath()));
        }
        Integer lineNumber = codeLocation.getLineNumber()-1;
        if (oldDebugLine!=null)
        {
            codeArea.getCell(oldDebugLine).removeStyleClass("debug-line");
        }
        if (lineNumber>=codeArea.getParagraphs().size()){
            oldDebugLine = null;
            return;
        }

        ParagraphBox cell = codeArea.getCell(lineNumber.intValue());
        //Bounds bounds = codeArea.getVisibleParagraphBoundsOnScreen(lineNumber.intValue());
        codeArea.showParagraphAtTop(lineNumber.intValue());
        oldDebugLine = lineNumber;
        cell.addStyleClass("debug-line");
        //codeArea.getParagraphs().set(lineNumber.intValue(),newCell);
    }

    public DebugToolBar(CustomCodeArea codeArea){
        super();
        this.codeArea = codeArea;
        Button      debug   = new Button("Debug");
        Button  step    = new Button("Step");
        Button  next    = new Button("Next");
        this.getItems().add(debug);
        this.getItems().add(step);
        this.getItems().add(next);
        debug.setOnMouseClicked( ME ->{
            if (PDBContext.debugContextMap.get(PDBContext.currentDebugPossessName ).pdbShell != null){
                PDBContext.debugContextMap.get(PDBContext.currentDebugPossessName).pdbShell.close();
            }
            PDBContext.startDebug(PDBContext.currentDebugPossessName,"test.py");
        });

        step.setOnMouseClicked( ME ->{
            PDBShell pdbShell = PDBContext.debugContextMap.get(PDBContext.currentDebugPossessName).pdbShell;
            if (pdbShell == null){
                PDBContext.startDebug(PDBContext.currentDebugPossessName,"test.py");
            }
            pdbShell.step().exceptionally(e->{
                if (e instanceof DebugEndException){
                    PDBContext.debugContextMap.get(PDBContext.currentDebugPossessName).isDebug.set(false);
                    pdbShell.close();
                    PDBContext.debugContextMap.remove(PDBContext.currentDebugPossessName);
                }
                return null;
            }).thenAccept(resp->{
                if (resp == null){
                    return;
                }
                debugHighLight(resp.getCodeLocation(),codeArea);
            });
        });

        next.setOnMouseClicked( ME ->{

            PDBShell pdbShell = PDBContext.debugContextMap.get(PDBContext.currentDebugPossessName).pdbShell;
            if (pdbShell == null){
                pdbShell = PDBContext.startDebug(PDBContext.currentDebugPossessName,"test.py");
            }
            pdbShell.next().exceptionally(e->{
                if (e instanceof DebugEndException){
                    PDBContext.endDebug(PDBContext.currentDebugPossessName);
                }
                return null;
            }).thenAccept(resp->{
                if (resp == null){
                    return;
                }
                debugHighLight(resp.getCodeLocation(),codeArea);
            });
        });
    }
}
