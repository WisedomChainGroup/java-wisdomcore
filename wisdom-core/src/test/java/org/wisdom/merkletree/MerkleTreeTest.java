package org.wisdom.merkletree;

import org.apache.commons.codec.DecoderException;
import org.junit.Assert;
import org.junit.Test;
import org.wisdom.merkletree.MerkleTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleTreeTest {

    @Test
    public void testMerkleTree() throws DecoderException {
//        // case1:List<String> contents = null;
//        List<String> contents = null;
//        Assert.assertEquals(new MerkleTree(contents).getList(),null);
//        Assert.assertEquals(new MerkleTree(contents).getRoot(),null);
//
//        // case2:List<String> contents = new ArrayList<>();
//        contents = new ArrayList<>();
//        Assert.assertEquals(new MerkleTree(contents).getList(),null);
//        Assert.assertEquals(new MerkleTree(contents).getRoot(),null);

        //case3:List<String> contents 有内容
        List<String> contents = Arrays.asList("TEST1","TEST2","TEST1","TEST2");
        Assert.assertEquals(new MerkleTree(contents).getRoot().getHash(),"1a1af435ee71e856d4d4c6d891a8dca47fa2933e2e8b54be983892b0a4bc23d6");
        //b73e3449ded67c74a73bf61eb621ebdf131b431588f67eeee33618857a466ee5
//        Assert.assertEquals(new MerkleTree(contents).getRoot().getName(),"(([节点:TEST1]和[节点:TEST2]的父节点)和([节点:TEST3]和[节点:TEST4]的父节点)的父节点)");
        MerkleTree a = new MerkleTree(contents);
    }
}
