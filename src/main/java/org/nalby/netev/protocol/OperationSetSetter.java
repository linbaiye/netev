package org.nalby.netev.protocol;

import java.nio.channels.SocketChannel;

public interface OperationSetSetter {
	public void setInterestOps(SocketChannel socketChannel, int opsSet);
}
