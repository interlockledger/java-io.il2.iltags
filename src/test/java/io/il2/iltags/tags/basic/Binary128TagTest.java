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

class Binary128TagTest {
	
	private static final byte[] Binary128_1 = {(byte)0xFE, (byte)0xAB, (byte) 0xFC, (byte) 0xDD, (byte)0xAD, (byte)0xCB, (byte) 0xFB, (byte) 0xEE,
			(byte)0xFE, (byte)0xAB, (byte) 0xFC, (byte) 0xDD, (byte)0xAD, (byte)0xCB, (byte) 0xFB, (byte) 0xEE};

	@Test
	void testBinary128Tag() {
		Binary128Tag t = new Binary128Tag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		Binary128Tag t = new Binary128Tag(123456);
		t.setValue(Binary128_1);
		assertArrayEquals(Binary128_1, t.getValue());
	}

	@Test
	void testSetValue() {
		Binary128Tag t = new Binary128Tag(123456);
		t.setValue(Binary128_1);
		assertArrayEquals(Binary128_1, t.getValue());
	}

	@Test
	void testGetValueSize() {
		Binary128Tag t = new Binary128Tag(123456);
		assertEquals(16, t.getValueSize());
		t.setValue(Binary128_1);
		assertEquals(16, t.getValueSize());		
	}

	@Test
	void testSerializeValue() throws IOException {
		Binary128Tag t = new Binary128Tag(123456);
		t.setValue(Binary128_1);
		ByteBuffer buff = ByteBuffer.allocate(16);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(Binary128_1,buff.array());
		assertThrows(IOException.class, () -> {
			t.serializeValue(out);
		});				
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		Binary128Tag t = new Binary128Tag(123456);
		ByteBufferDataInput in = new ByteBufferDataInput(Binary128_1);
		t.deserializeValue(null, Binary128_1.length, in);
		assertArrayEquals(Binary128_1, t.getValue());	
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});				
	}

	@Test
	void testCreateStandard() {
		Binary128Tag t = Binary128Tag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BIN128_TAG_ID);
	}

}
