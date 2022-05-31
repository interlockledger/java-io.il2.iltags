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

/**
 * This is the base interface for all Tag factories. Tag factories are
 * responsible for the creation of tags based on the tag IDs and also the
 * deserialization of the tags.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public interface ILTagFactory {

	/**
	 * Creates a new tag that implements the given tag id.
	 * 
	 * @param id The tag id.
	 * @return The ILTag that implements the given tag id.
	 * @throws ILTagException If the tag id is not supported.
	 */
	ILTag createTag(long id) throws ILTagException;

	/**
	 * Deserializes the tag from a byte array. All bytes of the array must be part
	 * of the tag otherwise the serialization will fail.
	 * 
	 * @param bytes The bytes of the tag.
	 * @return The deserialized tag.
	 * @throws ILTagException If the tag cannot be deserialized.
	 */
	ILTag fromBytes(byte[] bytes) throws ILTagException;

	/**
	 * Deserializes a single tag from the data input.
	 * 
	 * @param in The data input.
	 * @return The deserialized tag.
	 * @throws IOException    In case of IO Error.
	 * @throws ILTagException In case of deserialization error.
	 */
	ILTag deserialize(DataInput in) throws IOException, ILTagException;

	/**
	 * Deserializes a single tag from the data input and validates if the serialized
	 * tag is indeed the expected tag. The deserialization fails if the deserialized
	 * tag does not match the expected tag id.
	 * 
	 * @param id The expected tag id.
	 * @param in The data input.
	 * @return The deserialized tag.
	 * @throws IOException    In case of IO Error.
	 * @throws ILTagException In case of the serialization error.
	 */
	ILTag deserialize(long id, DataInput in) throws IOException, ILTagException;

	/**
	 * Deserializes a single tag into the provided tag instance. The serialized tag
	 * id must match the tag id of the provided tag instance.
	 * 
	 * @param tag The tag that will receive the tag.
	 * @param in  The data input.
	 * @throws IOException    In case of IO errors.
	 * @throws ILTagException In case of serialization error.
	 */
	void deserializeInto(ILTag tag, DataInput in) throws IOException, ILTagException;
}
