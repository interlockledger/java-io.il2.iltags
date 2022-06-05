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
import java.io.DataOutput;
import java.io.IOException;

import io.il2.iltags.ilint.ILIntEncoder;

/**
 * This helper class implements the ILTag header format. It provides methods
 * that can help with the serialization and deserialization of the ILTag
 * headers.
 *
 * @author Fabio Jun Takada Chino
 * @since 2022.06.02
 */
public class ILTagHeader {

	/**
	 * The tag id.
	 */
	public long tagId;

	/**
	 * The value size.
	 */
	public long valueSize;

	/**
	 * Creates a new instance of this class.
	 */
	public ILTagHeader() {
	}

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param tagId     The tag id.
	 * @param valueSize Size of the value. It is ignored for implicit tags.
	 */
	public ILTagHeader(long tagId, long valueSize) {
		this.tagId = tagId;
		this.valueSize = valueSize;
	}

	/**
	 * Returns true if the tag is implicit.
	 * 
	 * @return true if the tag is implicit or false otherwise.
	 */
	public boolean isImplicit() {
		return TagID.isImplicit(tagId);
	}

	/**
	 * Returns the size of the serialized header.
	 * 
	 * @return The size of the header in bytes.
	 */
	public long getSerializedSize() {
		return getSerializedSize(tagId, valueSize);
	}

	/**
	 * Returns the size of the serialized header.
	 * 
	 * @param id        The tag id.
	 * @param valueSize Size of the value. It is ignored for implicit tags.
	 * @return The size of the header in bytes.
	 */
	public static long getSerializedSize(long id, long valueSize) {
		long headerSize = ILIntEncoder.encodedSize(id);
		if (!TagID.isImplicit(id)) {
			headerSize += ILIntEncoder.encodedSize(valueSize);
		}
		return headerSize;
	}

	/**
	 * Serializes the header.
	 * 
	 * @param out The data output.
	 * @throws IOException In case of IO error.
	 */
	public void serialize(DataOutput out) throws IOException {
		serialize(tagId, valueSize, out);
	}

	/**
	 * Serializes the header.
	 * 
	 * @param id        The tag id.
	 * @param valueSize Size of the value. It is ignored for implicit tags.
	 * @param out       The data output.
	 * @throws IOException In case of IO errors.
	 */
	public static void serialize(long id, long valueSize, DataOutput out) throws IOException {
		ILIntEncoder.encode(id, out);
		if (!TagID.isImplicit(id)) {
			ILIntEncoder.encode(valueSize, out);
		}
	}

	/**
	 * Deserializes the header. For implicit tags the value size will be determined
	 * by io.il2.iltags.tags.TagID.getImplicitValueSize(long).
	 * 
	 * @param in the data input.
	 * @throws IOException    In case of IO error.
	 * @throws ILTagException In case the header is corrupted.
	 */
	public void deserialize(DataInput in) throws IOException, ILTagException {
		this.tagId = ILTagUtils.readILInt(in, "Invalid tag id.");
		if (TagID.isImplicit(this.tagId)) {
			this.valueSize = TagID.getImplicitValueSize(this.tagId);
		} else {
			this.valueSize = ILTagUtils.readILInt(in, "Invalid tag value size.");
		}
	}

	/**
	 * Deserializes the header. For implicit tags the value size will be determined
	 * by io.il2.iltags.tags.TagID.getImplicitValueSize(long).
	 * 
	 * @param in the data input.
	 * @return The header.
	 * @throws IOException    In case of IO error.
	 * @throws ILTagException In case the header is corrupted.
	 */
	public static ILTagHeader deserializeHeader(DataInput in) throws IOException, ILTagException {
		ILTagHeader header = new ILTagHeader();
		header.deserialize(in);
		return header;
	}
}
