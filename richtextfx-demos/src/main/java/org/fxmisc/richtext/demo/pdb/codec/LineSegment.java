package org.fxmisc.richtext.demo.pdb.codec;

public class LineSegment {
    public static boolean needSegment(String currentLine,String nextLine){
        return nextLine==null||nextLine.contains("(Pdb)")|| nextLine.equals("\n");
    }
}
