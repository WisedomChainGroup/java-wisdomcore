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

import java.util.*;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.util.ByteUtil;

public class MerkleTree {

    private TreeNode root;                  //根节点
    private HashMap<Byte, List<TreeNode>> map; // 记录 TreeNode

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
        map = new HashMap<>();
        //根据数据创建叶子节点
        List<TreeNode> leafList = createLeafList(contents);
        map.put(leafList.get(0).getLevel(), leafList);
        //创建父节点
        List<TreeNode> parents = createParentList(leafList);
        map.put(parents.get(0).getLevel(), parents);
        //循环创建各级父节点直至根节点
        while (parents.size() > 1) {
            List<TreeNode> temp = createParentList(parents);
            map.put(temp.get(0).getLevel(), temp);
            parents = temp;
        }
        root = parents.get(0);
    }

    //创建父节点列表
    private List<TreeNode> createParentList(List<TreeNode> leafList) {
        List<TreeNode> parents = new ArrayList<>();
        //空检验
        if (leafList == null || leafList.size() == 0) {
            return parents;
        }

        int length = leafList.size();
        for (int i = 0; i < length - 1; i += 2) {
            TreeNode parent = createParentNode(leafList.get(i), leafList.get(i + 1));
            parent.setIndex(i / 2);
            parents.add(parent);
        }

        //奇数个节点时，单独处理最后一个节点
        if (length % 2 != 0) {
            TreeNode parent = createParentNode(leafList.get(length - 1), null);
            if (parents.size() == 0){
                parent.setIndex(0);
            }else {
                parent.setIndex( parents.get(parents.size() - 1).getIndex() + 1);
            }
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
        } else {
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

        for (int i = 0; i < contents.size(); i++) {
            TreeNode node = new TreeNode(contents.get(i));
            node.setLevel((byte) 1);
            node.setIndex(i);
            leafList.add(node);
        }
        return leafList;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<TreeNode> getLevelList(Byte level) {
        return map.get(level);
    }

    public int getLevelSize() {
        return map.size();
    }

//    public static void main(String[] args) {
//        List<String> hashes = new ArrayList<>();
//        for (Transaction tx : createTransactionList()) {
//            hashes.add(tx.getHashHexString());
//        }
//        Byte a = 14;
//        List<TreeNode> list = new MerkleTree(hashes).getLevelList(a);
//        System.out.println("---------------------size----------" + list.size());
//        for (TreeNode node : list
//        ) {
//            System.out.println("---------------------1-----------" + node.getIndex());
//        }
//    }

//    private static List<Transaction> createTransactionList() {
//        List<Transaction> txs = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            Transaction tx = Transaction.createEmpty();
//            tx.type = Transaction.Type.TRANSFER.ordinal();
//            tx.amount = 1;
//            tx.nonce = 1;
//            tx.gasPrice = 1;
//            tx.payload = "111".getBytes();
//            txs.add(tx.setHashHexString(i + ""));
//        }
//        return txs;
//    }

}
