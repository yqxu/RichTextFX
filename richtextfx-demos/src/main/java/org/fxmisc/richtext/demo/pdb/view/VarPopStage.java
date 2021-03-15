package org.fxmisc.richtext.demo.pdb.view;

import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/**
 * @author yq
 */
public class VarPopStage extends Stage {

    public VarPopStage(){
        super();
        TreeView treeView = new TreeView();
        Scene scene = new Scene(treeView);
    }
}
