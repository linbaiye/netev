package org.nalby.netev;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.nalby.netev.utils.Expect;

public class RingBuffer {
	
	//10MB
	public static final int MAX_CAPACITY = 1024 * 1024 * 10;
	
	private int head;

	private int rear;

	private byte[] buffer;

	
	public RingBuffer(int capacity) {
		if (capacity <= 0 || capacity > MAX_CAPACITY) {
			throw new IllegalArgumentException("capacity should be between inclusively 1 and " + MAX_CAPACITY);
		}
		buffer = new byte[capacity + 1];
		head = 0;
		rear = 0;
	}
	
	public int capacity() {
		return buffer.length - 1;
	}

	
	public int availableSize() {
		if (rear > head) {
			return (buffer.length - rear - 1) + head;
		} else if (rear == head) {
			return buffer.length - 1;
		}
		return head - rear - 1;
	}
	
	public int size() {
		return buffer.length - 1 - availableSize();
	}
	
	
	public boolean append(byte[] data, int start, int len) {
		Expect.notNull(data, "data is null.");
		Expect.toBeTrue((start + len) <= data.length && start >= 0 && len >= 1, "Invalid length argument(s).");
		if (availableSize() < len)  {
			return false;
		}
		for (int i = 0; i < len; i++) {
			this.buffer[this.rear++] = data[i + start];
			if (this.rear == this.buffer.length) {
				this.rear = 0;
			}
		}
		return true;
	}
	
	/**
	 * Drain {@code len} bytes data from buffer.
	 * @param data
	 * @param len
	 * @return the size drained.
	 */
	public int drain(byte[] data, int len) {
		Expect.notNull(data, "data is null");
		Expect.toBeTrue(len <= data.length, "len can't be longer than data's size.");
		int currentSize = size();
		if (currentSize == 0) {
			return 0;
		}
		int j = 0;
		for (;j < currentSize && j < len; j++) {
			data[j] = buffer[this.head++];
			if (this.head == buffer.length) {
				this.head = 0;
			}
		}
		return j;
	}
	
	public short peekShort() {
		Expect.toBeTrue(size() >= 2, "Not enough bytes to make up a short");
		int currentHead = this.head;
		byte[] tmp = new byte[2];
		drain(tmp, 2);
		this.head = currentHead;
		return ByteBuffer.wrap(tmp).order(ByteOrder.BIG_ENDIAN).getShort();
	}
	
	public int peek(byte[] tmp, int len) {
		int currentHead = this.head;
		int drained = drain(tmp, len);
		this.head = currentHead;
		return drained;
	}
	
	public ByteBuffer toByteBuffer(int len) {
		if (size() < len) {
			return null;
		}
		byte[] tmp = new byte[len];
		peek(tmp, len);
		return ByteBuffer.wrap(tmp);
	}

}
