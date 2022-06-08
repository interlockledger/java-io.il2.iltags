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

class Int8TagTest {

	private static final byte[] BIN_SAMPLE = { (byte) 0xCA, (byte) 0x7F };

	@Test
	void testInt8Tag() {
		Int8Tag t = new Int8Tag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(BIN_SAMPLE);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);		
		Int8Tag t = new Int8Tag(123456);
		t.deserializeValue(null, 1, in);
		assertEquals((byte) 0xCA, t.getValue());
	}

	@Test
	void testSetValue() {
		Int8Tag t = new Int8Tag(123456);
		t.setValue((byte) 0xCA);
		assertEquals((byte) 0xCA, t.getValue());
	}

	@Test
	void testGetUnsignedValue() throws Exception {
		Int8Tag t = new Int8Tag(123456);
		t.setValue((byte) 0x7F);		
		assertEquals((byte) 0x7F, t.getValue());
	}

	@Test
	void testSetUnsignedValue() throws IOException, ILTagException {	
		Int8Tag t = new Int8Tag(123456);
		t.setValue((byte) 0x7F);
		assertEquals((byte) 0x7F, t.getValue());
	}

	@Test
	void testGetValueSize() {
		Int8Tag t = new Int8Tag(123456);
		assertEquals(1, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		ByteBuffer buff = ByteBuffer.allocate(2);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		Int8Tag t = new Int8Tag(123456);

		t.setValue((byte) 0xCA);
		t.serializeValue(out);
		t.setValue((byte) 0x7F);
		t.serializeValue(out);
		assertArrayEquals(BIN_SAMPLE, buff.array());
		assertThrows(IOException.class, () -> {
			t.serializeValue(out);
		});
	}

	@Test
	void testDeserializeValue() throws Exception {
		ByteBuffer buff = ByteBuffer.wrap(BIN_SAMPLE);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		Int8Tag t = new Int8Tag(123456);
		t.deserializeValue(null, 1, in);
		assertEquals((byte) 0xCA, t.getValue());
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 2, in);
		});			
		t.deserializeValue(null, 1, in);
		assertEquals((byte) 0x7F, t.getValue());
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 1, in);
		});	
	}
}
