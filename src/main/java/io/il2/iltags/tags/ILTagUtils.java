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

import io.il2.iltags.ilint.ILIntDecoder;

/**
 * This class defines some utility methods used to help the manipulation of
 * ILTags.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.05
 */
public class ILTagUtils {

	private ILTagUtils() {
	}

	/**
	 * Asserts that the value size is within the limits defined by this library. It
	 * throws an exception if the tag value is too large to be handled by this
	 * library.
	 * 
	 * @param valueSize The size of the value. It is handled as an unsigned value.
	 * @throws TagTooLargeException If the value size is too large to be handled.
	 */
	public static void assertTagSizeLimit(long valueSize) throws TagTooLargeException {
		if (Long.compareUnsigned(valueSize, ILTag.MAX_TAG_VALUE_SIZE) > 0) {
			throw new TagTooLargeException(String.format("The tag value has %1$X but the maximum size allowed is %2$X.",
					valueSize, ILTag.MAX_TAG_VALUE_SIZE));
		}
	}

	/**
	 * Reads the ILTag reader. It also validates if the header is valid and if the
	 * value size is within the limits imposed by this library.
	 * 
	 * @param in The data input.
	 * @return The header.
	 * @throws IOException          In case of IO error.
	 * @throws TagTooLargeException If the value size exceeds the limits imposed by
	 *                              this library.
	 * @throws ILTagException       If the header is corrupted.
	 */
	public static ILTagHeader readHeader(DataInput in) throws IOException, TagTooLargeException, ILTagException {
		ILTagHeader header = ILTagHeader.deserializeHeader(in);
		if (!header.isImplicit()) {
			assertTagSizeLimit(header.valueSize);
		}
		return header;
	}

	/**
	 * Reads an ILInt from the data input.
	 * 
	 * @param in           the data input.
	 * @param errorMessage The error message to be thrown if the ILInt is invalid.
	 * @return The value read.
	 * @throws IOException           In case of IO error.
	 * @throws CorruptedTagException If the ILInt is invalid.
	 */
	public static long readILInt(DataInput in, String errorMessage) throws IOException, CorruptedTagException {
		try {
			return ILIntDecoder.decode(in);
		} catch (IllegalArgumentException e) {
			throw new CorruptedTagException(errorMessage);
		}
	}

	/**
	 * Reads a signed ILInt from the data input.
	 * 
	 * @param in           the data input.
	 * @param errorMessage The error message to be thrown if the ILInt is invalid.
	 * @return The value read.
	 * @throws IOException           In case of IO error.
	 * @throws CorruptedTagException If the ILInt is invalid.
	 */
	public static long readSignedILInt(DataInput in, String errorMessage) throws IOException, CorruptedTagException {
		try {
			return ILIntDecoder.decodeSigned(in);
		} catch (IllegalArgumentException e) {
			throw new CorruptedTagException(errorMessage);
		}
	}

	/**
	 * Asserts that the given array can fit within
	 * 
	 * @param count       The expected number of entries.
	 * @param minUnitSize The minimum unit size.
	 * @param valueSize   The size of the value.
	 * @throws CorruptedTagException If the valueSize cannot hold the expected
	 *                               number of entries.
	 */
	public static void assertArraySize(long count, long minUnitSize, long valueSize) throws CorruptedTagException {
		long totalSize = count * minUnitSize;
		if (Long.compareUnsigned(totalSize, valueSize) > 0) {
			throw new CorruptedTagException(String.format("%1$X bytes cannot hold %2$X entries.", valueSize, count));
		}
	}

	/**
	 * Serializes a tag. If tag is null, it will be serialized into a standard null
	 * tag.
	 * 
	 * @param tag The tag.
	 * @param out The data output.
	 * @throws IOException In case of IO error.
	 */
	public static void writeTagOrNull(ILTag tag, DataOutput out) throws IOException {
		if (tag != null) {
			try {
				tag.serialize(out);
			} catch (ILTagException e) {
				throw new IOException(String.format("Unable to serialize the tag %1$s.", tag.getClass().getName()), e);
			}
		} else {
			// The standard null tag.
			out.write(0);
		}
	}
}
