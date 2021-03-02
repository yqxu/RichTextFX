package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.util.Map;

public interface LineParser {
    Map<String,Object> parse(String str);
}
