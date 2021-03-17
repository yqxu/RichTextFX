package org.fxmisc.richtext.demo.pdb;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import org.fxmisc.richtext.demo.brackethighlighter.CustomCodeArea;
import org.fxmisc.richtext.demo.pdb.commands.CMDStartDebug;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PDBContext {
    public static PDBContext currentContext(){
        return debugContextMap.get(currentDebugPossessName);
    }
    public static String                               currentDebugPossessName = "default";
    public static ConcurrentHashMap<String,PDBContext> debugContextMap         = new ConcurrentHashMap<>();
    static {
        debugContextMap.put(currentDebugPossessName, new PDBContext());
    }
    public  volatile SimpleBooleanProperty isDebug = new SimpleBooleanProperty(false);
    public  volatile PDBShell               pdbShell ;
    public  volatile CustomCodeArea codeArea;

    public static PDBShell startDebug(String debugName,String location)  {
        PDBShell pdbShell = new PDBShell();
        pdbShell.setShell(new Shell());
        pdbShell.getShell().startWithCmd(CMDStartDebug.newInstance(location));
        PDBContext context = debugContextMap.get(debugName);
        if (context == null){
            context = new PDBContext();
            debugContextMap.put(debugName,context);
        }
        if (context.pdbShell!=null){
            context.pdbShell.close();
        }
        context.pdbShell = pdbShell;
        context.isDebug.set(true);
        return pdbShell;
    }

    public static PDBContext endDebug(String debugName)  {
        PDBContext context = debugContextMap.get(debugName);
        if (context == null){
            context = new PDBContext();
            debugContextMap.put(debugName,context);
        }
        if (context.pdbShell!=null){
            context.pdbShell.close();
            context.pdbShell = null;
        }
        context.isDebug.set(false);
        return context;
    }

}
