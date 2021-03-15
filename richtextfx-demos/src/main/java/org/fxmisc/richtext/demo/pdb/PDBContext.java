package org.fxmisc.richtext.demo.pdb;

import org.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class PDBContext {
    public static volatile AtomicBoolean isDebug = new AtomicBoolean(false);
    public static volatile PDBShell pdbShell;
    public static volatile CustomCodeArea codeArea;

}
