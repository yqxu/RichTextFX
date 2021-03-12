package org.fxmisc.richtext.demo.pdb.modules;

import java.util.List;

/**
 * @author yq
 */
public class PyArray implements PyType {
    List<PyType> date;

    public List<PyType> getDate() {
        return date;
    }

    public void setDate(List<PyType> date) {
        this.date = date;
    }
}
