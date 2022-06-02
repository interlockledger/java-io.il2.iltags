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

import io.il2.iltags.tags.AbsstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the signed and unsigned 32 bit integer tag.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.02
 */
public class Int32Tag extends AbsstractILTag {

	protected int value;

	public Int32Tag(long tagId) {
		super(tagId);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public long getUnsignedValue() {
		return value & 0xFFFF_FFFF;
	}

	public void setUnsignedValue(long value) {
		this.value = (int) value;
	}

	@Override
	public long getValueSize() {
		return 4;
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		out.writeInt(value);
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		if (valueSize != 4) {
			throw new CorruptedTagException("Invalid value size.");
		}
		value = in.readInt();
	}

	/**
	 * Creates the standard signed 32-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int32Tag createStandardSigned() {
		return new Int32Tag(TagID.IL_INT32_TAG_ID);
	}

	/**
	 * Creates the standard unsigned 32-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int32Tag createStandardUnsigned() {
		return new Int32Tag(TagID.IL_UINT32_TAG_ID);
	}
}
