package org.fxmisc.richtext.demo.pdb.codec.segment;

import java.util.HashMap;
import java.util.Map;

public class TokenFactory {
    public   static  Map<Character,Class<? extends Token>> tokenMap = new HashMap<>();
    static {
        tokenMap.put('{',DictToken.class);
        tokenMap.put('(',UnionToken.class);
        tokenMap.put('[',ArrayToken.class);
        tokenMap.put('<',RefToken.class);
        tokenMap.put('\'',LeafToken.class);
        tokenMap.put('"',LeafToken.class);
    }

    public static Token tokenInstance(Character c){
        Class<? extends Token> clazz = tokenMap.get(c);
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Token tokenInstance(String str){
        str = str.trim();
        Class<? extends Token> clazz = tokenMap.get(str.charAt(0));
        if (clazz == null){
            return new LeafToken();
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
