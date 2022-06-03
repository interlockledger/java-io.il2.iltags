/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2021-2022, InterlockLedger
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.il2.iltags.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class implements a facade that provides the java.io.DataInput interface
 * to a java.nio.ByteBuffer.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.25
 */
public class ByteBufferDataInput implements DataInput {

	protected final ByteBuffer source;

	/**
	 * Creates a new instance of this class. The byte order of the provided source
	 * will be set to Big Endian to ensure full compatibility with the
	 * java.io.DataInput interface.
	 * 
	 * @param source The source of data.
	 */
	public ByteBufferDataInput(ByteBuffer source) {
		this.source = source;
		this.source.order(ByteOrder.BIG_ENDIAN);
	}

	/**
	 * Create a new instance of this class.
	 * 
	 * @param data   The data to be read.
	 * @param offs   The initial offset.
	 * @param length The length of the data.
	 */
	public ByteBufferDataInput(byte[] data, int offs, int length) {
		this(ByteBuffer.wrap(data, offs, length));
	}

	/**
	 * Create a new instance of this class.
	 * 
	 * @param data The data to be read.
	 */
	public ByteBufferDataInput(byte[] data) {
		this(data, 0, data.length);
	}

	private final void assertAvailable(int len) throws EOFException {
		if (this.source.remaining() < len) {
			throw new EOFException("End of buffer reached.");
		}
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		assertAvailable(len);
		source.get(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		n = Math.min(n, source.remaining());
		source.position(source.position() + n);
		return n;
	}

	@Override
	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		assertAvailable(1);
		return source.get();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return readByte() & 0xFF;
	}

	@Override
	public short readShort() throws IOException {
		assertAvailable(2);
		return source.getShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort() & 0xFFFF;
	}

	@Override
	public char readChar() throws IOException {
		return (char) readShort();
	}

	@Override
	public int readInt() throws IOException {
		assertAvailable(4);
		return source.getInt();
	}

	@Override
	public long readLong() throws IOException {
		assertAvailable(8);
		return source.getLong();
	}

	@Override
	public float readFloat() throws IOException {
		assertAvailable(4);
		return source.getFloat();
	}

	@Override
	public double readDouble() throws IOException {
		assertAvailable(8);
		return source.getDouble();
	}

	@Override
	public String readLine() throws IOException {
		if (!source.hasRemaining()) {
			return null;
		}
		// Note: this function actually reads Unicode's Basic Latin + Latin 1
		// Supplement (a.k.a ISO-8859-1).
		StringBuilder sb = new StringBuilder();
		boolean scanning = source.hasRemaining();
		while (scanning) {
			int c = source.get() & 0xFF;
			switch (c) {
			case '\n':
				scanning = false;
				break;
			case '\r':
				scanning = false;
				if (source.hasRemaining()) {
					c = source.get() & 0xFF;
					if (c != '\n') {
						source.position(source.position() - 1);
					}
				}
				break;
			default:
				sb.append((char) c);
				scanning = source.hasRemaining();
			}
		}
		return sb.toString();
	}

	/**
	 * Extracts the body of a multi-byte modified UTF-8 character. The body is
	 * packed into an 8 or 16 bits integer.
	 * 
	 * @param src   The source byte buffer. It assumes that the buffer order is set
	 *              to big endian format.
	 * @param count The number of bytes in the body. It must be 1 or 2. Any other
	 *              value will result in an undefined behavior.
	 * @return The value of the body. It may have 8 or 16 bits.
	 * @throws UTFDataFormatException If the body encoding is invalid or truncated.
	 */
	protected static int extractUTF8Body(ByteBuffer src, int count) throws UTFDataFormatException {
		if (src.remaining() < count) {
			throw new UTFDataFormatException("Premature end of character.");
		}
		int b;
		int mask = 0b11000000_11000000;
		int exp = 0b10000000_10000000;
		if (count == 1) {
			b = src.get() & 0XFF;
			mask = mask >> 8;
			exp = exp >> 8;
		} else {
			b = src.getShort() & 0XFFFF;
		}
		if ((b & mask) != exp) {
			throw new UTFDataFormatException("Premature end of character.");
		}
		return b;
	}

	/**
	 * Extracts the next char from the buffer when it is encoded in Modified UTF-8.
	 * src must have at least 1 byte remaining.
	 * 
	 * @param src The source.
	 * @return The extracted character.
	 * @throws UTFDataFormatException If a valid character cannot be extracted.
	 */
	protected static char extractUTF8Char(ByteBuffer src) throws UTFDataFormatException {
		int header = src.get() & 0xFF;
		if ((header & 0b10000000) == 0b00000000) {
			// 1 Byte - 0b0xxxxxxx
			return (char) header;
		} else if ((header & 0b11100000) == 0b11000000) {
			// 2 bytes - 0b110xxxxx 0b10xxxxxx
			int body = extractUTF8Body(src, 1);
			return (char) (((header & 0b00011111) << 6) | (body & 0b00111111));
		} else if ((header & 0b11110000) == 0b11100000) {
			// 3 bytes - 0b1110xxxx 0b10xxxxxx 0b10xxxxxx
			int body = extractUTF8Body(src, 2);
			return (char) (((header & 0b00001111) << 12) | ((body & 0b00111111_00000000) >> 2) | (body & 0b00111111));
		} else {
			throw new UTFDataFormatException("Invalid utf-8 character.");
		}
	}

	@Override
	public String readUTF() throws IOException {
		int len = readUnsignedShort();
		if (source.remaining() < len) {
			// Seek to the end of the buffer as all bytes will be consumed.
			source.position(source.limit());
			throw new EOFException();
		}
		ByteBuffer view = source.duplicate();
		view.limit(view.position() + len);
		StringBuilder sb = new StringBuilder();
		try {
			while (view.hasRemaining()) {
				sb.append(extractUTF8Char(view));
			}
		} finally {
			// Synchronized both buffers.
			source.position(view.position());
		}
		return sb.toString();
	}

	/**
	 * Reads an Modified UTF-8 character.
	 * 
	 * @return The character read.
	 * @throws IOException            On any IO error.
	 * @throws UTFDataFormatException If the character is invalid or truncated.
	 */
	public char readUTFChar() throws IOException, UTFDataFormatException {
		if (!source.hasRemaining()) {
			throw new EOFException();
		}
		return extractUTF8Char(source);
	}
}
