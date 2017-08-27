package org.nalby.netev.protocol.socks5;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.nalby.netev.utils.Socks5Constants.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.nalby.netev.exception.MalformedProtocolException;

@RunWith(MockitoJUnitRunner.class)
public class WaitingHelloChannelTests {
	
	@Mock
	private SocketChannel socketChannel;

	private ByteBuffer readBuffer = ByteBuffer.allocate(10*1024);

	private ByteBuffer writeBuffer = ByteBuffer.allocate(10*1024);

	private WaitingHelloChannel waitingHelloChannel;
	
	@Mock
	private Socks5Channel handler;

	@Before
	public void setup() {
		waitingHelloChannel = new WaitingHelloChannel(readBuffer, writeBuffer, socketChannel, handler);
	}
	
	@Test
	public void testReadEOF() throws IOException, MalformedProtocolException {
		when(socketChannel.read(readBuffer)).thenReturn(-1);
		assertTrue(waitingHelloChannel.onChannelRead() == HandlerIntention.FIN);
	}
	
	@Test
	public void testIncompleteMessage() throws IOException, MalformedProtocolException {
		when(socketChannel.read(readBuffer)).thenReturn(1);
		assertTrue(waitingHelloChannel.onChannelRead() == HandlerIntention.NONE);
	}
	
	
	private void buildHelloMessage() throws IOException {
		readBuffer.put(VERSION);
		readBuffer.put((byte)2);
		readBuffer.put((byte)1);
		readBuffer.put((byte)2);
	}
	
	@Test
	public void testCompleteMessage() throws IOException, MalformedProtocolException {
		buildHelloMessage();
		when(socketChannel.read(readBuffer)).thenReturn(4);
		assertTrue(waitingHelloChannel.onChannelRead() == HandlerIntention.WRITE);
		when(socketChannel.write(any(ByteBuffer.class))).thenAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				ByteBuffer byteBuffer = (ByteBuffer) invocation.getArguments()[0];
				assertTrue(byteBuffer.limit() == 2);
				byteBuffer.position(2);
				return 0;
			}
		});
		waitingHelloChannel.onChannelWrite();
		//Make sure the readBuffer was compacted.
		assertTrue(readBuffer.position() == 0);
		
		buildHelloMessage();
		readBuffer.put((byte)1);
		waitingHelloChannel.onChannelWrite();
		//Make sure the readBuffer was compacted.
		assertTrue(readBuffer.position() == 1);
	}


}
