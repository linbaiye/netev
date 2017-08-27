package org.nalby.netev;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class Dispatcher {
	
	private Selector selector;
	
	private ServerSocketChannel serverSocketChannel;
	
	private static final int DEFAULT_BACKLOG = 100;
	
	public Dispatcher(String host, int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(host, port), DEFAULT_BACKLOG);
		serverSocketChannel.configureBlocking(false);
		//serverSocketChannel.setOption(SocketOptions.R, value)
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
	}
	
	private void onAccept() throws IOException {
		SocketChannel socketChannel = serverSocketChannel.accept();
		if (socketChannel == null) {
			return;
		}
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	
	private void onWrite(SocketChannel socketChannel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		byteBuffer.put(new String("hello").getBytes());
		byteBuffer.flip();
		while (byteBuffer.hasRemaining()) {
			socketChannel.write(byteBuffer);
		}
		socketChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
	}
	
	
	private void onRead(SocketChannel channel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.allocate(10000);
		channel.read(byteBuffer);
		System.out.println(new String(byteBuffer.array(), 0, byteBuffer.position()));
		channel.keyFor(selector).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}
	
	public void start() throws IOException {
		while (true) {
			if (selector.select() == 0) {
				continue;
			}
			Set<SelectionKey> set = selector.selectedKeys();
			for (SelectionKey selectedKey: set) {
				if (selectedKey.isAcceptable()) {
					onAccept();
					continue;
				}
				if (selectedKey.isReadable()) {
					onRead((SocketChannel) selectedKey.channel());
				}
				if (selectedKey.isWritable()) {
					onWrite((SocketChannel) selectedKey.channel());
				}
			}
		}
	}
	
}
