package org.fxmisc.richtext.demo.framwork;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.richtext.demo.JavaKeywordsAsyncDemo;
import org.fxmisc.richtext.demo.codearea.PythonCodeArea;
import org.fxmisc.richtext.demo.filesys.PyFileReader;
import org.fxmisc.richtext.demo.pdb.view.DebugToolBar;
import org.fxmisc.richtext.demo.pyscope.WorkSpace;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IdeaFrame extends Application {
    public static WorkSpace currentWorkSpace(){
        return workSpace;
    }
    private static WorkSpace workSpace;
    private PythonCodeArea codeArea;
    private Stage primaryStage;
    private VBox vBox = new VBox();
    private HBox hBox = new HBox();
    public static void main(String[] args) {
        launch(args);
    }

    public PythonCodeArea getCodeArea() {
        return codeArea;
    }

    public void setCodeArea(PythonCodeArea codeArea) {
        this.codeArea = codeArea;
    }

    @Override
    public void start(Stage primaryStage) {
        workSpace = new WorkSpace("test.py","test.py",this);
        codeArea = new PythonCodeArea();
        this.primaryStage = primaryStage;
        workSpace.showFile(workSpace.getDefaultOpenFile());
        ToolBar toolBar = new DebugToolBar(codeArea);
        vBox.getChildren().add(toolBar);
        hBox.setPrefWidth(vBox.getPrefWidth());
        hBox.setFillHeight(true);
        StackPane pane = new StackPane(codeArea);
        TreeView treeView = new TreeView();
        treeView.setShowRoot(false);
        TreeItem root = new TreeItem();
        TreeItem<Label> treeItem = new TreeItem<Label>();
        treeItem.setValue(new Label("test"));
        TreeItem<Button> treeItem2 = new TreeItem<Button>();
        treeItem2.setValue(new Button("test2"));
        TreeItem<String> treeItem3 = new TreeItem<>();
        treeItem3.setValue("test3");
        root.getChildren().add(treeItem);
        root.getChildren().add(treeItem2);
        root.getChildren().add(treeItem3);
        treeView.setRoot(root);
        hBox.getChildren().add(treeView);
        hBox.getChildren().add(pane);
        vBox.getChildren().add(hBox);

        //stackPane.setStyle("height:100%");
        Scene scene = new Scene(vBox, 600, 400);
        codeArea.setPrefWidth(scene.getWidth());
        hBox.setPrefHeight(scene.getHeight());
        scene.getStylesheets().add(JavaKeywordsAsyncDemo.class.getResource("java-keywords.css").toExternalForm());
        scene.getStylesheets().add(JavaKeywordsAsyncDemo.class.getResource("idea-style-tool-bar.css").toExternalForm());
        scene.getStylesheets().add(JavaKeywordsAsyncDemo.class.getResource("tree-view.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Demo");
        Platform.runLater(()->codeArea.setTextInsertionStyle(Collections.singleton("normal")));

        //pane.setPadding(new Insets(11, 12, 13, 14));

        primaryStage.show();
        autoScale();
    }

    private void autoScale(){
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                codeArea.setPrefWidth((Double) newValue);
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                codeArea.setPrefHeight((Double) newValue);
                vBox.setPrefHeight((Double) newValue);
                hBox.setPrefHeight((Double) newValue);
            }
        });
    }
}
