package org.ethereum.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class MockTest {
    private static final int NODES = 2;

    @Autowired
    ApplicationContext ctx;

    @Test
    public void multiNodeTest(){
        List<MockNode> nodes = new ArrayList<>();
        MockNode node1 = ctx.getBean("rdbmsMockNode", MockNode.class);
        node1.setName("node0");
        nodes.add(node1);
        for(int i = 1; i < NODES; i++){
            MockNode node = ctx.getBean("inmemoryDBMockNode", MockNode.class);
            node.setName("node"+i);
            nodes.add(node);
        }

        for(MockNode na: nodes){
            for(MockNode nb: nodes){
                if(na.getName().equals(nb.getName())){
                    continue;
                }
                na.addPeer(nb);
            }
        }


        // blocking here
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void singleNodeTest(){
        ctx.getBean(MockNode.class);
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
