package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.HashMap;
import java.util.Map;

public class CodeLocationParser implements LineParser {

    //(Pdb) > /Users/yq/Documents/RichTextFX/test.py(2)<module>()
    @Override
    public CodeLocation parse(String str) {
        CodeLocation codeLocation = new CodeLocation();
        int idxFileStart = str.indexOf("> ")+1;
        int idxFileEnd = str.indexOf("(",idxFileStart);
        int idxLineNumberStart = idxFileEnd + 1;
        int idxLineNumberEnd = str.indexOf(")",idxLineNumberStart);
        codeLocation.setFilePath(str.substring(idxFileStart,idxFileEnd));
        codeLocation.setLineNumber(Long.parseLong(str.substring(idxLineNumberStart,idxLineNumberEnd)));
        return codeLocation;
    }

    @Override public boolean match(String str) {
        return str.contains("> ");
    }

    public static CodeLocationParser newInstance() {
         CodeLocationParser parser = new CodeLocationParser();
        return parser;
    }
}
