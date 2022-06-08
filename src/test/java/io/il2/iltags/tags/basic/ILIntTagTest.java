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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;

class ILIntTagTest {
	
	// sample with all kind of ILIntTag ( 1 to 9 bytes)
	private static final long[] SAMPLE_IDS = { 0x10, 0xFEl, 0xFEDCl, 0xFEDCBAl, 0xFEDCBA98l, 0xFEDCBA9876l,
			0xFEDCBA987654l, 0xFEDCBA98765432l, 0xFEDCBA9876543210l };

	@Test
	void testILIntTag() {
		ILIntTag t = new ILIntTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		ILIntTag t = new ILIntTag(123456);
		t.setValue(123);
		assertEquals(123, t.getValue());
	}

	@Test
	void testSetValue() {
		ILIntTag t = new ILIntTag(123456);
		t.setValue(123);
		assertEquals(123, t.getValue());
	}

	@Test
	void testGetValueSize() {
		ILIntTag t = new ILIntTag(123456);
		assertEquals(1, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		ILIntTag tag = new ILIntTag(123456);
		for (long v:SAMPLE_IDS) {
			tag.setValue(v);
			ByteBuffer exp = ByteBuffer.allocate(ILIntEncoder.encodedSize(v));
			ByteBufferDataOutput expOut = new ByteBufferDataOutput(exp);
			ILIntEncoder.encode(v, expOut);
			
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				tag.serializeValue(out);
			}
			assertArrayEquals(exp.array(), bOut.toByteArray());
		}
	}
	

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ILIntTag tag = new ILIntTag(123456);
		for (long v:SAMPLE_IDS) {
			ByteBuffer exp = ByteBuffer.allocate(ILIntEncoder.encodedSize(v));
			ByteBufferDataOutput expOut = new ByteBufferDataOutput(exp);
			ILIntEncoder.encode(v, expOut);
			
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(exp.array()))) {
				tag.deserializeValue(null, 123, in);
			}
			assertEquals(v, tag.getValue());			
		}
		assertThrows(CorruptedTagException.class, () -> {
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x08}))) {
				tag.deserializeValue(null, 123, in);
			}			
		});		
	}

}
