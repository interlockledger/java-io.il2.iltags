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
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class ILIntArrayTagTest {

	private long[] createSample(int n) {
		Random random = new Random(System.nanoTime());
		long[] sample = new long[n];

		for (int i = 0; i < n; i++) {
			sample[i] = random.nextLong();
		}
		return sample;
	}

	@Test
	void testILIntArrayTag() {
		ILIntArrayTag t = new ILIntArrayTag(1234);
		assertEquals(1234, t.getTagID());
		assertNull(t.getValues());
	}

	@Test
	void testGetSetValues() {
		ILIntArrayTag t = new ILIntArrayTag(1234);
		assertNull(t.getValues());

		t.setValues(1, 3, 2);
		assertArrayEquals(new long[] { 1, 3, 2 }, t.getValues());

	}

	@Test
	void testGetValueSize() {
		ILIntArrayTag t = new ILIntArrayTag(1234);

		assertEquals(1, t.getValueSize());

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			long[] sample = createSample(size);
			long exp = ILIntEncoder.encodedSize(size);
			for (long v : sample) {
				exp += ILIntEncoder.encodedSize(v);
			}
			t.setValues(sample);
			assertEquals(exp, t.getValueSize());
		}
	}

	@Test
	void testSerializeValue() throws Exception {
		ILIntArrayTag t = new ILIntArrayTag(1234);

		ByteBuffer b = ByteBuffer.allocate(1);
		t.serializeValue(new ByteBufferDataOutput(b));
		assertArrayEquals(new byte[] { 0x00 }, b.array());

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			long[] sample = createSample(size);
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encode(size, out);
				for (long v : sample) {
					ILIntEncoder.encode(v, out);
				}
			}
			t.setValues(sample);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				t.serializeValue(out);
			}
			assertArrayEquals(exp.toByteArray(), bOut.toByteArray());
		}
	}

	@Test
	void testDeserializeValue() throws Exception {
		ILIntArrayTag t = new ILIntArrayTag(1234);

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			long[] sample = createSample(size);
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encode(size, out);
				for (long v : sample) {
					ILIntEncoder.encode(v, out);
				}
			}
			byte[] serialized = exp.toByteArray();
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			t.deserializeValue(null, serialized.length, in);
			assertArrayEquals(sample, t.getValues());

			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(null, serialized.length - 1, in);
			});
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(null, serialized.length + 1, in);
			});
		}

		{
			// Bad ILInt
			byte[] serialized = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(null, serialized.length - 1, in);
			});
		}
		{
			// Bad ILInt
			byte[] serialized = new byte[] { 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(null, serialized.length - 1, in);
			});
		}
	}

	@Test
	void testCreateStandard() {
		ILIntArrayTag t = ILIntArrayTag.createStandard();
		assertEquals(TagID.IL_ILINTARRAY_TAG_ID, t.getTagID());
		assertNull(t.values);
	}

	@Test
	void testCreateStandardOIDTag() {
		ILIntArrayTag t = ILIntArrayTag.createStandardOIDTag();
		assertEquals(TagID.IL_OID_TAG_ID, t.getTagID());
		assertNull(t.values);
	}
}
