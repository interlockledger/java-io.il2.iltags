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

import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class implements a facade that provides the java.io.DataOutput interface
 * to a java.nio.ByteBuffer.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.25
 */
public class ByteBufferDataOutput implements DataOutput {

	protected final ByteBuffer dest;

	/**
	 * Creates a new instance of this class. The byte order of the given dest will
	 * be set to Big Endian to ensure full compatibility with the java.io.DataOutput
	 * interface.
	 * 
	 * @param dest The dest of data.
	 * @throws IllegalArgumentException if dest is marked as read only.
	 */
	public ByteBufferDataOutput(ByteBuffer dest) {
		if (dest.isReadOnly()) {
			throw new IllegalArgumentException("dest cannot be read only.");
		}
		this.dest = dest;
		this.dest.order(ByteOrder.BIG_ENDIAN);
	}

	private final void assertRemaining(int len) throws IOException {
		if (this.dest.remaining() < len) {
			throw new IOException("End of buffer reached.");
		}
	}

	@Override
	public void write(int b) throws IOException {
		assertRemaining(1);
		dest.put((byte) (b & 0xFF));
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		assertRemaining(len);
		dest.put(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		write(v ? 0x01 : 0x00);
	}

	@Override
	public void writeByte(int v) throws IOException {
		write(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		assertRemaining(2);
		dest.putShort((short) (v & 0xFFFF));
	}

	@Override
	public void writeChar(int v) throws IOException {
		writeShort(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		assertRemaining(4);
		dest.putInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		assertRemaining(8);
		dest.putLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		assertRemaining(4);
		dest.putFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		assertRemaining(8);
		dest.putDouble(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		assertRemaining(s.length());
		for (int i = 0; i < s.length(); i++) {
			dest.put((byte) (s.charAt(i) & 0xFF));
		}
	}

	@Override
	public void writeChars(String s) throws IOException {
		assertRemaining(s.length() * 2);
		for (int i = 0; i < s.length(); i++) {
			dest.putChar((char) (s.charAt(i) & 0xFFFF));
		}
	}

	/**
	 * Return the size of the char encoded in modified UTF-8.
	 * 
	 * @param c The character.
	 * @return The number of bytes used.
	 */
	protected static int utf8CharSize(int c) {
		if (c == 0x0000) {
			return 2;
		} else if (c < 0x0080) {
			return 1;
		} else if (c < 0x0800) {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * Returns the length of string encoded as modified UTF-8 in bytes.
	 * 
	 * @param s The string to be encoded.
	 * @return The length of the string in bytes.
	 */
	protected static int utf8Len(CharSequence s) {
		int len = 0;
		for (int i = 0; i < s.length(); i++) {
			len += utf8CharSize(s.charAt(i));
		}
		return len;
	}

	/**
	 * Writes the character encoded in the modified UTF-8 format. There must be
	 * enough space in dest to write the character.
	 * 
	 * @param c The character to be written.
	 */
	protected final void writeUTFCharCore(int c) {
		// This code tries to minimize the number of put operations into the buffer thus
		// it will try to compose the values inside integers before writing them.
		if (c == 0) {
			dest.putShort((short) 0b11000000_10000000);
		} else if (c < 0x0080) {
			dest.put((byte) c);
		} else if (c < 0x0800) {
			int b = 0b11000000_10000000 | ((c << 2) & 0b00011111_00000000) | (c & 0b00111111);
			dest.putShort((short) b);
		} else {
			int b = 0b11100000 | ((c >> 12) & 0b00001111);
			dest.put((byte) b);
			b = 0b10000000_10000000 | ((c << 2) & 0b00111111_00000000) | (c & 0b00111111);
			dest.putShort((short) b);
		}
	}

	@Override
	public void writeUTF(String s) throws IOException {
		int len = utf8Len(s);
		if (len > 65535) {
			throw new UTFDataFormatException(String.format("Encoded string too long: %d bytes", len));
		}
		assertRemaining(2 + len);
		dest.putShort((short) len);
		for (int i = 0; i < s.length(); i++) {
			writeUTFCharCore(s.charAt(i));
		}
	}

	/**
	 * Writes the character encoded in the modified UTF-8 format.
	 * 
	 * @param c The character to be added. Only bits 0-15 are used.
	 * @throws IOException In case of error.
	 */
	public void writeUTFChar(int c) throws IOException {
		c = c & 0xFFFF;
		assertRemaining(utf8CharSize(c));
		writeUTFCharCore(c);
	}
}
