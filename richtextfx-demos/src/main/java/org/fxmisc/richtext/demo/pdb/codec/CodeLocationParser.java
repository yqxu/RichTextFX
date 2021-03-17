package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.DebugEndException;
import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CodeLocationParser implements LineParser {

    public CodeLocationParser(CompletableFuture future){
        this.future = future;
    }
    private CompletableFuture future;

    //(Pdb) > /Users/yq/Documents/RichTextFX/test.py(2)<module>()
    @Override
    public CodeLocation parse(String str) throws DebugEndException {
        try{
            CodeLocation codeLocation = new CodeLocation();
            str = str.replaceAll("\\(Pdb\\) ","");
            if (str.startsWith("--Return--")){
                DebugEndException exp = new DebugEndException("Debug end.");
                throw exp;
            }
            int idxFileStart = str.indexOf("> ")+1;
            int idxFileEnd = str.indexOf("(",idxFileStart);
            int idxLineNumberStart = idxFileEnd + 1;
            int idxLineNumberEnd = str.indexOf(")",idxLineNumberStart);
            codeLocation.setFilePath(str.substring(idxFileStart,idxFileEnd));
            codeLocation.setLineNumber(Integer.parseInt(str.substring(idxLineNumberStart,idxLineNumberEnd)));
            return codeLocation;
        }catch (Exception e){
            return new CodeLocation();
        }

    }

    @Override public boolean match(String str) {
        return str.contains("> ");
    }

    public static CodeLocationParser newInstance(CompletableFuture future) {
         CodeLocationParser parser = new CodeLocationParser(future);
        return parser;
    }
}
