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

import io.il2.iltags.ilint.ILIntDecoder;
import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the ILInt tag.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.30
 */
public class ILIntTag extends AbstractILTag {

	protected long value;

	public ILIntTag(long tagId) {
		super(tagId);
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public long getValueSize() {
		return ILIntEncoder.encodedSize(value);
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		ILIntEncoder.encode(value, out);
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		ILIntDecoder.decode(in);
	}

	/**
	 * Creates the standard ILInt tag.
	 * 
	 * @return The standard tag.
	 */
	public static ILIntTag createStandard() {
		return new ILIntTag(TagID.IL_ILINT_TAG_ID);
	}
}
