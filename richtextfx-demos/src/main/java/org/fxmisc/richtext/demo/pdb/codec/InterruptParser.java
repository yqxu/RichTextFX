package org.fxmisc.richtext.demo.pdb.codec;

import java.util.HashMap;
import java.util.Map;

public class InterruptParser {

    static  String KeyboardInterrupt = "(Pdb) --KeyboardInterrupt--";
    static  String ModuleNotFoundError = "ModuleNotFoundError";

    public static String errorStr(String str){
        return str.substring(str.indexOf(":")+1);
    }
    
    public static boolean match(String str){
        return str.contains("--KeyboardInterrupt--")||str.equals("\n")||str.contains(ModuleNotFoundError);
    }
}
