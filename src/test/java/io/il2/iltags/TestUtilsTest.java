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
package io.il2.iltags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;



public class TestUtilsTest {

	@Test
	public void testCreateSampleByteArray() {

		for (int size = 0; size < 64; size++) {
			byte[] v = TestUtils.createSampleByteArray(size);
			byte[] expected = new byte[size];
			for (int i = 0; i < size; i++) {
				expected[i] = (byte) (i & 0xFF);
			}
			assertArrayEquals(expected, v);
		}
	}

	@Test
	public void testFillSampleByteArray() {

		for (int size = 0; size < 16; size++) {
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					byte[] v = new byte[i + size + j];
					byte[] expected = v.clone();
					TestUtils.fillSampleByteArray(v, i, size);
					for (int k = 0; k < size; k++) {
						expected[i + k] = (byte) (k & 0xFF);
					}
					assertArrayEquals(expected, v);
				}
			}
		}
	}

	@Test
	public void testGenRandomString() {

		for (int size = 0; size < 1024; size += 33) {
			String s = TestUtils.genRandomString(size);
			assertEquals(size, s.length());
		}
	}

	@Test
	public void testSample() {
		ByteBuffer b = TestUtils.UTF8.encode(CharBuffer.wrap(TestUtils.SAMPLE));
		byte[] v = new byte[b.limit()];
		b.get(v);
		assertArrayEquals(TestUtils.SAMPLE_BIN, v);
	}
}
