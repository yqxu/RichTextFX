package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.modules.CodeLocation;

import java.util.HashMap;
import java.util.Map;

public class CodeStrParser implements LineParser {

    @Override
    public String parse(String str) {
        int idx = str.indexOf("-> ") + "-> ".length();
        return str.substring(idx);
    }

    @Override public boolean match(String str) {
        return str.contains("-> ");
    }

    public static CodeStrParser newInstance() {
        CodeStrParser parser = new CodeStrParser();
        return parser;
    }
}
