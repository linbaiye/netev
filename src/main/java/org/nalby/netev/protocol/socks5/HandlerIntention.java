package org.nalby.netev.protocol.socks5;

public enum HandlerIntention {
	//
	NONE,
	//The handler needs to write (the channel).
	WRITE,
	//The handler wants the data to be proxied.
	PROXY,
	//The channel reaches eof.
	FIN
}
