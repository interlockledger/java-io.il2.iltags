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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class FloatTagTest {
	
	// Representation of float 3.6f
	private static final byte[] SAMPLE_IDS = {(byte)0x40, (byte)0x66, (byte) 0x66, (byte) 0x66};		

	@Test
	void testFloatTag() {
		FloatTag t = new FloatTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		FloatTag t = new FloatTag(123456);
		t.setValue(3.6f);
		assertEquals(3.6f, t.getValue());
	}

	@Test
	void testSetValue() {
		FloatTag t = new FloatTag(123456);
		t.setValue(3.6f);
		assertEquals(3.6f, t.getValue());
	}

	@Test
	void testGetValueSize() {
		FloatTag t = new FloatTag(123456);
		assertEquals(4, t.getValueSize());
		t.setValue(3.6f);
		assertEquals(4, t.getValueSize());		
	}

	@Test
	void testSerializeValue() throws IOException {
		FloatTag t = new FloatTag(123456);
		t.setValue(3.6f);
		ByteBuffer buff = ByteBuffer.allocate(4);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
		assertThrows(IOException.class, () -> {
			t.serializeValue(out);
		});		
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		FloatTag t = new FloatTag(123456);
		t.deserializeValue(null, 4, in);
		assertEquals(3.6f, t.getValue());
		
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});			
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});		
		
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 4, in);
		});			
	}

	@Test
	void testCreateStandard() {
		FloatTag t = FloatTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BIN32_TAG_ID);
	}

}
