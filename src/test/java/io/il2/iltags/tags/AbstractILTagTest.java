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
package io.il2.iltags.tags;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class AbstractILTagTest {

	private class BaseILTagX extends AbstractILTag {

		private final long valueSize;

		public BaseILTagX(long tagId, long valueSize) {
			super(tagId);
			this.valueSize = valueSize;
		}

		@Override
		public long getValueSize() {
			return valueSize;
		}

		@Override
		public void serializeValue(DataOutput out) throws IOException {
			for (long i = 0; i < valueSize; i++) {
				out.write((byte) i);
			}
		}

		@Override
		public void deserializeValue(ILTagFactory factory, long valueSize, DataInput in)
				throws IOException, ILTagException {
			if (this.valueSize != valueSize) {
				throw new CorruptedTagException("Tag is corrupted.");
			}
			for (long i = 0; i < valueSize; i++) {
				if (in.readUnsignedByte() != (int) (i & 0xFF)) {
					throw new CorruptedTagException("Tag is corrupted because the read value is wrong.");
				}
			}
		}
	}

	@Test
	void testBaseILTag() {
		BaseILTagX t = new BaseILTagX(12345, 123);
		assertEquals(12345, t.getTagID());
	}

	@Test
	void testIsImplicit() {
		BaseILTagX t = new BaseILTagX(0, 123);
		assertTrue(t.isImplicit());
		t = new BaseILTagX(15, 123);
		assertTrue(t.isImplicit());

		t = new BaseILTagX(16, 123);
		assertFalse(t.isImplicit());
		t = new BaseILTagX(-1, 123);
		assertFalse(t.isImplicit());
	}

	@Test
	void testIsReserved() {
		BaseILTagX t = new BaseILTagX(0, 123);
		assertTrue(t.isReserved());
		t = new BaseILTagX(31, 123);
		assertTrue(t.isReserved());

		t = new BaseILTagX(32, 123);
		assertFalse(t.isReserved());
		t = new BaseILTagX(-1, 123);
		assertFalse(t.isReserved());
	}

	@Test
	void testGetTagSize() {
		BaseILTagX t = new BaseILTagX(0, 0);
		assertEquals(1, t.getTagSize());

		t = new BaseILTagX(2, 10);
		assertEquals(1 + 10, t.getTagSize());

		t = new BaseILTagX(0xFFFF_FFFF_FFFF_FFFFl, 0xFFFF_FFFFl);
		assertEquals(9 + 5 + 0xFFFF_FFFFl, t.getTagSize());
	}

	@Test
	void testSerializeHeader() throws Exception {
		// Implicit
		BaseILTagX t = new BaseILTagX(TagID.IL_INT16_TAG_ID, 2);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeHeader(out);
		}
		assertArrayEquals(new byte[] { 0x04 }, bOut.toByteArray());

		// Explicit
		t = new BaseILTagX(255, 65536);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeHeader(out);
		}
		assertArrayEquals(new byte[] { (byte) 0xF8, (byte) 0x07, (byte) 0xF9, (byte) 0xFF, (byte) 0x08 },
				bOut.toByteArray());

		t = new BaseILTagX(0xFFFF_FFFF_FFFF_FFFFl, 0x1234567890l);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeHeader(out);
		}
		assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0x07, (byte) 0xFC, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x77,
				(byte) 0x98 }, bOut.toByteArray());
	}

	@Test
	void testSerialize() throws Exception {
		// Simulate an implicit tag
		BaseILTagX t = new BaseILTagX(TagID.IL_INT16_TAG_ID, 2);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serialize(out);
		}
		assertArrayEquals(new byte[] { 0x04, 0x00, 0x01 }, bOut.toByteArray());

		// Simulate an implicit tag
		t = new BaseILTagX(16, 2);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serialize(out);
		}
		assertArrayEquals(new byte[] { 0x10, 0x02, 0x00, 0x01 }, bOut.toByteArray());
	}

	@Test
	void testToBytes() throws Exception {
		// Simulate an implicit tag
		BaseILTagX t = new BaseILTagX(TagID.IL_INT16_TAG_ID, 2);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serialize(out);
		}
		assertArrayEquals(bOut.toByteArray(), t.toBytes());

		// Simulate an implicit tag
		t = new BaseILTagX(16, 2);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serialize(out);
		}
		assertArrayEquals(bOut.toByteArray(), t.toBytes());
	}

	@Test
	void testAssertTagSizeLimit() throws Exception {
		AbstractILTag.assertTagSizeLimit(0);
		AbstractILTag.assertTagSizeLimit(ILTag.MAX_TAG_SIZE);

		assertThrows(TagTooLargeException.class, () -> {
			AbstractILTag.assertTagSizeLimit(ILTag.MAX_TAG_SIZE + 1);
		});
		assertThrows(TagTooLargeException.class, () -> {
			AbstractILTag.assertTagSizeLimit(-1);
		});
	}
}
