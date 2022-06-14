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

class Int16TagTest {

	private static final byte[] SAMPLE_IDS = {(byte)0xfe, (byte)0xab};
	
	@Test
	void testInt16Tag() {
		Int16Tag t = new Int16Tag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() throws IOException, ILTagException {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short)0xFEl);
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testSetValue() {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short) 0xFEl);
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testGetUnsignedValue() {
		Int16Tag t = new Int16Tag(123456);
		short uInt = (short) 0xFEl & 0xffff;
		t.setValue(uInt);		
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testSetUnsignedValue() {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short)( 0xFEl & 0xffff));			
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testGetValueSize() {
		Int16Tag t = new Int16Tag(123456);
		assertEquals(2, t.getValueSize());
		t.setValue((short)( 0xFEl & 0xffff));		
		assertEquals(2, t.getValueSize());		
	}

	@Test
	void testSerializeValue() throws IOException {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short) 0xfeab);
		ByteBuffer buff = ByteBuffer.allocate(2);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		Int16Tag t = new Int16Tag(123456);
		t.deserializeValue(null, 2, in);
		assertEquals((short) 0xFEAB, t.getValue());
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});			
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 2, in);
		});
		
	}

	@Test
	void testCreateStandardSigned() {
		Int16Tag t = Int16Tag.createStandardSigned();
		assertEquals(t.getTagID(), TagID.IL_INT16_TAG_ID);
	}

	@Test
	void testCreateStandardUnsigned() {
		Int16Tag t = Int16Tag.createStandardUnsigned();
		assertEquals(t.getTagID(), TagID.IL_UINT16_TAG_ID);
	}

}
