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
import java.math.BigDecimal;
import java.math.BigInteger;

import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.ILTagUtils;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the big decimal tag. If the value is null, it will be
 * treated as zero.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.05
 */
public class BigDecTag extends AbstractILTag {

	protected BigDecimal value;

	public BigDecTag(long tagId) {
		super(tagId);
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public long getValueSize() {
		if (value != null) {
			return 4 + (value.unscaledValue().bitLength() + 8) / 8;
		} else {
			return 4 + 1;
		}
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		if (value != null) {
			out.writeInt(value.scale());
			byte[] tmp = value.unscaledValue().toByteArray();
			out.write(tmp);
		} else {
			out.writeInt(0);
			out.writeByte(0);
		}
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		ILTagUtils.assertTagSizeLimit(valueSize);
		if (valueSize < (4 + 1)) {
			throw new CorruptedTagException("Invalid big decimal value.");
		}
		int scale = in.readInt();
		byte[] tmp = new byte[(int) (valueSize - 4)];
		in.readFully(tmp);
		BigInteger unscaled = new BigInteger(tmp);
		value = new BigDecimal(unscaled, scale);
	}

	/**
	 * Creates the standard big integer tag.
	 * 
	 * @return The standard tag.
	 */
	public static BigDecTag createStandard() {
		return new BigDecTag(TagID.IL_BDEC_TAG_ID);
	}
}
