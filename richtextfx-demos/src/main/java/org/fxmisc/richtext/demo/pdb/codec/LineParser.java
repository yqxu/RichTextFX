package org.fxmisc.richtext.demo.pdb.codec;

import org.fxmisc.richtext.demo.pdb.commands.Command;

import java.util.Map;

public interface LineParser {
    Object parse(String str);

    boolean match(String str);
}
