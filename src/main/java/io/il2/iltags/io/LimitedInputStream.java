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

import java.io.IOException;
import java.io.InputStream;

/**
 * This class implements a FilterInputStream that limits the number of bytes
 * that can be extracted from the source stream.
 * 
 * <p>By default, this class will act as a shell over the source InputStream
 * and will never close it. This behavior can be changed if the instance is
 * instructed to take ownership of the source.</p>
 *
 * <p>The LimitedInputStream does not support mark() even if the source does.</p>
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.25 
 */
public class LimitedInputStream extends InputStream {

	/**
	 * The source.
	 */
	protected final InputStream source;
	
	/**
	 * The number of bytes in this stream.
	 */
	protected long size;
	
	/**
	 * Flag that marks if the source is owned or not.
	 */
	protected final boolean owner;
	
	/**
	 * Creates a new instance of this class. The source will not be
	 * closed when this instance is closed.
	 * 
	 * @param source The source stream.
	 * @param size The maximum number of bytes to read.
	 */
	public LimitedInputStream(InputStream source, long size) {
		this(source, size, false);
	}
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param source The source stream.
	 * @param size The maximum number of bytes to read.
	 * @param owner If true, it will take the ownership of the source and closes
	 * it when it is no longer required. Otherwise, the source will never be closed
	 * by this instance.
	 */
	public LimitedInputStream(InputStream source, long size, boolean owner) {
		this.source = source;
		this.size = size;
		this.owner = owner;
	}
	
	/**
	 * Returns the number of bytes remaining on this stream.
	 * 
	 * @return The number of bytes remaining.
	 */
	public long remaining() {
		return this.size;
	}
	
	/**
	 * Verifies if this input stream is empty.
	 * 
	 * @return True if there is no more bytes remaining or false otherwise.
	 */
	public boolean empty() {
		return this.size == 0;
	}

	@Override
	public int read() throws IOException {
		if (this.size > 0) {
			this.size--;
			return this.source.read();
		} else {
			return -1;
		}
	}
	
	@Override
	public int read(byte[] b,int off, int len) throws IOException {
		if (this.size > 0) {
			long n = Math.min(this.size, (long)len);
			int r = this.source.read(b, off, (int)n);
			if (r > 0) {
				this.size-= r;
			}
			return r; 
		} else {
			return -1;
		}
	}
	
	@Override
	public long skip(long n) throws IOException {
		if (this.size > 0) {
			n = Math.min(this.size, n);
			n = this.source.skip(n);
			this.size -= n;
			return n;			
		} else {
			return 0;
		}
	}
	
	@Override
	public int available() throws IOException {
		return (int)Math.min(this.size, (long)this.source.available());		
	}
	
	@Override
	public void close() throws IOException {
		if (this.owner) {
			this.source.close();
		}
	}
	
	/**
	 * Returns true if this instance owns the source.
	 * 
	 * @return true the source is owned by this instance or false
	 * otherwise.
	 */
	public boolean isOwner() {
		return this.owner;
	}
}
