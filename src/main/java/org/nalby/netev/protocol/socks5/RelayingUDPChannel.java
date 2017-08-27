package org.nalby.netev.protocol.socks5;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.nalby.netev.exception.MalformedProtocolException;

//We are currently relaying datagrams.
public class RelayingUDPChannel extends BaseProtocolHanlder {
	private ByteBuffer udpResponse;
	public RelayingUDPChannel(ByteBuffer readBuffer, ByteBuffer writeBuffer, SocketChannel socketChannel,
			Socks5Channel handler) throws UnknownHostException {
		super(readBuffer, writeBuffer, socketChannel, handler);
		udpResponse = ByteBuffer.allocate(10);
		udpResponse.put((byte)0x5);
		udpResponse.put((byte)0x0);
		udpResponse.put((byte)0x0);
		udpResponse.put((byte)0x1);
		udpResponse.put(Inet4Address.getByName("localhost").getAddress());
		udpResponse.putShort((short)10000);
	}

	@Override
	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException {
		if (socketChannel.read(readBuffer) == -1) {
			return HandlerIntention.FIN;
		}
		return HandlerIntention.NONE;
	}

	@Override
	public void onChannelWrite() throws MalformedProtocolException, IOException {
		ByteBuffer tmp = udpResponse.asReadOnlyBuffer();
		flipAndWriteToChannel(tmp);
	}
}
