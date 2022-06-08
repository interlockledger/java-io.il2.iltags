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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class StringDictonaryTagTest {

	private String[] createSample(int n) {
		return TagTestUtils.createSampleStrings(n * 2);
	}

	@Test
	void testStringDictonaryTag() {
		StringDictonaryTag t = new StringDictonaryTag(123);
		assertEquals(123, t.getTagID());
		assertEquals(0, t.getValues().size());
	}

	@Test
	void testGetValueSize() {

		for (int size : new int[] { 0, 1, 255, 512 }) {
			StringDictonaryTag t = new StringDictonaryTag(123);
			String[] sample = createSample(size);
			for (int i = 0; i < size; i++) {
				t.getValues().put(sample[i * 2], sample[i * 2 + 1]);
			}
			long exp = ILIntEncoder.encodedSize(size);
			for (String s : sample) {
				exp += StringTag.getStandardStringTagSize(s);
			}
			assertEquals(exp, t.getValueSize());
		}
	}

	@Test
	void testSerializeValue() throws Exception {
		for (int size : new int[] { 0, 1, 255, 512 }) {
			String[] sample = createSample(size);
			ByteArrayOutputStream bExp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bExp)) {
				ILIntEncoder.encode(size, out);
				for (String s : sample) {
					StringTag.serializeStandardStringTag(s, out);
				}
			}

			StringDictonaryTag t = new StringDictonaryTag(123);
			for (int i = 0; i < size; i++) {
				t.getValues().put(sample[i * 2], sample[i * 2 + 1]);
			}
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				t.serializeValue(out);
			}
			assertArrayEquals(bExp.toByteArray(), bOut.toByteArray());
		}
	}

	@Test
	void testDeserializeValue() throws Exception {
		for (int size : new int[] { 0, 1, 255, 512 }) {
			String[] sample = createSample(size);
			ByteArrayOutputStream bExp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bExp)) {
				ILIntEncoder.encode(size, out);
				for (String s : sample) {
					StringTag.serializeStandardStringTag(s, out);
				}
			}

			byte[] serialized = bExp.toByteArray();
			StringDictonaryTag t = new StringDictonaryTag(123);
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(serialized))) {
				t.deserializeValue(null, serialized.length, in);
			}

			assertEquals(size, t.getValues().size());
			int i = 0;
			for (Map.Entry<String, String> entry : t.getValues().entrySet()) {
				assertEquals(sample[i * 2], entry.getKey());
				assertEquals(sample[i * 2 + 1], entry.getValue());
				i++;
			}

			// Bad size
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(serialized))) {
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(null, serialized.length + 1, in);
				});
			}
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(serialized))) {
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(null, serialized.length - 1, in);
				});
			}
		}

		// Test a corrupted pairs
		{
			byte[] serialized = new byte[] { 0x01, 0x11, 0x01, (byte) 0x80, 0x11, 0x01, (byte) 0x30 };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				StringDictonaryTag t = new StringDictonaryTag(123);
				t.deserializeValue(null, serialized.length, in);
			});
		}
		{
			byte[] serialized = new byte[] { 0x01, 0x11, 0x01, (byte) 0x30, 0x11, 0x01, (byte) 0x80 };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				StringDictonaryTag t = new StringDictonaryTag(123);
				t.deserializeValue(null, serialized.length, in);
			});
		}
		// Bad tag
		{
			byte[] serialized = new byte[] { 0x01, 0x12, 0x01, (byte) 0x30, 0x11, 0x01, (byte) 0x30 };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				StringDictonaryTag t = new StringDictonaryTag(123);
				t.deserializeValue(null, serialized.length, in);
			});
		}
		{
			byte[] serialized = new byte[] { 0x01, 0x11, 0x01, (byte) 0x30, 0x12, 0x01, (byte) 0x30 };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				StringDictonaryTag t = new StringDictonaryTag(123);
				t.deserializeValue(null, serialized.length, in);
			});
		}
	}

	@Test
	void testCreateStandard() {
		StringDictonaryTag t = StringDictonaryTag.createStandard();
		assertEquals(TagID.IL_STRING_DICTIONARY_TAG_ID, t.getTagID());
	}

}
