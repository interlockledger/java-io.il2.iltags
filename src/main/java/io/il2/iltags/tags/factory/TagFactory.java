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
package io.il2.iltags.tags.factory;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import io.il2.iltags.ilint.ILIntDecoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnexpectedTagException;
import io.il2.iltags.tags.UnsupportedTagException;

/**
 * This class implements the ILTagFactory.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public class TagFactory implements ILTagFactory {

	private final boolean strict;

	private HashMap<Long, TagCreator> creators = new HashMap<>();

	public TagFactory(boolean strict) {
		this.strict = strict;
	}

	public boolean isStrict() {
		return this.strict;
	}

	public void registerTag(long tagId, TagCreator creator) {
		if (TagID.isReserved(tagId)) {
			throw new IllegalArgumentException("");
		}
		creators.put(tagId, creator);
	}

	protected ILTag createReserved(long tagId) throws ILTagException {
		// TODO
		return null;
	}

	protected ILTag createRegistered(long tagId) throws ILTagException {
		TagCreator creator = creators.get(tagId);
		if (creator == null) {
			if (isStrict()) {
				throw new UnsupportedTagException(String.format("Tag with ID %1$X is not supported.", tagId));
			} else {
				// TODO Create a byte array tag here.
				return null;
			}
		}
		return creator.createTag(tagId);
	}

	@Override
	public ILTag createTag(long id) throws ILTagException {
		if (TagID.isReserved(id)) {
			return createReserved(id);
		} else {
			return createRegistered(id);
		}
	}

	@Override
	public ILTag fromBytes(byte[] bytes) throws ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);
		ILTag tag;
		try {
			tag = deserialize(in);
		} catch (IOException e) {
			throw new CorruptedTagException("Unable to deserialize the tag.", e);
		}
		if (buff.hasRemaining()) {
			throw new CorruptedTagException("Too many bytes.");
		}
		return tag;
	}

	@Override
	public ILTag deserialize(DataInput in) throws IOException, ILTagException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILTag deserialize(long id, DataInput in) throws IOException, ILTagException {
		long tagId = ILIntDecoder.decode(in);
		if (tagId != id) {
			throw new UnexpectedTagException(String.format("Expecting tag %1$X but found %1$X.", id, tagId));
		}
		ILTag tag = createTag(id);
		// TODO
		return tag;
	}

	@Override
	public void deserializeInto(ILTag tag, DataInput in) throws IOException, ILTagException {
		// TODO Auto-generated method stub
	}
}
