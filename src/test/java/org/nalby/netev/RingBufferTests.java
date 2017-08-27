package org.nalby.netev;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

public class RingBufferTests {
	
	@Test
	public void testNormalPositions() {
		RingBuffer ringBuffer = new RingBuffer(3);
		byte[] tmp = new byte[3];
		assertTrue(ringBuffer.append(tmp, 0, 1) && ringBuffer.availableSize() == 2);
		assertTrue(ringBuffer.append(tmp, 1, 2) && ringBuffer.availableSize() == 0);
		assertFalse(ringBuffer.append(tmp, 0, 1));
	}
	
	@Test
	public void testDrain() {
		RingBuffer ringBuffer = new RingBuffer(10);
		byte[] tmp = new byte[10];
		assertTrue(ringBuffer.append(tmp, 0, 9) && ringBuffer.availableSize() == 1);
		assertTrue(ringBuffer.drain(tmp, 9) == 9);
		for (int i = 1; i < 10; i++) {
			assertTrue(ringBuffer.append(tmp, 0, i) && ringBuffer.availableSize() == 10 - i);
			assertTrue(ringBuffer.drain(tmp, i - 1) == i - 1 && ringBuffer.size() == 1);
			assertTrue(ringBuffer.drain(tmp, 1) == 1 && ringBuffer.size() == 0);
		}
	}
	
	@Test
	public void testPeekShort() {
		RingBuffer ringBuffer = new RingBuffer(10);
		ByteBuffer byteBuffer = ByteBuffer.allocate(10).order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putShort((short)10);
		ringBuffer.append(byteBuffer.array(), 0, 2);
		System.out.println(ringBuffer.peekShort());
		assertTrue(ringBuffer.peekShort() == 10);
	}

}
