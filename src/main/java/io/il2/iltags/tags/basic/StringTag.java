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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.tags.AbstractILTag;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.ILTagHeader;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnexpectedTagException;
import io.il2.iltags.utils.UTF8Utils;

/**
 * This class implements the string tag. This implementation will threat null
 * strings as empty strings during the tag serialization but will never leave
 * the value as null during the deserialization of the class.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.02
 */
public class StringTag extends AbstractILTag {

	protected String value;

	public StringTag(long tagId) {
		super(tagId);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public long getValueSize() {
		if (value != null) {
			return UTF8Utils.getEncodedSize(value);
		} else {
			return 0;
		}
	}

	@Override
	public void serializeValue(DataOutput out) throws IOException {
		if (value != null) {
			writeUTF8String(value, out);
		}
	}

	@Override
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
			throws IOException, ILTagException {
		assertTagSizeLimit(valueSize);
		value = readUTF8String((int) valueSize, in);
	}

	/**
	 * Writes the string using the UTF-8 encoding.
	 * 
	 * @param value The string.
	 * @param out   The data output.
	 * @throws IOException In case of IO error.
	 */
	protected static void writeUTF8String(CharSequence value, DataOutput out) throws IOException {
		if (value != null) {
			ByteBuffer enc = UTF8Utils.newEncoder().encode(CharBuffer.wrap(value));
			out.write(enc.array(), 0, enc.limit());
		}
	}

	/**
	 * Reads an UTF-8 string from the input. It will fail if the data is invalid.
	 * 
	 * @param size The size of the string in bytes.
	 * @param in   The data input.
	 * @return The string read.
	 * @throws IOException    In case of IO error.
	 * @throws ILTagException If the data ins invalid.
	 */
	protected static String readUTF8String(int size, DataInput in) throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.allocate(size);
		in.readFully(buff.array(), 0, size);
		try {
			return UTF8Utils.newDecoder().decode(buff).toString();
		} catch (CharacterCodingException e) {
			throw new CorruptedTagException("Invalid UTF-8 string.", e);
		}
	}

	/**
	 * Computes the size of the string tag required to encode the given string.
	 * 
	 * @param id    The tag id.
	 * @param value The string value.
	 * @return The size of the tag in bytes.
	 */
	public static long getStringTagSize(long id, CharSequence value) {
		int valueSize = UTF8Utils.getEncodedSize(value);
		return ILTagHeader.getSerializedSize(id, valueSize) + valueSize;
	}

	/**
	 * Computes the size of the standard string tag required to encode the given
	 * string.
	 * 
	 * @param value The string value.
	 * @return The size of the tag in bytes.
	 */
	public static long getStandardStringTagSize(CharSequence value) {
		int valueSize = UTF8Utils.getEncodedSize(value);
		return 1 + ILIntEncoder.encodedSize(valueSize) + valueSize;
	}

	/**
	 * Serializes a string directly into a string tag with the given id. It is
	 * equivalent to create a StringTag instance and serialize it.
	 * 
	 * @param id    The tag id.
	 * @param value The string value.
	 * @param out   The output data.
	 * @throws IOException In case of error.
	 */
	public static void serializeStringTag(long id, CharSequence value, DataOutput out) throws IOException {
		ILTagHeader.serialize(id, UTF8Utils.getEncodedSize(value), out);
		writeUTF8String(value, out);
	}

	/**
	 * Serializes a string directly into a standard string. It is equivalent to
	 * create a StringTag instance and serialize it.
	 * 
	 * @param value The string.
	 * @param out   The data output.
	 * @throws IOException In case of IO error.
	 */
	public static void serializeStandardStringTag(CharSequence value, DataOutput out) throws IOException {
		serializeStringTag(TagID.IL_STRING_TAG_ID, value, out);
	}

	/**
	 * Deserializes a string directly from a string tag with the given tag id.
	 * 
	 * @param id The expected tag id.
	 * @param in The data input.
	 * @return The deserialized string.
	 * @throws IOException            In case of IO error.
	 * @throws UnexpectedTagException If the tag id does not match.
	 * @throws ILTagException         In case of serialization error.
	 */
	public static String deserializeStringTag(long id, DataInput in)
			throws IOException, UnexpectedTagException, ILTagException {
		ILTagHeader header = ILTagHeader.deserializeHeader(in);
		if (header.tagId != id) {
			throw new UnexpectedTagException(
					String.format("Expecting string tag with id %1$X but found %2$X.", id, header.tagId));
		}
		return readUTF8String((int) header.valueSize, in);
	}

	/**
	 * Deserializes a string directly from a standard string tag.
	 * 
	 * @param in The data input.
	 * @return The deserialized string.
	 * @throws IOException            In case of IO error.
	 * @throws UnexpectedTagException If the tag id does not match.
	 * @throws ILTagException         In case of serialization error.
	 */
	public static String deserializeStandardStringTag(DataInput in) throws IOException, ILTagException {
		return deserializeStringTag(TagID.IL_STRING_TAG_ID, in);
	}

	/**
	 * Creates the standard string tag.
	 * 
	 * @return The standard tag.
	 */
	public static StringTag createStandard() {
		return new StringTag(TagID.IL_STRING_TAG_ID);
	}
}
