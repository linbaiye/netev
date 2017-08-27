package org.nalby.netev;

import java.nio.channels.SocketChannel;

import org.nalby.netev.protocol.ProtocolHandler;
import org.nalby.netev.utils.Expect;

public class Connection {
	
	private SocketChannel socketChannel;
	
	private ProtocolHandler protocolHandler;

	public Connection(SocketChannel socketChannel) {
		Expect.notNull(socketChannel, "socketChannel can't be null.");
		this.socketChannel = socketChannel;
	}
	
	public boolean hasCompleteMessage() {
		return false;
	}
	
	public byte[] getPayload() {
		return null;
	}
}
