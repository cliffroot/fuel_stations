package com.tryp.support.data;

import io.realm.RealmObject;

/**
 * Created by cliffroot on 10.03.16.
 */
public class RealmPair extends RealmObject {

    private String left;
    private String right;

    public RealmPair () {}

    public RealmPair (String left, String right) {
        this.left   = left;
        this.right  = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
