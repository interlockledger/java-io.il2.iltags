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

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class BooleanTagTest {

	@Test
	void testBooleanTag() {
		BooleanTag t = new BooleanTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		BooleanTag t = new BooleanTag(123456);
		t.setValue(true);
		assertEquals(true, t.getValue());
		t.setValue(false);
		assertEquals(false, t.getValue());
	}

	@Test
	void testSetValue() {
		BooleanTag t = new BooleanTag(123456);
		t.setValue(true);
		assertEquals(true, t.getValue());
		t.setValue(false);
		assertEquals(false, t.getValue());
	}

	@Test
	void testGetValueSize() {
		BooleanTag t = new BooleanTag(123456);
		assertEquals(1, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		BooleanTag t = new BooleanTag(123456);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { 0x00 }, bOut.toByteArray());

		t.setValue(true);
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { 0x01 }, bOut.toByteArray());
	}

	@Test
	void testDeserializeValue() throws Exception {
		BooleanTag t = new BooleanTag(123456);

		ByteBufferDataInput in = new ByteBufferDataInput(new byte[] { 0x00 });
		t.deserializeValue(null, 1, in);
		assertFalse(t.getValue());

		in = new ByteBufferDataInput(new byte[] { 0x01 });
		t.deserializeValue(null, 1, in);
		assertTrue(t.getValue());

		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(new byte[] { (byte) 0x02 });
			t.deserializeValue(null, 1, in2);
		});

		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(new byte[] { (byte) 0x00 });
			t.deserializeValue(null, 0, in2);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(new byte[] { (byte) 0x00 });
			t.deserializeValue(null, 2, in2);
		});
	}

	@Test
	void testCreateStandard() {
		BooleanTag t = BooleanTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BOOL_TAG_ID);
	}

}
