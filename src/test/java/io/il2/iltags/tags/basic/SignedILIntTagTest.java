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

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class SignedILIntTagTest {

	private static final long sLong1 = -98723886;

	@Test
	void testSignedILIntTag() {
		SignedILIntTag t = new SignedILIntTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		SignedILIntTag t = new SignedILIntTag(123456);
		t.setValue(sLong1);
		assertEquals(sLong1, t.getValue());
	}

	@Test
	void testSetValue() {
		SignedILIntTag t = new SignedILIntTag(123456);
		t.setValue(sLong1);
		assertEquals(sLong1, t.getValue());
	}

	@Test
	void testGetValueSize() {
		SignedILIntTag t = new SignedILIntTag(123456);
		assertEquals(1, t.getValueSize());
		t.setValue(sLong1);
		assertEquals(5, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws IOException {
		SignedILIntTag t = new SignedILIntTag(123456);

		for (long v : new long[] { 0, 1, -1, 9223372036854775807l, -9223372036854775808l }) {
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encodeSigned(v, out);
			}
			t.setValue(v);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				t.serializeValue(out);
			}
			assertArrayEquals(exp.toByteArray(), bOut.toByteArray());
		}
	}

	@Test
	void testDeserializeValue() throws Exception {
		SignedILIntTag t = new SignedILIntTag(123456);

		for (long v : new long[] { 0, 1, -1, 9223372036854775807l, -9223372036854775808l }) {
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encodeSigned(v, out);
			}
			byte[] serialized = exp.toByteArray();
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			t.deserializeValue(null, -1, in);
			assertEquals(v, t.getValue());
		}

		assertThrows(CorruptedTagException.class, () -> {
			byte[] serialized = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xF8 };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			t.deserializeValue(null, -1, in);
		});
	}

	@Test
	void testCreateStandard() {
		SignedILIntTag t = SignedILIntTag.createStandard();
		assertEquals(TagID.IL_SIGNED_ILINT_TAG_ID, t.getTagID());
	}

}
