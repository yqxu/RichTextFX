package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.HashMap;
import java.util.Map;

public class CodeLocationParser implements LineParser {

    //(Pdb) > /Users/yq/Documents/RichTextFX/test.py(2)<module>()
    @Override
    public Map<String, Object> parse(String str) {
        Map<String,Object> ret = new HashMap<>();
        CodeLocation codeLocation = new CodeLocation();
        int idxFileStart = str.indexOf("> ")+1;
        int idxFileEnd = str.indexOf("(",idxFileStart);
        int idxLineNumberStart = idxFileEnd + 1;
        int idxLineNumberEnd = str.indexOf(")",idxLineNumberStart);
        codeLocation.setFilePath(str.substring(idxFileStart,idxFileEnd));
        codeLocation.setLineNumber(Long.parseLong(str.substring(idxLineNumberStart,idxLineNumberEnd)));
        ret.put("codeLocation",codeLocation);
        return ret;
    }

    public static CodeLocationParser newInstance() {
         CodeLocationParser parser = new CodeLocationParser();
        return parser;
    }
}
