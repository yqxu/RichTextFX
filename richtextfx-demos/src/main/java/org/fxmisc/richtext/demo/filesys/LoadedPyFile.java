package org.fxmisc.richtext.demo.filesys;

import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;

/**
 * @author yq
 */
public class LoadedPyFile extends PyFile {
    StyledDocument document;

    public StyledDocument getDocument() {
        return document;
    }

    public void setDocument(StyledDocument document) {
        this.document = document;
    }


}
