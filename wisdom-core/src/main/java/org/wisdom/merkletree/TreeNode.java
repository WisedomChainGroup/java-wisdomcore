package org.wisdom.merkletree;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.keystore.crypto.SHA3Utility;

public class TreeNode {

    private TreeNode left;              //二叉树的左孩子
    private TreeNode right;             //二叉树的右孩子
    private String data;                //二叉树孩子节点的数据
    private String hash;                //二叉树孩子节点的数据对应的哈希值，此处采用SHA-256算法
    private String name;                //节点名称
    private Byte level;              //级别

    public TreeNode(){

    }

    public TreeNode(String data){
        this.data = data;
        this.hash = Hex.encodeHexString(SHA3Utility.keccak256(data.getBytes()));
        this.name = "[节点:"+ data + "]";
    }

    public TreeNode getLeft() {
        return left;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public TreeNode getRight() {
        return right;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getLevel() {
        return level;
    }

    public void setLevel(Byte level) {
        this.level = level;
    }
}
