package org.fxmisc.richtext.demo.pdb.codec.segment;

import org.fxmisc.richtext.demo.pdb.codec.segment.LeafToken;
import org.fxmisc.richtext.demo.pdb.codec.segment.TempToken;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.codec.segment.TokenFactory;

import java.util.*;

/**
 * @author yq
 */
public class TokenSegment {

    private static Set<Character> tokenStart = new HashSet<>();
    static {
        tokenStart.add('{');
        tokenStart.add('(');
        tokenStart.add('[');
        tokenStart.add('<');
        tokenStart.add('\'');
        tokenStart.add('"');
    }
    private static Map<Character,Character> symbolMap = new HashMap<>();
    static {
        symbolMap.put('{','}');
        symbolMap.put('(',')');
        symbolMap.put('[',']');
        symbolMap.put('<','>');
        symbolMap.put('\'','\'');
        symbolMap.put('"','"');
    }
    private static Set<Character> tokenEnd = new HashSet<>();
    static {
        tokenStart.add('}');
        tokenStart.add(')');
        tokenStart.add(']');
        tokenStart.add('>');
    }
    private static Character seprator = ',';


    public static String test = "([\"'\", '\\'\\'\\'\"', 'a', {'fo': <__main__.Foo object at 0x10f6d9ee0>}, <__main__"
                                + ".Foo object at 0x10f6d9ee0>, <__main__.Foo object at 0x10f6d9d30>, \"'''\", "
                                + "'\\\\'], \"'\", '\\'\\'\\'\"')\n";
    public static List<Token> split(String str){
        StringBuilder buffer = new StringBuilder();
        str = str.trim();
        List<Token> tokens = new ArrayList<>();
        for (int i = 0 ; i <str.length() ;i++){
            char currentCharI = str.charAt(i);
            if (tokenStart.contains(currentCharI)){
                Token tempToken = TokenFactory.tokenInstance(currentCharI);
                String wrap = "" + currentCharI + symbolMap.get(currentCharI);
                tempToken.setWrapper(wrap);
                Character end = symbolMap.get(currentCharI);
                boolean needSplit = false;
                for (int j = i+1 ; j< str.length();j++){
                    char currentCharJ = str.charAt(j);
                    char currentCharBeforeJ = str.charAt(j-1);
                    if (end.equals(currentCharJ)&&'\\'!=currentCharBeforeJ){
                        if (seprator == currentCharJ){
                            needSplit = true;
                        }
                        i = j;
                        break;
                    }
                    buffer.append(currentCharJ);
                }
                TempToken tempToken1 = new TempToken();
                tempToken1.setDate(buffer.toString());
                tempToken1.setNeedSplit(needSplit);
                tempToken.addElement(tempToken1);
                tokens.add(tempToken);
                buffer = new StringBuilder();
            }else if (seprator.equals(str.charAt(i))){
                if (buffer.length()>0){
                    LeafToken leafToken = new LeafToken();
                    leafToken.setDate(buffer.toString());
                    tokens.add(leafToken);
                    buffer = new StringBuilder();
                }
            }else{
                buffer.append(str.charAt(i));
            }
        }

        return tokens;
    }

    public static  void main(String[] args){
        List<Token> token = split(test);
        System.out.println(token);
    }

}
