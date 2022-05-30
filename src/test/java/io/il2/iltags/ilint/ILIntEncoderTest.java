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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataOutput;

class ILIntEncoderTest {

	@Test
	void testEncodedSize() {
		assertEquals(1, ILIntEncoder.encodedSize(0x0000l));
		assertEquals(1, ILIntEncoder.encodedSize(0x00F7l));
		assertEquals(2, ILIntEncoder.encodedSize(0x00F8l));
		assertEquals(2, ILIntEncoder.encodedSize(0x01F7l));
		assertEquals(3, ILIntEncoder.encodedSize(0x01F8l));
		assertEquals(3, ILIntEncoder.encodedSize(0x0001_00F7l));
		assertEquals(4, ILIntEncoder.encodedSize(0x0001_00F8l));
		assertEquals(4, ILIntEncoder.encodedSize(0x0100_00F7l));
		assertEquals(5, ILIntEncoder.encodedSize(0x0100_00F8l));
		assertEquals(5, ILIntEncoder.encodedSize(0x0001_0000_00F7l));
		assertEquals(6, ILIntEncoder.encodedSize(0x0001_0000_00F8l));
		assertEquals(6, ILIntEncoder.encodedSize(0x0100_0000_00F7l));
		assertEquals(7, ILIntEncoder.encodedSize(0x0100_0000_00F8l));
		assertEquals(7, ILIntEncoder.encodedSize(0x0001_0000_0000_00F7l));
		assertEquals(8, ILIntEncoder.encodedSize(0x0001_0000_0000_00F8l));
		assertEquals(8, ILIntEncoder.encodedSize(0x0100_0000_0000_00F7l));
		assertEquals(9, ILIntEncoder.encodedSize(0x0100_0000_0000_00F8l));
		assertEquals(9, ILIntEncoder.encodedSize(0xFFFF_FFFF_FFFF_FFFFl));
	}

	@Test
	void testSignedEncodedSize() {

		for (ILIntBaseTest.Sample s : ILIntBaseTest.SIGNED_SAMPLES) {
			assertEquals(s.getEncodedSize(), ILIntEncoder.signedEncodedSize(s.getValue()));
		}
	}

	@Test
	void testEncode() throws Exception {
		for (ILIntBaseTest.Sample s : ILIntBaseTest.SAMPLES) {
			ByteBuffer buff = ByteBuffer.allocate(s.getEncodedSize());
			ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
			ILIntEncoder.encode(s.getValue(), out);
			assertArrayEquals(s.getEncoded(), buff.array(), String.format("%1$X", s.getValue()));

			// Test IOError
			buff.position(0);
			buff.limit(s.getEncodedSize() - 1);
			assertThrows(IOException.class, () -> {
				ILIntEncoder.encode(s.getValue(), out);
			}, String.format("%1$X", s.getValue()));
		}
	}

	@Test
	void testEncodeSigned() throws Exception {
		for (ILIntBaseTest.Sample s : ILIntBaseTest.SIGNED_SAMPLES) {
			ByteBuffer buff = ByteBuffer.allocate(s.getEncodedSize());
			ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
			ILIntEncoder.encodeSigned(s.getValue(), out);
			assertArrayEquals(s.getEncoded(), buff.array(), String.format("%1$X", s.getValue()));

			// Test IOError
			buff.position(0);
			buff.limit(s.getEncodedSize() - 1);
			assertThrows(IOException.class, () -> {
				ILIntEncoder.encodeSigned(s.getValue(), out);
			}, String.format("%1$X", s.getValue()));
		}
	}
}
