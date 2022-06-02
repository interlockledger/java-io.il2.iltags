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
package io.il2.iltags.tags;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataOutput;

/**
 * This abstract class implements the basic functionality of the tags.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public abstract class AbstractILTag implements ILTag {

	private final long tagId;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tagId The specified tag id.
	 */
	protected AbstractILTag(long tagId) {
		this.tagId = tagId;
	}

	@Override
	public long getTagID() {
		return this.tagId;
	}

	@Override
	public boolean isImplicit() {
		return TagID.isImplicit(tagId);
	}

	@Override
	public boolean isReserved() {
		return TagID.isReserved(tagId);
	}

	@Override
	public long getTagSize() {
		long size = ILIntEncoder.encodedSize(tagId);
		long valueSize = this.getValueSize();
		if (!isImplicit()) {
			size += ILIntEncoder.encodedSize(valueSize);
		}
		return size + valueSize;
	}

	/**
	 * Serializes the tag header.
	 * 
	 * @param out The data output.
	 * @throws IOException In case of IO error.
	 */
	protected void serializeHeader(DataOutput out) throws IOException {
		ILIntEncoder.encode(tagId, out);
		if (!isImplicit()) {
			ILIntEncoder.encode(getValueSize(), out);
		}
	}

	@Override
	public void serialize(DataOutput out) throws IOException, ILTagException {
		serializeHeader(out);
		serializeValue(out);
	}

	@Override
	public byte[] toBytes() throws ILTagException {
		long size = getTagSize();
		assertTagSizeLimit(size);
		ByteBuffer buff = ByteBuffer.allocate((int) size);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		try {
			serialize(out);
		} catch (IOException e) {
			throw new ILTagException("Unable to serialize this tag. The tag implementation may be be incorrect.", e);
		}
		return buff.array();
	}

	/**
	 * Asserts that the value size is within the limits defined by this library. It
	 * throws an exception if the tag value is too large to be handled by this
	 * library.
	 * 
	 * @param valueSize The size of the value. It is handled as an unsigned value.
	 * @throws TagTooLargeException If the value size is too large to be handled.
	 */
	public static void assertTagSizeLimit(long valueSize) throws TagTooLargeException {
		if (Long.compareUnsigned(valueSize, MAX_TAG_SIZE) > 0) {
			throw new TagTooLargeException(String.format("The tag value has %1$X but the maximum size allowed is %2$X.",
					valueSize, MAX_TAG_SIZE));
		}
	}
}
