package org.nalby.netev.protocol.socks5;

import static org.nalby.netev.utils.Socks5Constants.VERSION;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.nalby.netev.exception.MalformedProtocolException;

public class WaitingHelloChannel extends BaseProtocolHanlder  {
	private static final short FIXED_LENGTH = 2;
	private byte[] fixedHeader = new byte[FIXED_LENGTH];
	private	ByteBuffer response;

	public WaitingHelloChannel(ByteBuffer readBuffer, ByteBuffer writeBuffer, SocketChannel socketChannel,
			Socks5Channel stateHolder) {
		super(readBuffer, writeBuffer, socketChannel, stateHolder);
		response = ByteBuffer.allocate(2);
		response.put((byte)0x5);
		response.put((byte)0x0);
	}

	private boolean hasCompleteMessage() throws MalformedProtocolException{
		if (readBuffer.position() < FIXED_LENGTH) {
			return false;
		}
		peekReadBuffer(fixedHeader);
		if (fixedHeader[0] != VERSION) {
			throw new MalformedProtocolException("Unexpected version.");
		}
		return readBuffer.position() >= fixedHeader[1] + FIXED_LENGTH;
	}

	@Override
	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException {
		if (socketChannel.read(readBuffer) == -1) {
			return HandlerIntention.FIN;
		}
		return hasCompleteMessage() ? HandlerIntention.WRITE : HandlerIntention.NONE;
	}

	@Override
	public void onChannelWrite() throws MalformedProtocolException, IOException {
		if (!hasCompleteMessage()) {
			return;
		}
		readBuffer.flip();
		readBuffer.get();
		readBuffer.position(readBuffer.get() + FIXED_LENGTH);
		readBuffer.compact();

		flipAndWriteToChannel(response);
		stateHolder.setNexetHandler(new WaitingRequestChannel(readBuffer, writeBuffer, socketChannel, stateHolder));
	}
}
