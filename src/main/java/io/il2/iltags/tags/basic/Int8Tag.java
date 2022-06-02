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
 * This class implements the signed and unsigned 8 bit integer tag.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public class Int8Tag extends AbsstractILTag {

	protected byte value;

	public Int8Tag(long tagId) {
		super(tagId);
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}

	public int getUnsignedValue() {
		return value & 0xFF;
	}

	public void setUnsignedValue(int value) {
		this.value = (byte) value;
	}

	@Override
	public long getValueSize() {
		return 1;
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		out.write(value & 0xFF);
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		if (valueSize != 1) {
			throw new CorruptedTagException("Invalid value size.");
		}
		value = in.readByte();
	}

	/**
	 * Creates the standard signed 8-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int8Tag createStandardSigned() {
		return new Int8Tag(TagID.IL_INT8_TAG_ID);
	}

	/**
	 * Creates the standard unsigned 8-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int8Tag createStandardUnsigned() {
		return new Int8Tag(TagID.IL_UINT8_TAG_ID);
	}
}
