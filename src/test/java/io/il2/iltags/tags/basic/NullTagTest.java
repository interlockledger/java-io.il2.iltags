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

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.TagID;

class NullTagTest {

	@Test
	void testNullTag() {
		NullTag t = new NullTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValueSize() {
		NullTag t = new NullTag(123456);
		assertEquals(0, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		ByteBuffer buff = ByteBuffer.allocate(0);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);

		NullTag t = new NullTag(123456);
		t.serializeValue(out);
	}

	@Test
	void testDeserializeValue() throws Exception {
		ByteBuffer buff = ByteBuffer.allocate(0);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		NullTag t = new NullTag(123456);
		t.deserializeValue(null, 0, in);
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});
	}

	@Test
	void testCreateStandard() {
		NullTag t = NullTag.createStandard();
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());
	}
}
