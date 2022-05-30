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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import io.il2.iltags.ilint.ILIntEncoder;

/**
 * 
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public abstract class BaseILTag implements ILTag {

	private final long tagId;

	protected BaseILTag(long tagId) {
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

	@Override
	public void serialize(DataOutput out) throws IOException, ILTagException {
		ILIntEncoder.encode(tagId, out);
		if (!isImplicit()) {
			ILIntEncoder.encode(getValueSize(), out);
		}
		serializeValue(out);
	}

	@Override
	public byte[] toBytes() throws ILTagException {
		long size = getTagSize();
		if (size > MAX_TAG_SIZE) {
			throw new TagTooLargeException("This tag is too large to be handled by this library.");
		}
		ByteArrayOutputStream bOut = new ByteArrayOutputStream((int) size);
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			serialize(out);
		} catch (IOException e) {
			throw new TagTooLargeException("Unable to serialize this tag.", e);
		}
		return bOut.toByteArray();
	}
}
