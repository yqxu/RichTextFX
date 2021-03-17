package org.fxmisc.richtext.demo.pdb.codec;

public class LineSegment {
    public static boolean needSegment(String nextLine){
        return nextLine.equals("(Pdb) ");
    }
}
