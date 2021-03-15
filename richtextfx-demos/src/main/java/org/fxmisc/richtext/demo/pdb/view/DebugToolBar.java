package org.fxmisc.richtext.demo.pdb.view;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;
import org.fxmisc.richtext.demo.pdb.DebugEndException;
import org.fxmisc.richtext.demo.pdb.PDBContext;
import org.fxmisc.richtext.demo.pdb.PDBShell;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.concurrent.atomic.AtomicBoolean;

public class DebugToolBar extends ToolBar {

    PDBShell pdbShell = new PDBShell();
    CodeArea codeArea;
    private volatile int oldDebugLine = 0;

    private void debugHighLight(CodeLocation codeLocation, CodeArea codeArea){
        Integer lineNumber = codeLocation.getLineNumber()-1;
        codeArea.getCell(oldDebugLine).removeStyleClass("debug-line");
        oldDebugLine = lineNumber;
        codeArea.getCell(lineNumber.intValue()).addStyleClass("debug-line");
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
            if (pdbShell != null){
                pdbShell.close();
            }
            pdbShell = PDBShell.startDebug("test.py");
        });

        step.setOnMouseClicked( ME ->{
            if (pdbShell == null){
                pdbShell = PDBShell.startDebug("test.py");
                PDBContext.isDebug.set(true);
            }
            pdbShell.step().exceptionally(e->{
                if (e instanceof DebugEndException){
                    PDBContext.isDebug.set(false);
                    pdbShell.close();
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
            if (pdbShell == null){
                pdbShell = PDBShell.startDebug("test.py");
            }
            pdbShell.next().exceptionally(e->{
                if (e instanceof DebugEndException){
                    PDBContext.isDebug.set(false);
                    pdbShell.close();
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
