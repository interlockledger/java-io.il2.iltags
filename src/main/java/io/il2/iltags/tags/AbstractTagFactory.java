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

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.il2.iltags.io.ByteBufferDataInput;

/**
 * This class implements an abstract ILTagFactory. It implements all methods
 * with the exception of io.il2.iltags.tags.ILTagFactory.createTag(long).
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public abstract class AbstractTagFactory implements ILTagFactory {

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
		ILTagHeader header = ILTagHeader.deserializeHeader(in);
		ILTag tag = this.createTag(header.tagId);
		tag.deserializeValue(this, header.valueSize, in);
		return tag;
	}

	@Override
	public ILTag deserialize(long id, DataInput in) throws IOException, ILTagException {
		ILTagHeader header = ILTagHeader.deserializeHeader(in);
		if (header.tagId != id) {
			throw new UnexpectedTagException(String.format("Expecting %1$X but found %2$X.", header.tagId, id));
		}
		ILTag tag = this.createTag(header.tagId);
		tag.deserializeValue(this, header.valueSize, in);
		return tag;
	}

	@Override
	public void deserializeInto(ILTag tag, DataInput in) throws IOException, ILTagException {
		ILTagHeader header = ILTagHeader.deserializeHeader(in);
		if (header.tagId != tag.getTagID()) {
			throw new UnexpectedTagException(
					String.format("Expecting %1$X but found %2$X.", header.tagId, tag.getTagID()));
		}
		tag.deserializeValue(this, header.valueSize, in);
	}
}
