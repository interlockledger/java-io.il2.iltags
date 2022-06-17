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
import java.io.EOFException;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class RangeTagTest {

	@Test
	void testRangeTag() {
		RangeTag t = new RangeTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetFirst() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getFirst());
	}

	@Test
	void testSetFirst() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getFirst());
		t.setFirst(1);
		assertEquals(1, t.getFirst());
	}

	@Test
	void testGetCount() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getCount());
		t.setCount(5);
		assertEquals(5, t.getCount());
	}

	@Test
	void testSetCount() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getCount());
		t.setCount(5);
		assertEquals(5, t.getCount());
	}

	@Test
	void testGetValueSize() {
		RangeTag t = new RangeTag(123456);
		assertEquals(3, t.getValueSize()); // 3 = first(1) + 2
		t.setCount(5);
		assertEquals(3, t.getValueSize());
		t.setFirst(5);
		assertEquals(3, t.getValueSize());

	}

	@Test
	void testSerializeValue() throws Exception {
		RangeTag t = new RangeTag(123456);

		t.setFirst(32);
		t.setCount(12345);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { 0x20, 0x30, 0x39 }, bOut.toByteArray());

		t.setFirst(0xFACBD2);
		t.setCount(54321);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { (byte) 0xFA, (byte) 0xFA, (byte) 0xCA, (byte) 0xDA, (byte) 0xD4, 0x31 },
				bOut.toByteArray());
	}

	@Test
	void testDeserializeValue() throws Exception {
		RangeTag t = new RangeTag(123456);

		ByteBufferDataInput in = new ByteBufferDataInput(new byte[] { 0x20, 0x30, 0x39 });
		t.deserializeValue(null, 3, in);
		assertEquals(32, t.getFirst());
		assertEquals(12345, t.getCount());

		in = new ByteBufferDataInput(
				new byte[] { (byte) 0xFA, (byte) 0xFA, (byte) 0xCA, (byte) 0xDA, (byte) 0xD4, 0x31 });
		t.deserializeValue(null, 6, in);
		assertEquals(0xFACBD2, t.getFirst());
		assertEquals(54321, t.getCount());

		assertThrows(EOFException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(
					new byte[] { (byte) 0xFA, (byte) 0xFA, (byte) 0xCA, (byte) 0xDA, (byte) 0xD4, 0x31 });
			t.deserializeValue(null, 5, in2);
		});

		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(
					new byte[] { (byte) 0xFA, (byte) 0xFA, (byte) 0xCA, (byte) 0xDA, (byte) 0xD4, 0x31 });
			t.deserializeValue(null, 7, in2);
		});

		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(new byte[] { (byte) 0xFA, (byte) 0xFA });
			t.deserializeValue(null, 2, in2);
		});
	}

	@Test
	void testCreateStandard() {
		RangeTag t = RangeTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_RANGE_TAG_ID);
	}

}
