package org.fxmisc.richtext.demo.pdb.view;

import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * @author yq
 */
public class VarPopStage extends Stage {
    TreeView treeView = new TreeView();

    public TreeView getTreeView() {
        return treeView;
    }

    public void setTreeView(TreeView treeView) {
        this.treeView = treeView;
    }

    public VarPopStage(){
        super();
        Scene scene = new Scene(treeView);
        this.setScene(scene);
    }
}
