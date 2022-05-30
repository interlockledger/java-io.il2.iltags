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
package io.il2.iltags.ilint;

import static io.il2.iltags.ilint.ILIntBase.*;

import java.io.DataInput;
import java.io.IOException;

/**
 * This class contains static methods used to decode ILInt values.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.25
 */
public class ILIntDecoder {

	/**
	 * Returns the size of the ILInt based on its header.
	 * 
	 * @param header The header. Only the 8 least significant bits of the header are
	 *               used.
	 * @return The size of the ILInt.
	 */
	public static int sizeFromHeader(int header) {
		header = header & 0xFF;
		if (header < ILINT_BASE) {
			return 1;
		} else {
			return header - ILINT_BASE + 2;
		}
	}

	/**
	 * Decodes the body of an ILInt.
	 * 
	 * @param body The body. It must have 1 to 8 bytes.
	 * @return The encoded value.
	 * @throws IllegalArgumentException If the body is invalid.
	 */
	public static long decodeBody(byte[] body) throws IllegalArgumentException {
		if (body.length == 0 || body.length > 8) {
			throw new IllegalArgumentException("Bad body size.");
		}
		long u = 0;
		for (byte b : body) {
			u = (u << 8) | (b & 0xFF);
		}
		if (Long.compareUnsigned(u, MAX_BODY_VALUE) > 0) {
			throw new IllegalArgumentException("Overflow.");
		}
		return u + ILINT_BASE64;
	}

	/**
	 * Decodes an unsigned ILInt.
	 * 
	 * @param reader The data reader.
	 * @return The unsigned decoded value.
	 * @throws IllegalArgumentException If the ILInt format is invalid.
	 * @throws IOException              In case of IO error.
	 */
	public static long decode(DataInput reader) throws IllegalArgumentException, IOException {
		int header = reader.readUnsignedByte();
		if (header < ILINT_BASE) {
			return (long) header;
		}
		int size = sizeFromHeader(header);
		byte[] body = new byte[size - 1];
		reader.readFully(body);
		return decodeBody(body);
	}

	/**
	 * Decodes a signed ILInt.
	 * 
	 * @param reader The data reader.
	 * @return The signed decoded value.
	 * @throws IllegalArgumentException If the ILInt format is invalid.
	 * @throws IOException              In case of IO error.
	 */
	public static long decodeSigned(DataInput reader) throws IllegalArgumentException, IOException {
		return Signed.unpack(decode(reader));
	}
}
