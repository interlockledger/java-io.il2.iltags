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

import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the signed and unsigned 16 bit integer tag.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.02
 */
public class Int16Tag extends AbstractILTag {

	protected short value;

	public Int16Tag(long tagId) {
		super(tagId);
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}

	public int getUnsignedValue() {
		return value & 0xFFFF;
	}

	public void setUnsignedValue(int value) {
		this.value = (short) value;
	}

	@Override
	public long getValueSize() {
		return 2;
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		out.writeShort(value & 0xFFFF);
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		if (valueSize != 2) {
			throw new CorruptedTagException("Invalid value size.");
		}
		value = in.readShort();
	}

	/**
	 * Creates the standard signed 16-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int16Tag createStandardSigned() {
		return new Int16Tag(TagID.IL_INT16_TAG_ID);
	}

	/**
	 * Creates the standard unsigned 16-bit integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static Int16Tag createStandardUnsigned() {
		return new Int16Tag(TagID.IL_UINT16_TAG_ID);
	}
}
