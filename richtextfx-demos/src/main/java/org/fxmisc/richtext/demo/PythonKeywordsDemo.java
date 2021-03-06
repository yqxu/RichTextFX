package org.fxmisc.richtext.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.demo.pdb.PDBShell;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonKeywordsDemo extends Application {


    PDBShell pdbShell ;
    private static final String[] KEYWORDS = new String[] {
            "and", "as", "assert", "break", "class",
            "continue", "def", "del", "elif", "else", "except",
            "exec", "finally", "for", "from", "global", "if",
            "import", "in", "is", "lambda", "not", "or", "pass",
            "print", "raise", "return", "try", "while", "with",
            "yield","False", "True", "None", "NotImplemented",
            "Ellipsis"
    };

    private static final String[] BUILTINS = new String[] {"abs", "all", "any", "basestring", "bool",
            "callable", "chr", "classmethod", "cmp", "compile",
            "complex", "delattr", "dict", "dir", "divmod",
            "enumerate", "eval", "execfile", "exit", "file",
            "filter", "float", "frozenset", "getattr", "globals",
            "hasattr", "hex", "id", "int", "isinstance",
            "issubclass", "iter", "len", "list", "locals", "map",
            "max", "min", "object", "oct", "open", "ord", "pow",
            "property", "range", "reduce", "repr", "reversed",
            "round", "set", "setattr", "slice", "sorted",
            "staticmethod", "str", "sum", "super", "tuple", "type",
            "vars", "zip"
    };

    private static final String[] INDENT = new String[] {"if","else", "elif","while" , "for","def" ,"\\(","\\[","\\{"};
    private static final String[] UN_INDENT = new String[] {"\\)", "\\}","\\]"};

    private static final String INDENT_PATTERN =  "\\b(" + String.join("|", INDENT) + ")\\b"+"|"+"\\("+"|"+"\\["+"|"+"\\{";
    private static final String UN_INDENT_PATTERN = String.join("|", UN_INDENT);


    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String BUILTINS_PATTERN = "\\b(" + String.join("|", BUILTINS) + ")\\b";

    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|\"|'''([^'])*'''|'([^'])*'|'''|''|'";
    private static final String COMMENT_PATTERN = "#[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
    		                          + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static String sampleCode =
            "#!/Users/yq/PycharmProjects/pythonProject/venv/bin/python\n" +
            "# -*- coding: utf-8 -*-\n" +
            "import re\n" +
            "import sys\n" +
            "from pip._internal.cli.main import main\n" +
            "if __name__ == '__main__':\n" +
            "    sys.argv[0] = re.sub(r'(-script\\.pyw|\\.exe)?$', '', sys.argv[0])\n" +
            "    sys.exit(main())\n";

    private static String debugCode = "from ast import *\n" + "# This is a sample Python script.\n" + "\n"
                                      + "# Press ⌃R to execute it or replace it with your code.\n"
                                      + "# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.\n"
                                      + "\n" + "\n" + "\n"
                                      + "# See PyCharm help at https://www.jetbrains.com/help/pycharm/\n"
                                      + "script = '''\n" + "\n" + "def print_hi(name):\n"
                                      + "    # Use a breakpoint in the code line below to debug your script.\n"
                                      + "    print(f'Hi, {name}')  # Press ⌘F8 to toggle the breakpoint.\n" + "\n"
                                      + "\n" + "# Press the green button in the gutter to run the script.\n"
                                      + "if __name__ == '__main__':\n" + "    print_hi('PyCharm')\n"
                                      + "print(\"aaa test\")\n" + "'''\n" + "\n" + "astwitherror = '''\n"
                                      + "def print_hi(name):\n" + "    p\n" + "'''\n" + "\n" + "\n"
                                      + "ast = parse(script)\n" + "\n" + "print(unparse(ast))\n" + "\n";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BUILTINS>" + BUILTINS_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static final Pattern PATTERN_INDENT = Pattern.compile(
            "(?<INDENT>" + INDENT_PATTERN + ")"
            + "|(?<UNINDENT>" + UN_INDENT_PATTERN + ")"
    );

    public static void main(String[] args) {
        launch(args);
    }


    private volatile int oldDebugLine = 0;
    private void debugHighLight(CodeLocation codeLocation,CodeArea codeArea){
        Integer lineNumber = codeLocation.getLineNumber()-1;
        codeArea.getCell(oldDebugLine).removeStyleClass("debug-line");
        oldDebugLine = lineNumber;
        codeArea.getCell(lineNumber.intValue()).addStyleClass("debug-line");
    }

    @Override
    public void start(Stage primaryStage) {
        Button debug = new Button("Debug");
        Button step = new Button("Step");
        Button next = new Button("Next");
        ToolBar toolBar = new ToolBar(
                new Button("New"),
                new Button("Open"),
                new Button("Save"),
                new Separator(),
                new Button("Clean"),
                new Button("Compile"),
                new Button("Run"),
                new Separator(),
                debug,
                step,
                next
        );

        CodeArea codeArea = new CodeArea();
        debug.setOnMouseClicked( ME ->{
            if (pdbShell != null){
                pdbShell.close();
            }
            pdbShell = PDBShell.startDebug("test.py");
        });

        step.setOnMouseClicked( ME ->{
            if (pdbShell == null){
                pdbShell = PDBShell.startDebug("test.py");
            }
            pdbShell.step().thenAccept(resp->debugHighLight(resp.getCodeLocation(),codeArea));
        });

        next.setOnMouseClicked( ME ->{
            if (pdbShell == null){
                pdbShell = PDBShell.startDebug("test.py");
            }
            pdbShell.next().thenAccept(resp->debugHighLight(resp.getCodeLocation(),codeArea));
        });
//        codeArea.setStyleSpans();
        // add line numbers to the left of area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setContextMenu( new DefaultContextMenu() );
        codeArea.setStyle("-fx-background-color:#303030");
/*
        // recompute the syntax highlighting for all text, 500 ms after user stops editing area
        // Note that this shows how it can be done but is not recommended for production with
        // large files as it does a full scan of ALL the text every time there is a change !
        Subscription cleanupWhenNoLongerNeedIt = codeArea

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        // when no longer need syntax highlighting and wish to clean up memory leaks
        // run: `cleanupWhenNoLongerNeedIt.unsubscribe();`
*/
        // recompute syntax highlighting only for visible paragraph changes
        // Note that this shows how it can be done but is not recommended for production where multi-
        // line syntax requirements are needed, like comment blocks without a leading * on each line. 
        codeArea.getVisibleParagraphs().addModificationObserver
        (
            new VisibleParagraphStyler<>( codeArea, this::computeHighlighting )
        );

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if (KE.getCode() == KeyCode.BACK_SPACE){
                String str = codeArea.getText(codeArea.getCaretPosition()-3,codeArea.getCaretPosition());
                if ("   ".equals(str)){
                    codeArea.replaceText(codeArea.getCaretPosition()-3,codeArea.getCaretPosition(),"");
                }
            }
            if ( KE.getCode() == KeyCode.ENTER ) {
            	int caretPosition = codeArea.getCaretPosition();
            	int currentParagraph = codeArea.getCurrentParagraph();
                String text = codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 );
                Matcher m0 = whiteSpace.matcher( text );
                Matcher matcher = PATTERN_INDENT.matcher(text);
                int count = 0;
                while(matcher.find()) {
                    String kind =
                            matcher.group("INDENT") != null ? "INDENT" :
                            matcher.group("UNINDENT")!= null ? "UNINDENT":
                            null; /* never happens */
                    switch (kind){
                        case "INDENT": count++;continue;
                        case "UNINDENT" : count--;continue;
                        default:break;
                    }
                }
                String insertText = "";
                if ( m0.find()){
                    insertText = m0.group();
                }
                while (count>0 ){
                    insertText = insertText + "    ";
                    count --;
                }
                while(count<0 && insertText.length()>0){
                    char c = insertText.charAt(insertText.length()-1);
                    switch (c){
                        case '\t': insertText = insertText.substring(0,insertText.length()-1);break;
                        case ' ' : insertText = insertText.substring(0,insertText.length()-4);break;
                    }
                    count ++ ;
                }
                final String insertStr = insertText;
                Platform.runLater( () -> codeArea.insertText( caretPosition, insertStr) );

            }
        });

        
        codeArea.replaceText(0, 0, debugCode);
        //StackPane stackPane =  new StackPane(new VirtualizedScrollPane<>(codeArea));
        VBox vBox = new VBox();
        vBox.getChildren().add(toolBar);
        HBox hBox = new HBox();
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
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {//监听窗口的宽
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                codeArea.setPrefWidth((Double) newValue);
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {//监听窗口的高
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                codeArea.setPrefHeight((Double) newValue);
                vBox.setPrefHeight((Double) newValue);
                hBox.setPrefHeight((Double) newValue);
            }
        });
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("BUILTINS")!= null ? "builtins":
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            if (styleClass!=null){
                spansBuilder.add(Collections.singleton("normal"), matcher.start() - lastKwEnd);
            }
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("normal"),   text.length()-lastKwEnd);
        return spansBuilder.create();
    }

    private class VisibleParagraphStyler<PS, SEG, S> implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>>
    {
        private final GenericStyledArea<PS, SEG, S> area;
        private final Function<String,StyleSpans<S>> computeStyles;
        private int prevParagraph, prevTextLength;

        public VisibleParagraphStyler( GenericStyledArea<PS, SEG, S> area, Function<String,StyleSpans<S>> computeStyles )
        {
            this.computeStyles = computeStyles;
            this.area = area;
        }

        @Override
        public void accept( ListModification<? extends Paragraph<PS, SEG, S>> lm )
        {
            if ( lm.getAddedSize() > 0 )
            {
                int paragraph = Math.min( area.firstVisibleParToAllParIndex() + lm.getFrom(), area.getParagraphs().size()-1 );
                String text = area.getText( paragraph, 0, paragraph, area.getParagraphLength( paragraph ) );

        	    if ( paragraph != prevParagraph || text.length() != prevTextLength )
        	    {
                    int startPos = area.getAbsolutePosition( paragraph, 0 );
                    Platform.runLater( () -> area.setStyleSpans( startPos, computeStyles.apply( text ) ) );
                    prevTextLength = text.length();
                    prevParagraph = paragraph;
        	    }
        	}
        }
    }

    private class DefaultContextMenu extends ContextMenu
    {
        private MenuItem fold, unfold, print;

        public DefaultContextMenu()
        {
            fold = new MenuItem( "Fold selected text" );
            fold.setOnAction( AE -> { hide(); fold(); } );

            unfold = new MenuItem( "Unfold from cursor" );
            unfold.setOnAction( AE -> { hide(); unfold(); } );

            print = new MenuItem( "Print" );
            print.setOnAction( AE -> { hide(); print(); } );

            getItems().addAll( fold, unfold, print );
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
    }


    private  int getIndentationCount(String spaces) {
        int count = 0;
        for (int i = 0; i < spaces.length(); i++) {
            char ch = spaces.charAt(i);
            if (ch == '\t') {
                count += (4 - (count % 4));
            } else if (ch == ' ') {
                count += 1;
            }
        }
        return count;
    }
}
