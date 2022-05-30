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

import java.io.DataOutput;
import java.io.IOException;

/**
 * This class contains the static methods required to encode values using the
 * ILInt format.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.26
 */
public class ILIntEncoder {

	/**
	 * Returns the ILInt encoded size. The value of v will be viewed as an unsigned
	 * value.
	 * 
	 * @param v The value.
	 * @return The number of bytes required to encode the value.
	 */
	public static int encodedSize(long u) {
		// Do not forget to treat u as an unsigned value.
		if (Long.compareUnsigned(u, ILINT_BASE64) < 0) {
			return 1;
		} else if (Long.compareUnsigned(u, (0xFFl + ILINT_BASE64)) <= 0) {
			return 2;
		} else if (Long.compareUnsigned(u, (0xFFFFl + ILINT_BASE64)) <= 0) {
			return 3;
		} else if (Long.compareUnsigned(u, (0x00FF_FFFFl + ILINT_BASE64)) <= 0) {
			return 4;
		} else if (Long.compareUnsigned(u, (0xFFFF_FFFFl + ILINT_BASE64)) <= 0) {
			return 5;
		} else if (Long.compareUnsigned(u, (0x00FF_FFFF_FFFFl + ILINT_BASE64)) <= 0) {
			return 6;
		} else if (Long.compareUnsigned(u, (0xFFFF_FFFF_FFFFl + ILINT_BASE64)) <= 0) {
			return 7;
		} else if (Long.compareUnsigned(u, (0x00FF_FFFF_FFFF_FFFFl + ILINT_BASE64)) <= 0) {
			return 8;
		} else {
			return 9;
		}
	}

	/**
	 * Returns the signed ILInt encoded size.
	 * 
	 * @param v The value.
	 * @return The number of bytes required to encode the value.
	 */
	public static int signedEncodedSize(long s) {
		return encodedSize(Signed.pack(s));
	}

	/**
	 * Encodes the given unsigned 64-bit integer using the ILInt format.
	 * 
	 * @param u   The value to encode.
	 * @param out The data output.
	 * @return The number of bytes used.
	 * @throws IOException In case of error.
	 */
	public static int encode(long u, DataOutput out) throws IOException {
		int encodedSize = encodedSize(u);
		if (encodedSize == 1) {
			out.write((int) (u & 0xFF));
			return encodedSize;
		} else {
			byte[] tmp = new byte[encodedSize];
			tmp[0] = (byte) (ILINT_BASE + encodedSize - 2);
			u -= ILINT_BASE64;
			for (int i = encodedSize - 1; i > 1; i--) {
				tmp[i] = (byte) (u & 0xFF);
				u = u >>> 8;
			}
			out.write(tmp);
			return encodedSize;
		}
	}

	/**
	 * Encodes the given signed 64-bit integer using the signed ILInt format.
	 * 
	 * @param s   The signed value to encode.
	 * @param out The data output.
	 * @return The number of bytes used.
	 * @throws IOException In case of error.
	 */
	public static int encodeSigned(long s, DataOutput out) throws IOException {
		return encode(Signed.pack(s), out);
	}
}
