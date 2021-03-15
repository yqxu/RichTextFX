package org.fxmisc.richtext.demo.menu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.demo.pdb.view.VarPopStage;

public class CodeAreaMenu extends ContextMenu {
    private MenuItem fold, unfold, print,varDetail;

    public CodeAreaMenu()
    {
        fold = new MenuItem( "Fold selected text" );
        fold.setOnAction( AE -> { hide(); fold(); } );

        unfold = new MenuItem( "Unfold from cursor" );
        unfold.setOnAction( AE -> { hide(); unfold(); } );

        print = new MenuItem( "Print" );
        print.setOnAction( AE -> { hide(); print(); } );

        varDetail = new MenuItem("Evaluate Expression");
        varDetail.setOnAction(AE -> {
            hide();
            Stage stage = new VarPopStage();
            stage.show();
            String arg = selectedContent();

        });
        getItems().addAll( fold, unfold, print,varDetail );
    }

    /**
     * Folds multiple lines of selected text, only showing the first line and hiding the rest.
     */
    private void fold() {
        ((CodeArea) getOwnerNode()).foldSelectedParagraphs();
    }

    /**
     * Unfold the CURRENT line/paragraph if it has a fold.
     */
    private void unfold() {
        CodeArea area = (CodeArea) getOwnerNode();
        area.unfoldParagraphs( area.getCurrentParagraph() );
    }

    private void print() {
        System.out.println( ((CodeArea) getOwnerNode()).getText() );
    }

    /**
     * Unfold the CURRENT line/paragraph if it has a fold.
     */
    private String selectedContent() {
        CodeArea area = (CodeArea) getOwnerNode();
        String selected = area.getSelectedText();
        return selected;
    }

}
