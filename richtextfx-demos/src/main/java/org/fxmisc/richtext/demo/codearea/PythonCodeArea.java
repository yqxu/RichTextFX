package org.fxmisc.richtext.demo.codearea;
import javafx.application.Platform;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.demo.brackethighlighter.BracketHighlighter;
import org.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;
import org.fxmisc.richtext.demo.menu.CodeAreaMenu;
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

public class PythonCodeArea extends CustomCodeArea {


    final Pattern whiteSpace = Pattern.compile( "^\\s+" );
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


    public PythonCodeArea(){
        super();
        BracketHighlighter bracketHighlighter = new BracketHighlighter(this);
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.setContextMenu( new CodeAreaMenu() );
        this.setStyle("-fx-background-color:#303030");
        this.getVisibleParagraphs().addModificationObserver(new VisibleParagraphStyler<>( this, this::computeHighlighting ));
        this.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if (KE.getCode() == KeyCode.BACK_SPACE){
                String str = this.getText(this.getCaretPosition()-3,this.getCaretPosition());
                if ("   ".equals(str)){
                    this.replaceText(this.getCaretPosition()-3,this.getCaretPosition(),"");
                }
            }
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = this.getCaretPosition();
                int currentParagraph = this.getCurrentParagraph();
                String text = this.getParagraph( currentParagraph-1 ).getSegments().get( 0 );
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
                Platform.runLater( () -> this.insertText( caretPosition, insertStr) );
            }
        });
    }


    public void showCode(String  debugCode) {
        // auto-indent: insert previous line's indents on enter
        this.replaceText(0, 0, debugCode);
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
