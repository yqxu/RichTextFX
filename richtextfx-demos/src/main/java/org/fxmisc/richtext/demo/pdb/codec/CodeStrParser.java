package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.HashMap;
import java.util.Map;

public class CodeStrParser implements LineParser {

    @Override
    public Map<String, Object> parse(String str) {
        Map<String,Object> ret = new HashMap<>();
        int idx = str.indexOf("-> ") + "-> ".length();
        ret.put("codeStr",str.substring(idx));
        return ret;
    }
    public static CodeStrParser newInstance() {
        CodeStrParser parser = new CodeStrParser();
        return parser;
    }
}
