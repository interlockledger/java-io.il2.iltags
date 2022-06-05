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
import java.nio.CharBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.TestUtils;
import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnexpectedTagException;
import io.il2.iltags.utils.UTF8Utils;

class StringTagTest {

	@Test
	void testStringTag() {
		StringTag t = new StringTag(123123);
		assertEquals(null, t.getValue());
		assertEquals(123123, t.getTagID());
	}

	@Test
	void testGetSetValue() {
		StringTag t = new StringTag(123123);

		assertEquals(null, t.getValue());
		t.setValue("test");
		assertEquals("test", t.getValue());
	}

	@Test
	void testGetValueSize() {
		StringTag t = new StringTag(123123);

		t.setValue(null);
		assertEquals(0, t.getValueSize());
		t.setValue("");
		assertEquals(0, t.getValueSize());
		t.setValue("test");
		assertEquals(4, t.getValueSize());
		t.setValue(TestUtils.SAMPLE);
		assertEquals(TestUtils.SAMPLE_BIN.length, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		StringTag t = new StringTag(123123);

		t.setValue(null);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] {}, bOut.toByteArray());

		t.setValue("");
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] {}, bOut.toByteArray());

		t.setValue("test");
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { 't', 'e', 's', 't' }, bOut.toByteArray());

		t.setValue(TestUtils.SAMPLE);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(TestUtils.SAMPLE_BIN, bOut.toByteArray());
	}

	@Test
	void testDeserializeValue() throws Exception {
		StringTag t = new StringTag(123123);

		for (int size = 0; size < 65536; size += 10000) {
			String s = TestUtils.genRandomString(size, true);
			ByteBuffer enc = UTF8Utils.newEncoder().encode(CharBuffer.wrap(s));
			t.deserializeValue(null, enc.limit(), new ByteBufferDataInput(enc));
			assertEquals(s, t.getValue());
		}

		// Invalid UTF-8 string
		ByteBuffer enc = ByteBuffer.wrap(new byte[] { 'a', (byte) 0x80, 'b' });
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, enc.limit(), new ByteBufferDataInput(enc));
		});
	}

	@Test
	void testWriteUTF8String() throws Exception {

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			StringTag.writeUTF8String(null, out);
		}
		assertArrayEquals(new byte[] {}, bOut.toByteArray());

		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			StringTag.writeUTF8String("", out);
		}
		assertArrayEquals(new byte[] {}, bOut.toByteArray());

		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			StringTag.writeUTF8String("test", out);
		}
		assertArrayEquals(new byte[] { 't', 'e', 's', 't' }, bOut.toByteArray());

		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			StringTag.writeUTF8String(TestUtils.SAMPLE, out);
		}
		assertArrayEquals(TestUtils.SAMPLE_BIN, bOut.toByteArray());
	}

	@Test
	void testReadUTF8String() throws Exception {

		for (int size = 0; size < 65536; size += 10000) {
			String s = TestUtils.genRandomString(size, true);
			ByteBuffer enc = UTF8Utils.newEncoder().encode(CharBuffer.wrap(s));
			String deserialized = StringTag.readUTF8String(enc.limit(), new ByteBufferDataInput(enc));
			assertEquals(s, deserialized);
		}

		// Invalid UTF-8 string
		ByteBuffer enc = ByteBuffer.wrap(new byte[] { 'a', (byte) 0x80, 'b' });
		assertThrows(CorruptedTagException.class, () -> {
			StringTag.readUTF8String(enc.limit(), new ByteBufferDataInput(enc));
		});
	}

	@Test
	void testGetStringTagSize() {
		for (long id : TestUtils.SAMPLE_IDS) {
			for (int size = 0; size < 65536; size += 15000) {
				String s = TestUtils.genRandomString(size, true);
				int encSize = UTF8Utils.getEncodedSize(s);
				long exp = ILIntEncoder.encodedSize(id) + ILIntEncoder.encodedSize(encSize) + encSize;
				assertEquals(exp, StringTag.getStringTagSize(id, s));
			}
		}
	}

	@Test
	void testGetStandardStringTagSize() {
		for (int size = 0; size < 65536; size += 15000) {
			String s = TestUtils.genRandomString(size, true);
			int encSize = UTF8Utils.getEncodedSize(s);
			long exp = 1 + ILIntEncoder.encodedSize(encSize) + encSize;
			assertEquals(exp, StringTag.getStandardStringTagSize(s));
		}
	}

	@Test
	void testSerializeStringTag() throws Exception {
		for (long id : TestUtils.SAMPLE_IDS) {
			for (int size = 0; size < 65536; size += 15000) {
				String s = TestUtils.genRandomString(size, true);
				ByteBuffer enc = UTF8Utils.newEncoder().encode(CharBuffer.wrap(s));
				ByteArrayOutputStream exp = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(exp)) {
					ILIntEncoder.encode(id, out);
					ILIntEncoder.encode(enc.limit(), out);
					out.write(enc.array(), 0, enc.limit());
				}
				ByteArrayOutputStream serialized = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(serialized)) {
					StringTag.serializeStringTag(id, s, out);
				}
				assertArrayEquals(exp.toByteArray(), serialized.toByteArray());
			}
		}
	}

	@Test
	void testSerializeStandardStringTag() throws Exception {
		for (int size = 0; size < 65536; size += 15000) {
			String s = TestUtils.genRandomString(size, true);
			ByteBuffer enc = UTF8Utils.newEncoder().encode(CharBuffer.wrap(s));
			ByteArrayOutputStream exp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(exp)) {
				ILIntEncoder.encode(TagID.IL_STRING_TAG_ID, out);
				ILIntEncoder.encode(enc.limit(), out);
				out.write(enc.array(), 0, enc.limit());
			}
			ByteArrayOutputStream serialized = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(serialized)) {
				StringTag.serializeStandardStringTag(s, out);
			}
			assertArrayEquals(exp.toByteArray(), serialized.toByteArray());
		}
	}

	@Test
	void testDeserializeStringTag() throws Exception {
		for (long id : TestUtils.SAMPLE_IDS) {
			for (int size = 0; size < 65536; size += 15000) {
				String exp = TestUtils.genRandomString(size, true);
				StringTag t = new StringTag(id);
				t.setValue(exp);
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bOut)) {
					t.serialize(out);
				}
				byte[] serialized = bOut.toByteArray();
				String s = StringTag.deserializeStringTag(id, new ByteBufferDataInput(serialized));
				assertEquals(exp, s);

				assertThrows(UnexpectedTagException.class, () -> {
					StringTag.deserializeStringTag(id + 1, new ByteBufferDataInput(serialized));
				});
			}
		}
		assertThrows(CorruptedTagException.class, () -> {
			StringTag.deserializeStringTag(0x10, new ByteBufferDataInput(new byte[] { 0x10, 0x01, (byte) 0x80 }));
		});
	}

	@Test
	void testDeserializeStandardStringTag() throws Exception {
		for (int size = 0; size < 65536; size += 15000) {
			String exp = TestUtils.genRandomString(size, true);
			StringTag t = StringTag.createStandard();
			t.setValue(exp);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				t.serialize(out);
			}
			byte[] serialized = bOut.toByteArray();
			String s = StringTag.deserializeStandardStringTag(new ByteBufferDataInput(serialized));
			assertEquals(exp, s);
		}

		assertThrows(UnexpectedTagException.class, () -> {
			StringTag.deserializeStandardStringTag(new ByteBufferDataInput(new byte[] { 0x10, 0x01, (byte) 0x80 }));
		});
		assertThrows(CorruptedTagException.class, () -> {
			StringTag.deserializeStandardStringTag(new ByteBufferDataInput(new byte[] { 0x11, 0x01, (byte) 0x80 }));
		});
	}

	@Test
	void testCreateStandard() {
		StringTag t = StringTag.createStandard();
		assertEquals(null, t.getValue());
		assertEquals(TagID.IL_STRING_TAG_ID, t.getTagID());
	}
}
