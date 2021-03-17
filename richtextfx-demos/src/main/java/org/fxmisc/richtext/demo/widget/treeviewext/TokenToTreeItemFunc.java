package org.fxmisc.richtext.demo.widget.treeviewext;

import javafx.scene.control.TreeItem;
import org.fxmisc.richtext.demo.pdb.codec.segment.*;
import org.reactfx.util.Tuple3;
import org.reactfx.util.Tuples;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yq
 */
public class TokenToTreeItemFunc implements Function<Tuple3<String,Token,String>, TreeItem> {

    @Override public TreeItem apply(Tuple3<String, Token ,String> stringTokenEntry) {
        Token token = stringTokenEntry.get2();
        String key = stringTokenEntry.get1();
        String parentKey = stringTokenEntry.get3();
        if (token instanceof LeafToken){
            TreeItem treeItem = new TreeItem();
            treeItem.setValue(key+":"+token.getDate());
            return treeItem;
        }
        if (token instanceof ArrayToken){
            TreeItem treeItem = new TreeItem();
            treeItem.setValue(key+":");
            List<TreeItem> list = ((ArrayToken)token).getDate().stream()
                                           .map(tokenChild-> Tuples.t("", tokenChild,parentKey))
                                           .map(tuple3 -> this.apply(tuple3)).collect(Collectors.toList());
            treeItem.getChildren().addAll(list);
            treeItem.setExpanded(false);
            return treeItem;
        }
        if (token instanceof DictToken){
            TreeItem treeItem = new TreeItem();
            treeItem.setValue(key+":");
            List<TreeItem> list = ((DictToken)token).getDate().entrySet().stream()
                                                     .map(entry-> Tuples.t(entry.getKey(), entry.getValue(),parentKey+"."+key))
                                                     .map(tuple3 -> this.apply(tuple3)).collect(Collectors.toList());
            treeItem.getChildren().addAll(list);
            treeItem.setExpanded(false);
            return treeItem;
        }
        if (token instanceof RefToken){
            TreeItem treeItem = new RefTokenItem(parentKey+"."+key);
            return treeItem;
        }
        System.out.println("not supported token ."+token.getWrapper()+"  class:"+token.getClass());
        return new TreeItem();
    }


}
