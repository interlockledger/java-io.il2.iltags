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
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.TagID;

class SignedILIntTagTest {
	
	// -97, 
	private static final long[] SAMPLE_IDS = { 0x9D, 0x9D9D, 0xFFD99D9D};	
	
	private static final long sLong1 = -98723886;

	@Test
	void testSignedILIntTag() {
		SignedILIntTag t = new SignedILIntTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		SignedILIntTag t = new SignedILIntTag(123456);
		t.setValue(sLong1);
		assertEquals(sLong1, t.getValue());
	}

	@Test
	void testSetValue() {
		SignedILIntTag t = new SignedILIntTag(123456);
		t.setValue(sLong1);
		assertEquals(sLong1, t.getValue());
	}

	@Test
	void testGetValueSize() {
		SignedILIntTag t = new SignedILIntTag(123456);
		assertEquals(1, t.getValueSize());
		t.setValue(sLong1);
		assertEquals(5, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws IOException {
		fail("Not yet implemented");
	}

	@Test
	void testDeserializeValue() {
		fail("Not yet implemented");		
	}
	
	@Test
	void testCreateStandard() {
		SignedILIntTag t = SignedILIntTag.createStandard();
		assertEquals(TagID.IL_SIGNED_ILINT_TAG_ID, t.getTagID());		
	}	

}
