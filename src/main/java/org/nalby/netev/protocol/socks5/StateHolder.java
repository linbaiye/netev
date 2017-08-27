package org.nalby.netev.protocol.socks5;

import org.nalby.netev.protocol.ProtocolHandler;

public interface StateHolder {

	public ProtocolHandler getCurrentHandler();

	public void setNexetHandler(ProtocolHandler handler);
}
