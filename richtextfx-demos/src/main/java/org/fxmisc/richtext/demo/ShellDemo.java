package org.fxmisc.richtext.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShellDemo extends Application {

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

    @Override
    public void start(Stage primaryStage) {
        CodeArea codeArea = new CodeArea();
        codeArea.setContextMenu( new DefaultContextMenu() );
        codeArea.setStyle("-fx-background-color:#202020");

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {

            }
        });

        
        codeArea.replaceText(0, 0, sampleCode);
        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(codeArea)), 600, 400);
        scene.getStylesheets().add(JavaKeywordsAsyncDemo.class.getResource("java-keywords.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Demo");
//        Platform.runLater(()->codeArea.setTextInsertionStyle(Collections.singleton("normal")));
        primaryStage.show();
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
