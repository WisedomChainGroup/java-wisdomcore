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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.util.ByteUtil;

public class MerkleTree {

    private List<TreeNode> list;            //TreeNode List
    private TreeNode root;                  //根节点

    //构造函数
    public MerkleTree(List<String> contents) {
        createMerkleTree(contents);
    }

    //构建Merkle树
    private void createMerkleTree(List<String> contents) {
        //输入为空则不进行任何处理
        if (contents == null || contents.size() == 0) {
            return;
        }
        //初始化
        list = new ArrayList<>();
        //根据数据创建叶子节点
        List<TreeNode> leafList = createLeafList(contents);
        list.addAll(leafList);
        //创建父节点
        List<TreeNode> parents = createParentList(leafList);
        list.addAll(parents);
        //循环创建各级父节点直至根节点
        while (parents.size() > 1) {
            List<TreeNode> temp = createParentList(parents);
            list.addAll(temp);
            parents = temp;
        }
        root = parents.get(0);
    }

    //创建父节点列表
    private List<TreeNode> createParentList(List<TreeNode> leafList) {
        List<TreeNode> parents = new ArrayList<TreeNode>();
        //空检验
        if (leafList == null || leafList.size() == 0) {
            return parents;
        }

        int length = leafList.size();
        for (int i = 0; i < length - 1; i += 2) {
            TreeNode parent = createParentNode(leafList.get(i), leafList.get(i + 1));
            parents.add(parent);
        }

        //奇数个节点时，单独处理最后一个节点
        if (length % 2 != 0) {
            TreeNode parent = createParentNode(leafList.get(length - 1), null);
            parents.add(parent);
        }
        return parents;
    }

    //创建父节点
    private TreeNode createParentNode(TreeNode left, TreeNode right) {

        TreeNode parent = new TreeNode();
        parent.setLevel((byte) (left.getLevel() + 1));
        parent.setLeft(left);
        parent.setRight(right);
        //如果right为空，则父节点的哈希值为left的哈希值
        String hash = left.getHash();
        if (right != null) {
            hash = Hex.encodeHexString(SHA3Utility.keccak256(ByteUtil.prepend(ByteUtil.byteMerger(left.getHash().getBytes(), right.getHash().getBytes()), left.getLevel())));
        }else{
            hash = Hex.encodeHexString(SHA3Utility.keccak256(ByteUtil.prepend(ByteUtil.byteMerger(left.getHash().getBytes(), left.getHash().getBytes()), left.getLevel())));
        }
        //hash字段和data字段同值
        parent.setData(hash);
        parent.setHash(hash);

        if (right != null) {
            parent.setName("(" + left.getName() + "和" + right.getName() + "的父节点)");
        } else {
            parent.setName("(继承节点{" + left.getName() + "}成为父节点)");
        }
        return parent;
    }

    //构建叶子节点列表
    private List<TreeNode> createLeafList(List<String> contents) {
        List<TreeNode> leafList = new ArrayList<TreeNode>();

        //空检验
        if (contents == null || contents.size() == 0) {
            return leafList;
        }

        for (String content : contents) {
            TreeNode node = new TreeNode(content);
            node.setLevel((byte) 1);
            leafList.add(node);
        }
        return leafList;
    }

    //遍历树
    public void traverseTreeNodes() {
        Collections.reverse(list);
        TreeNode root = list.get(0);
        traverseTreeNodes(root);

    }

    private void traverseTreeNodes(TreeNode node) {
        if (node.getLeft() != null) {
            traverseTreeNodes(node.getLeft());
        }
        if (node.getRight() != null) {
            traverseTreeNodes(node.getRight());
        }
    }

    public List<TreeNode> getList() {
        if (list == null) {
            return list;
        }
        Collections.reverse(list);
        return list;
    }

    public void setList(List<TreeNode> list) {
        this.list = list;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public static void main(String[] args) {
        List<String> contents = Arrays.asList("TEST1");
        new MerkleTree(contents);
    }
}