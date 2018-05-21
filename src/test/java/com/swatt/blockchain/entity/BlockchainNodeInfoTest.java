package com.swatt.blockchain.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.swatt.blockchain.entity.BlockchainNodeInfo;

@RunWith(MockitoJUnitRunner.class)
public class BlockchainNodeInfoTest extends EntityTest {

    public BlockchainNodeInfoTest() {
        super(BlockchainNodeInfo.class);
    }

    @Test
    public void testConstructorGettersAndSetters() throws Exception {
        super.testConstructorGettersAndSetters();
    }
}
