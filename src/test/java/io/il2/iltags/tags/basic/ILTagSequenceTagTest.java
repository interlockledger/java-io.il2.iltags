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
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagFactory;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.factory.TagFactory;

class ILTagSequenceTagTest {

	@Test
	void testILTagSequenceTag() {
		ILTagSequenceTag t = new ILTagSequenceTag(1234);
		assertEquals(1234, t.getTagID());
		assertEquals(0, t.getValues().size());
	}

	@Test
	void testGetValueSize() {
		for (int size : new int[] { 0, 1, 255, 65535 }) {
			ILTag[] sample = TagTestUtils.createSampleTags(size);
			long exp = 0;
			for (ILTag v : sample) {
				exp += v.getTagSize();
			}
			ILTagSequenceTag t = new ILTagSequenceTag(1234);
			t.getValues().addAll(Arrays.asList(sample));
			assertEquals(exp, t.getValueSize());
		}
	}

	@Test
	void testSerializeValue() throws Exception {
		for (int size : new int[] { 0, 1, 255, 65535 }) {
			ILTag[] sample = TagTestUtils.createSampleTags(size);
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				for (ILTag v : sample) {
					v.serialize(out);
				}
			}

			ILTagSequenceTag t = new ILTagSequenceTag(1234);
			t.getValues().addAll(Arrays.asList(sample));
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

		ILTagSequenceTag t = new ILTagSequenceTag(1234);

		for (int size : new int[] { 0, 1, 255, 65535 }) {
			ILTag[] sample = TagTestUtils.createSampleTags(size);
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				for (ILTag v : sample) {
					v.serialize(out);
				}
			}
			byte[] serialized = exp.toByteArray();
			{
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				t.deserializeValue(f, serialized.length, in);
				assertEquals(size, t.getValues().size());
				for (int i = 0; i < size; i++) {
					TagTestUtils.assertTagEquals(sample[i], t.getValues().get(i));
				}
			}
			if (size > 0) {
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(f, serialized.length - 1, in);
				});
			}
			{
				ByteBufferDataInput in = new ByteBufferDataInput(serialized);
				assertThrows(CorruptedTagException.class, () -> {
					t.deserializeValue(f, serialized.length + 1, in);
				});
			}
		}
		// Corrupted tag
		{
			byte[] serialized = new byte[] { 0x11, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
			ByteBufferDataInput in = new ByteBufferDataInput(serialized);
			assertThrows(CorruptedTagException.class, () -> {
				t.deserializeValue(f, serialized.length, in);
			});
		}
	}

	@Test
	void testCreateStandard() {
		ILTagSequenceTag t = ILTagSequenceTag.createStandard();
		assertEquals(TagID.IL_ILTAGSEQ_TAG_ID, t.getTagID());
		assertEquals(0, t.getValues().size());
	}

}
