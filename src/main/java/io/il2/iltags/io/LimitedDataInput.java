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

/**
 * This class implements a limited DataInput that reads data from another
 * DataInput.
 * 
 * <p>
 * The current implementation supports all methods from DataInput with the
 * exception of the methods java.io.DataInput.readLine() and
 * java.io.DataInput.readUTF(). It is important to notice that it may change in
 * future versions of this class.
 * </p>
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.03
 */
public class LimitedDataInput implements DataInput {

	protected final DataInput source;

	protected int size;

	public LimitedDataInput(DataInput source, int size) {
		this.source = source;
		this.size = size;
	}

	protected void updateRead(int size) throws EOFException {
		if (this.size < size) {
			throw new EOFException("End of data.");
		}
		this.size -= size;
	}

	public int remaining() {
		return size;
	}

	public boolean hasRemaining() {
		return size > 0;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		updateRead(len);
		source.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		int skipped = source.skipBytes(Math.min(n, this.size));
		this.size -= skipped;
		return skipped;
	}

	@Override
	public boolean readBoolean() throws IOException {
		updateRead(1);
		return source.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		updateRead(1);
		return source.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		updateRead(1);
		return source.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		updateRead(2);
		return source.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		updateRead(2);
		return source.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		updateRead(2);
		return source.readChar();
	}

	@Override
	public int readInt() throws IOException {
		updateRead(4);
		return source.readInt();
	}

	@Override
	public long readLong() throws IOException {
		updateRead(8);
		return source.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		updateRead(4);
		return source.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		updateRead(8);
		return source.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		throw new UnsupportedOperationException("readLine() is not supported.");
	}

	@Override
	public String readUTF() throws IOException {
		throw new UnsupportedOperationException("readUTF() is not supported.");
	}
}
