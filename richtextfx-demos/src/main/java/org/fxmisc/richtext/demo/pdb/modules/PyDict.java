package org.fxmisc.richtext.demo.pdb.modules;

import java.util.Map;

public class PyDict implements PyType {
    Map<String,PyType> date;

    public Map<String, PyType> getDate() {
        return date;
    }

    public void setDate(Map<String, PyType> date) {
        this.date = date;
    }
}
