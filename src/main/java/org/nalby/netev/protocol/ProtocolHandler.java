package org.nalby.netev.protocol;

import java.io.IOException;
import org.nalby.netev.exception.MalformedProtocolException;
import org.nalby.netev.protocol.socks5.HandlerIntention;

public interface ProtocolHandler {
	
	/**
	 * To be called whenever there is data readable on {@code socketChannel}.
	 * @param socketChannel the readable channel.
	 * @return HandlerIntention indicates what the handler wants to do on receiving data.
	 * @throws MalformedProtocolException if the data violates the protocol.
	 */
	public HandlerIntention onChannelRead() throws MalformedProtocolException, IOException;

	/**
	 * To be called whenever the {@link onChannelRead} indicates that it wants to proxy data.
	 * @return the payload to proxy.
	 * @throws MalformedProtocolException if the data violates the protocol.
	 */
	public byte[] payloadToProxy() throws MalformedProtocolException;
	
	/**
	 * To be called whenever the {@code socketChannel} becomes writable. 
	 * @param socketChannel the channel to write.
	 */
	public void onChannelWrite() throws MalformedProtocolException, IOException;
	
	public void proxyData(byte[] data) throws IOException;
}
