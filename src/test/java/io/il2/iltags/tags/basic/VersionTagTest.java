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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class VersionTagTest {

	@Test
	void testInt16Tag() {
		VersionTag t = new VersionTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetSetValue() throws IOException, ILTagException {
		VersionTag t = new VersionTag(123456);

		assertEquals(0, t.getMajor());
		assertEquals(0, t.getMinor());
		assertEquals(0, t.getRevision());
		assertEquals(0, t.getBuild());

		t.setMajor(1);
		t.setMinor(2);
		t.setRevision(3);
		t.setBuild(4);

		assertEquals(1, t.getMajor());
		assertEquals(2, t.getMinor());
		assertEquals(3, t.getRevision());
		assertEquals(4, t.getBuild());
	}

	@Test
	void testGetValueSize() {
		VersionTag t = new VersionTag(123456);
		assertEquals(4 * 4, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		VersionTag t = new VersionTag(123456);

		ByteBuffer buff = ByteBuffer.allocate(16);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, buff.array());

		t.setMajor(1);
		t.setMinor(2);
		t.setRevision(3);
		t.setBuild(4);

		buff = ByteBuffer.allocate(16);
		out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(new byte[] { 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4 }, buff.array());
	}

	@Test
	void testDeserializeValue() throws Exception {
		VersionTag t = new VersionTag(123456);

		byte[] serialized = new byte[] { 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4 };
		ByteBufferDataInput in = new ByteBufferDataInput(serialized);
		t.deserializeValue(null, serialized.length, in);
		assertEquals(1, t.getMajor());
		assertEquals(2, t.getMinor());
		assertEquals(3, t.getRevision());
		assertEquals(4, t.getBuild());

		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(serialized);
			t.deserializeValue(null, 15, in2);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ByteBufferDataInput in2 = new ByteBufferDataInput(serialized);
			t.deserializeValue(null, 17, in2);
		});
	}

	@Test
	void testCreateStandard() {
		VersionTag t = VersionTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_VERSION_TAG_ID);
	}
}
