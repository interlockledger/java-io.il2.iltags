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
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.factory.TagFactory;

class DictonaryTagTest {

	@Test
	void testDictonaryTag() {
		DictonaryTag t = new DictonaryTag(1234);
		assertEquals(1234, t.getTagID());
		assertEquals(0, t.getValues().size());
	}

	@Test
	void testGetValueSize() {

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			String[] keys = TagTestUtils.createSampleStrings(size);
			ILTag[] values = TagTestUtils.createSampleTags(size);
			long exp = ILIntEncoder.encodedSize(size);
			for (int i = 0; i < size; i++) {
				exp += StringTag.getStandardStringTagSize(keys[i]);
				exp += values[i].getTagSize();
			}

			DictonaryTag t = new DictonaryTag(1234);
			for (int i = 0; i < size; i++) {
				t.getValues().put(keys[i], values[i]);
			}
			assertEquals(exp, t.getValueSize());
		}
	}

	@Test
	void testSerializeValue() throws Exception {
		for (int size : new int[] { 0, 1, 255, 65535 }) {
			String[] keys = TagTestUtils.createSampleStrings(size);
			ILTag[] values = TagTestUtils.createSampleTags(size);

			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encode(size, out);
				for (int i = 0; i < size; i++) {
					StringTag.serializeStandardStringTag(keys[i], out);
					values[i].serialize(out);
				}
			}

			DictonaryTag t = new DictonaryTag(1234);
			for (int i = 0; i < size; i++) {
				t.getValues().put(keys[i], values[i]);
			}
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				t.serializeValue(out);
			}
			assertArrayEquals(exp.toByteArray(), bOut.toByteArray());
		}
	}

	@Test
	void testDeserializeValue() throws Exception {
		ILTagFactory f = new TagFactory(false);

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			String[] keys = TagTestUtils.createSampleStrings(size);
			ILTag[] values = TagTestUtils.createSampleTags(size);

			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encode(size, out);
				for (int i = 0; i < size; i++) {
					StringTag.serializeStandardStringTag(keys[i], out);
					values[i].serialize(out);
				}
			}
			byte[] serialized = exp.toByteArray();
			{
				DictonaryTag t = new DictonaryTag(1234);
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				t.deserializeValue(f, serialized.length, in);
				assertEquals(size, t.getValues().size());

				int i = 0;
				for (Map.Entry<String, ILTag> entry : t.getValues().entrySet()) {
					assertEquals(keys[i], entry.getKey());
					TagTestUtils.assertTagEquals(values[i], entry.getValue());
					i++;
				}
			}
			{
				DictonaryTag t = new DictonaryTag(1234);
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(f, serialized.length - 1, in);
				});
			}
			{
				DictonaryTag t = new DictonaryTag(1234);
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(f, serialized.length + 1, in);
				});
			}
		}
		// Bad counter
		{
			byte[] serialized = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00 };
			DictonaryTag t = new DictonaryTag(1234);
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(f, serialized.length, in);
			});
		}
		// Bad tag
		{
			byte[] serialized = new byte[] { 0x01, 0x011, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
			DictonaryTag t = new DictonaryTag(1234);
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(f, serialized.length, in);
			});
		}
	}

	@Test
	void testCreateStandard() {
		DictonaryTag t = DictonaryTag.createStandard();
		assertEquals(TagID.IL_DICTIONARY_TAG_ID, t.getTagID());
		assertEquals(0, t.getValues().size());
	}

}
