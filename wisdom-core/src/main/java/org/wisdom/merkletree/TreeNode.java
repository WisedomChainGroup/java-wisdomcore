/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.merkletree;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.keystore.crypto.SHA3Utility;

public class TreeNode {

    private TreeNode left;              //二叉树的左孩子
    private TreeNode right;             //二叉树的右孩子
    private String data;                //二叉树孩子节点的数据
    private String hash;                //二叉树孩子节点的数据对应的哈希值，此处采用SHA-256算法
    private String name;                //节点名称
    private Byte level;              //级别

    private int index;             // 叶子节点的位置，从0开始，其他节点是-1

    public TreeNode() {

    }

    public TreeNode(String data) {
        this.data = data;
        this.hash = Hex.encodeHexString(SHA3Utility.keccak256(data.getBytes()));
        this.name = "[节点:" + data + "]";
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static TreeNode copy(TreeNode treeNode) {
        TreeNode tn = new TreeNode();
        tn.setIndex(treeNode.getIndex());
        tn.setName(treeNode.getName());
        tn.setLevel(treeNode.getLevel());
        tn.setHash(treeNode.getHash());
        tn.setData(treeNode.getData());
        tn.setLeft(treeNode.getLeft());
        tn.setRight(treeNode.getRight());
        return tn;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "level=" + level +
                ", index=" + index +
                '}';
    }
}
