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
package io.il2.iltags.tags;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.basic.BooleanTag;

class ILTagUtilsTest {

	@Test
	void testAssertTagSizeLimit() throws Exception {
		ILTagUtils.assertTagSizeLimit(0);
		ILTagUtils.assertTagSizeLimit(ILTag.MAX_TAG_VALUE_SIZE);

		assertThrows(TagTooLargeException.class, () -> {
			ILTagUtils.assertTagSizeLimit(ILTag.MAX_TAG_VALUE_SIZE + 1);
		});
		assertThrows(TagTooLargeException.class, () -> {
			ILTagUtils.assertTagSizeLimit(-1);
		});
	}

	@Test
	void testReadHeader() throws Exception {

		ILTagHeader h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 0 }));
		assertEquals(0, h.tagId);
		assertEquals(0, h.valueSize);

		h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 1, 1 }));
		assertEquals(1, h.tagId);
		assertEquals(1, h.valueSize);

		h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 16, 17 }));
		assertEquals(16, h.tagId);
		assertEquals(17, h.valueSize);

		// Bad ID
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08, 0 }));
		});

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 16, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }));
		});
	}

	@Test
	void testReadILInt() throws Exception {

		assertEquals(0xF7l, ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xF7 }), "Test"));

		assertEquals(0xFFFF_FFFF_FFFF_FFFFl,
				ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x07 }), "Test"));

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }), "Test");
		});
	}

	@Test
	void testReadSignedILInt() throws Exception {

		assertEquals(0, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x0 }), "Test"));
		assertEquals(-1, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x1 }), "Test"));
		assertEquals(1, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x2 }), "Test"));
		assertEquals(9223372036854775807l,
				ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x06 }), "Test"));
		assertEquals(-9223372036854775808l,
				ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x07 }), "Test"));

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }), "Test");
		});

	}

	@Test
	void testAssertArraySize() throws Exception {

		ILTagUtils.assertArraySize(0, 0, 0);

		ILTagUtils.assertArraySize(1, 1, 1);
		ILTagUtils.assertArraySize(1, 1, 2);

		ILTagUtils.assertArraySize(2, 2, 4);
		ILTagUtils.assertArraySize(2, 2, 5);

		// Special case with count as 2^64 -1 and value size as 2^64 -1 and minimum
		// entry size = 1
		ILTagUtils.assertArraySize(-1, 1, -1);

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(1, 1, 0);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(2, 2, 3);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(-1, 1, 1);
		});
	}

	@Test
	void testWriteTagOrNull() throws Exception {

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			ILTagUtils.writeTagOrNull(null, out);
		}
		assertArrayEquals(new byte[] { 0 }, bOut.toByteArray());

		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			ILTagUtils.writeTagOrNull(BooleanTag.createStandard(), out);
		}
		assertArrayEquals(new byte[] { 1, 0 }, bOut.toByteArray());

		// Map exception
		ILTag t = mock(ILTag.class);
		doThrow(ILTagException.class).when(t).serialize(any());
		bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			assertThrows(IOException.class, () -> {
				ILTagUtils.writeTagOrNull(t, out);
			});
		}
	}
}
