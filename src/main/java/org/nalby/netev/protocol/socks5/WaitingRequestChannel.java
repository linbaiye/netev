package org.nalby.netev.protocol.socks5;

import static org.nalby.netev.utils.Socks5Constants.CMD_CONNECT;
import static org.nalby.netev.utils.Socks5Constants.CMD_UDP_ASSOCIATE;
import static org.nalby.netev.utils.Socks5Constants.VERSION;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.nalby.netev.exception.MalformedProtocolException;
public class WaitingRequestChannel extends BaseProtocolHanlder {

	private final static int FIXED_LENGTH = 10;
	private byte[] fixedHeader = new byte[FIXED_LENGTH];

	public WaitingRequestChannel(ByteBuffer readBuffer, ByteBuffer writeBuffer, SocketChannel socketChannel,
			Socks5Channel handler) {
		super(readBuffer, writeBuffer, socketChannel, handler);
	}

	@Override
	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException {
		if (socketChannel.read(readBuffer) == -1) {
			return HandlerIntention.FIN;
		}
		if (readBuffer.position() < FIXED_LENGTH) {
			return HandlerIntention.NONE;
		}
		peekReadBuffer(fixedHeader);
		if (fixedHeader[0] != VERSION) {
			throw new MalformedProtocolException("invalid version.");
		}
		if (fixedHeader[1] == CMD_UDP_ASSOCIATE) {
			//currentHandler = new UDPRelayingState();
			return HandlerIntention.WRITE;
		} 
		if (fixedHeader[1] == CMD_CONNECT) {
			return HandlerIntention.PROXY;
		}
		throw new MalformedProtocolException("Unexpected request.");
	}

	@Override
	public byte[] payloadToProxy() throws MalformedProtocolException {
		if (readBuffer.position() < FIXED_LENGTH) {
			throw new MalformedProtocolException("Not enough size to make up header.");
		}
		flipAndDrainReadBuffer(fixedHeader);
		return fixedHeader;
	}

}
