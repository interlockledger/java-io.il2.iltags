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

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;

class ILIntDecoderTest {

	@Test
	void testSizeFromHeader() {

		for (int i = 0; i < 0xF8; i++) {
			assertEquals(1, ILIntDecoder.sizeFromHeader(i));
		}
		assertEquals(2, ILIntDecoder.sizeFromHeader(0xF8));
		assertEquals(3, ILIntDecoder.sizeFromHeader(0xF9));
		assertEquals(4, ILIntDecoder.sizeFromHeader(0xFA));
		assertEquals(5, ILIntDecoder.sizeFromHeader(0xFB));
		assertEquals(6, ILIntDecoder.sizeFromHeader(0xFC));
		assertEquals(7, ILIntDecoder.sizeFromHeader(0xFD));
		assertEquals(8, ILIntDecoder.sizeFromHeader(0xFE));
		assertEquals(9, ILIntDecoder.sizeFromHeader(0xFF));
	}

	@Test
	void testDecodeBody() throws Exception {
		for (ILIntBaseTest.Sample s : ILIntBaseTest.SAMPLES) {
			if (s.getEncodedSize() > 1) {
				byte[] body = new byte[s.getEncodedSize() - 1];
				System.arraycopy(s.getEncoded(), 1, body, 0, body.length);
				assertEquals(s.getValue(), ILIntDecoder.decodeBody(body));
			}
		}
		// Overflow
		byte[] body = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0x08 };
		for (int i = 0x08; i < 0x100; i++) {
			body[7] = (byte) i;
			assertThrows(IllegalArgumentException.class, () -> {
				ILIntDecoder.decodeBody(body);
			});
		}
	}

	@Test
	void testDecode() throws Exception {
		for (ILIntBaseTest.Sample s : ILIntBaseTest.SAMPLES) {
			ByteBuffer buff = ByteBuffer.wrap(s.getEncoded());
			ByteBufferDataInput in = new ByteBufferDataInput(buff);
			assertEquals(s.getValue(), ILIntDecoder.decode(in));

			buff.position(0);
			buff.limit(buff.limit() - 1);
			assertThrows(EOFException.class, () -> {
				ILIntDecoder.decode(in);
			});
		}
	}

	@Test
	void testDecodeSigned() throws Exception {
		for (ILIntBaseTest.Sample s : ILIntBaseTest.SIGNED_SAMPLES) {
			ByteBuffer buff = ByteBuffer.wrap(s.getEncoded());
			ByteBufferDataInput in = new ByteBufferDataInput(buff);
			assertEquals(s.getValue(), ILIntDecoder.decodeSigned(in));

			buff.position(0);
			buff.limit(buff.limit() - 1);
			assertThrows(EOFException.class, () -> {
				ILIntDecoder.decodeSigned(in);
			});
		}
	}

}
