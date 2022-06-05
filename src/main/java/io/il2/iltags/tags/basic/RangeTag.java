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
package io.il2.iltags.tags.basic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.LimitedDataInput;
import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.ILTagUtils;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the range tag.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.30
 */
public class RangeTag extends AbstractILTag {

	protected long first;

	protected int count;

	public RangeTag(long tagId) {
		super(tagId);
	}

	public long getFirst() {
		return first;
	}

	public void setFirst(long first) {
		this.first = first;
	}

	public int getCount() {
		return count;
	}

	/**
	 * Sets the count. Only the 16 least significant bits will be considered.
	 * 
	 * @param count The count.
	 */
	public void setCount(int count) {
		this.count = count & 0xFFFF;
	}

	@Override
	public long getValueSize() {
		return ILIntEncoder.encodedSize(first) + 2;
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		ILIntEncoder.encode(first, out);
		out.writeShort(count);
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		if ((valueSize < 3) || (valueSize > 11)) {
			throw new CorruptedTagException("Corrupted range tag.");
		}
		LimitedDataInput limited = new LimitedDataInput(in, (int) valueSize);
		this.first = ILTagUtils.readILInt(limited, "Invalid value.");
		this.count = in.readUnsignedShort();
		if (limited.hasRemaining()) {
			throw new CorruptedTagException("Corrupted range tag. Too many bytes.");
		}
	}

	/**
	 * Creates the standard range tag.
	 * 
	 * @return The standard tag.
	 */
	public static RangeTag createStandard() {
		return new RangeTag(TagID.IL_RANGE_TAG_ID);
	}
}
