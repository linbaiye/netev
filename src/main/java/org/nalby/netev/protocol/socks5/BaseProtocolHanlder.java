package org.nalby.netev.protocol.socks5;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.nalby.netev.exception.MalformedProtocolException;
import org.nalby.netev.protocol.ProtocolHandler;
import org.nalby.netev.utils.Expect;

public abstract class BaseProtocolHanlder implements ProtocolHandler {
	
	Socks5Channel stateHolder;
	ByteBuffer readBuffer;
	ByteBuffer writeBuffer;
	SocketChannel socketChannel;
	
	/**
	 * Assume that the readBuffer is ready to read.
	 * @param tmp data to save into.
	 */
	void peekReadBuffer(byte[] tmp) {
		int currentPosition = readBuffer.position();
		readBuffer.position(0);
		readBuffer.get(tmp);
		readBuffer.position(currentPosition);
	}
	
	void flipAndDrainReadBuffer(byte[] tmp) {
		readBuffer.flip();
		readBuffer.get(tmp);
		readBuffer.compact();
	}
	
	void flipAndWriteToChannel(ByteBuffer toWrite) throws IOException {
		toWrite.flip();
		while (toWrite.hasRemaining()) {
			socketChannel.write(toWrite);
		}
	}
	
	public BaseProtocolHanlder(ByteBuffer readBuffer, ByteBuffer writeBuffer, SocketChannel socketChannel, Socks5Channel stateHolder) {
		Expect.notNull(readBuffer, "Read buffer can't be null.");
		Expect.notNull(writeBuffer, "Write buffer can't be null.");
		Expect.notNull(socketChannel, "Socket channel can't be null.");
		Expect.notNull(stateHolder, "Handler can't be null.");
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
		this.socketChannel = socketChannel;
		this.stateHolder = stateHolder;
	}

	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException {
		throw new IllegalStateException("Nothing to read.");
	}

	public void onChannelWrite() throws MalformedProtocolException, IOException {
		throw new IllegalStateException("Nothing to write.");
	}

	public void proxyData(byte[] data) throws IOException {
		throw new IllegalStateException("Nothing to proxy.");
	}

	public byte[] payloadToProxy() throws MalformedProtocolException {
		throw new IllegalStateException("Nothing to proxy.");
	}
}
