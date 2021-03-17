package org.fxmisc.richtext.demo.menu;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.demo.pdb.PDBContext;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.commands.CMDParam;
import org.fxmisc.richtext.demo.pdb.view.VarPopStage;
import org.fxmisc.richtext.demo.widget.treeviewext.TokenToTreeItemFunc;
import org.reactfx.util.Tuples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            VarPopStage stage = new VarPopStage();
            String arg = selectedContent();
            if (!PDBContext.currentContext().isDebug.get()){
                return;
            }
            CMDParam.ParamResp resp = PDBContext.currentContext().pdbShell.args(arg).join();
            Map<String, Token> map = resp.getDate();
            TokenToTreeItemFunc func = new TokenToTreeItemFunc();
            List<TreeItem> list = map.entrySet().stream().map(entry -> func.apply(Tuples.t(entry.getKey(), entry.getValue(), ""))).collect(
                    Collectors.toList());
            TreeItem root = new TreeItem<>();
            root.getChildren().addAll(list);
            stage.getTreeView().setRoot(root);
            stage.show();
        });
        getItems().addAll( fold, unfold, print,varDetail );
        //PDBContext.currentContext().isDebug.addListener((isDebug,o,n)->{
        //    if(n == false){
        //        this.getItems().removeIf(menuItem -> menuItem.getText().equals("Evaluate Expression"));
        //    }else {
        //        this.getItems().add(3,varDetail);
        //    }
        //});
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
