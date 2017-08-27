package org.nalby.netev.protocol.socks5;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.nalby.netev.exception.MalformedProtocolException;
import org.nalby.netev.protocol.ProtocolHandler;

// Protocol details: https://www.ietf.org/rfc/rfc1928.txt
public class Socks5Channel implements ProtocolHandler {
	private ProtocolHandler currentHandler;
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;

	public Socks5Channel(SocketChannel realChannel, String udpHost, int udpPort) throws UnknownHostException {
		this.currentHandler = new WaitingHelloChannel(readBuffer, writeBuffer, realChannel, this);
	}

	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException {
		return currentHandler.onChannelRead();
	}

	public byte[] payloadToProxy() throws MalformedProtocolException {
		return currentHandler.payloadToProxy();
	}

	public void onChannelWrite() throws MalformedProtocolException, IOException {
		currentHandler.onChannelWrite();
	}

	public void proxyData(byte[] data) throws IOException {
		currentHandler.proxyData(data);
	}

	public void setNexetHandler(ProtocolHandler handler) {
		currentHandler = handler;
	}

}
