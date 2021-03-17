package org.fxmisc.richtext.demo.widget.treeviewext;

import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;

import java.nio.Buffer;

/**
 * @author yq
 */
public abstract class LazyLoadTreeItem<K,T,V> extends TreeItem {
    K key;
    LazyLoadTreeItem(K key){
        super();
        this.key = key;
        TreeItem<Button> buttonTreeItem = new TreeItem<>();
        Button button = new Button("expand");
        buttonTreeItem.setValue(button);
        this.getChildren().add(buttonTreeItem);
        this.setExpanded(false);
        button.setOnMouseClicked(ME->{
            this.getChildren().remove(0);
            this.getChildren().addAll(apply(load(key)));
        });

    }
    abstract  T load(K key);
    abstract V apply(T loadValue);

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }
}
