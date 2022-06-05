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
import java.util.ArrayList;
import java.util.List;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.LimitedDataInput;
import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.ILTagUtils;
import io.il2.iltags.tags.TagID;

/**
 * This class implements the ILTag array tag. If values is null, it will be
 * treated as zero length array. Furthermore null entries will be serialized to
 * standard null tags.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.05
 */
public class ILTagArrayTag extends AbstractILTag {

	protected List<ILTag> values;

	public ILTagArrayTag(long tagId) {
		super(tagId);
	}

	public List<ILTag> getValues() {
		return values;
	}

	public void setValues(List<ILTag> values) {
		this.values = values;
	}

	@Override
	public long getValueSize() {
		if (values != null) {
			long size = ILIntEncoder.encodedSize(values.size());
			for (ILTag t : values) {
				if (t != null) {
					size += t.getTagSize();
				} else {
					size += 1;
				}
			}
			return size;
		} else {
			return 1;
		}
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		if (values != null) {
			ILIntEncoder.encode(values.size(), out);
			for (ILTag t : values) {
				ILTagUtils.writeTagOrNull(t, out);
			}
		} else {
			out.writeByte(0);
		}
	}

	protected void deserializeValueCore(ILTagFactory factory, LimitedDataInput in) throws IOException, ILTagException {
		long count = ILTagUtils.readILInt(in, "Invalid counter.");
		ILTagUtils.assertArraySize(count, 1, in.remaining());
		this.values = new ArrayList<>();
		for (int i = 0; i < (int) count; i++) {
			this.values.add(factory.deserialize(in));
		}
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		ILTagUtils.assertTagSizeLimit(valueSize);
		if (valueSize < 1) {
			throw new CorruptedTagException("Invalid ILInt array.");
		}
		LimitedDataInput limitedInput = new LimitedDataInput(in, (int) valueSize);
		deserializeValueCore(factory, limitedInput);
		if (limitedInput.hasRemaining()) {
			throw new CorruptedTagException("Bad value size.");
		}
	}

	/**
	 * Creates the standard ILTag array tag.
	 * 
	 * @return The standard tag.
	 */
	public static ILTagArrayTag createStandard() {
		return new ILTagArrayTag(TagID.IL_ILTAGARRAY_TAG_ID);
	}
}
