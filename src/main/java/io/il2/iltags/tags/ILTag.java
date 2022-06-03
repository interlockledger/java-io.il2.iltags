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

/**
 * This is the interface of all ILTags.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public interface ILTag {

	/**
	 * Maximum size of tag value that this library can handle. This limit has been
	 * defined in order to prevent potential issues with incorrect data
	 * serializations and prevent memory abuse.
	 */
	static final long MAX_TAG_VALUE_SIZE = 1024 * 1024 * 512;

	/**
	 * Returns the tag id.
	 * 
	 * @return The tag id.
	 */
	long getTagID();

	/**
	 * Returns true if this tag is implicit.
	 * 
	 * @return true if the tag is implicit or false otherwise.
	 */
	boolean isImplicit();

	/**
	 * Returns true if this tag is reserved.
	 * 
	 * @return true if the tag is reserved or false otherwise.
	 */
	boolean isReserved();

	/**
	 * Returns the size of this tag.
	 * 
	 * @return The size of this tag in bytes.
	 */
	long getTagSize();

	/**
	 * Serializes this tag.
	 * 
	 * @param out The data output.
	 * @throws IOException    In case of IO errors.
	 * @throws ILTagException In case of serialization errors.
	 */
	void serialize(DataOutput out) throws IOException, ILTagException;

	/**
	 * Returns the size of the value of the tag.
	 * 
	 * @return The size of the value of the tag in bytes.
	 */
	public long getValueSize();

	/**
	 * Serializes the value of this tag.
	 * 
	 * @param out The data output.
	 * @throws IOException In case of IO errors.
	 */
	public void serializeValue(DataOutput out) throws IOException;

	/**
	 * Deserializes the value of this tag, replacing the current value if any.
	 * 
	 * @param factory   The ILTagFactory. It may be used to create inner tags if
	 *                  any.
	 * @param valueSize The size of the value in bytes.
	 * @param in        The data input.
	 * @throws IOException    In case of IO errors.
	 * @throws ILTagException In case the tag serialization is invalid.
	 */
	public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in) throws IOException, ILTagException;

	byte[] toBytes() throws ILTagException;
}
