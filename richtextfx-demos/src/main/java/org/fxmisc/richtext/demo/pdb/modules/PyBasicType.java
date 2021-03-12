package org.fxmisc.richtext.demo.pdb.modules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PyBasicType implements PyType {
    public PyBasicType(String date){
        disPlayStr = date;
    }

    String disPlayStr;

    public static Set<String> basicTypes = new HashSet<>();
    {
        basicTypes.add("int");
        basicTypes.add("float");
    }

    public String getDisPlayStr() {
        return disPlayStr;
    }

    public void setDisPlayStr(String disPlayStr) {
        this.disPlayStr = disPlayStr;
    }

    public static Set<String> getBasicTypes() {
        return basicTypes;
    }

    public static void setBasicTypes(Set<String> basicTypes) {
        PyBasicType.basicTypes = basicTypes;
    }
}
