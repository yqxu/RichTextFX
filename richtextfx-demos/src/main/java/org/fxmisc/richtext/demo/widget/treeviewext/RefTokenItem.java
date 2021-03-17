package org.fxmisc.richtext.demo.widget.treeviewext;

import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.demo.pdb.PDBContext;
import org.fxmisc.richtext.demo.pdb.codec.segment.ArrayToken;
import org.fxmisc.richtext.demo.pdb.codec.segment.RefToken;
import org.fxmisc.richtext.demo.pdb.codec.segment.Token;
import org.fxmisc.richtext.demo.pdb.commands.CMDParam;
import org.reactfx.util.Tuples;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yq
 */
public class RefTokenItem extends LazyLoadTreeItem<String, Map<String,Token>,Collection<TreeItem>> {

    RefTokenItem(String key) {
        super(key);
    }

    @Override Map<String,Token> load(String key) {
        CMDParam.ParamResp resp = PDBContext.currentContext().pdbShell.detail(key).join();
        Map<String,Token> items = resp.getDate();
        return items;
    }

    @Override Collection<TreeItem> apply(Map<String,Token> loadValue) {
        return loadValue.entrySet().stream().map(stringTokenEntry -> {
            String childKey = stringTokenEntry.getKey();
            Token token = stringTokenEntry.getValue();
            TokenToTreeItemFunc func = new TokenToTreeItemFunc();
            TreeItem treeItem = func.apply(Tuples.t(childKey, token, key));
            return treeItem;
        }).collect(Collectors.toList());
    }

}
