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
package io.il2.iltags.tags.payload;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagFactory;

class PayloadedTagTest {

	@Test
	void testPayloadedTag() {
		TagPayload p = mock(TagPayload.class);
		PayloadedTag<TagPayload> t = new PayloadedTag<>(1234, p);

		assertEquals(1234, t.getTagID());
		assertSame(p, t.getPayload());
	}

	@Test
	void testGetValueSize() {
		TagPayload p = mock(TagPayload.class);
		PayloadedTag<TagPayload> t = new PayloadedTag<>(1234, p);

		when(p.getValueSize()).thenReturn(Long.valueOf(4321));
		assertEquals(4321, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws Exception {
		TagPayload p = mock(TagPayload.class);
		PayloadedTag<TagPayload> t = new PayloadedTag<>(1234, p);

		when(p.getValueSize()).thenReturn(Long.valueOf(2));
		doAnswer((invocation) -> {
			DataOutput out = invocation.getArgument(0);
			out.writeByte(0);
			out.writeByte(1);
			return 0;
		}).when(p).serializeValue(any());

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			t.serializeValue(out);
		}
		assertArrayEquals(new byte[] { 0, 1 }, bOut.toByteArray());

		doThrow(IOException.class).when(p).serializeValue(any());
		assertThrows(IOException.class, () -> {
			try (DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream())) {
				t.serializeValue(out);
			}
		});
	}

	@Test
	void testDeserializeValue() throws Exception {
		ILTagFactory f = mock(ILTagFactory.class);
		TagPayload p = mock(TagPayload.class);
		PayloadedTag<TagPayload> t = new PayloadedTag<>(1234, p);

		when(p.getValueSize()).thenReturn(Long.valueOf(2));
		doAnswer((invocation) -> {
			ILTagFactory f2 = invocation.getArgument(0);
			if (f2 != f) {
				throw new IllegalArgumentException();
			}
			long valueSize = invocation.getArgument(1);
			if (valueSize != 2) {
				throw new CorruptedTagException();
			}
			DataInput in = invocation.getArgument(2);
			if (in.readByte() != 0) {
				throw new CorruptedTagException();
			}
			if (in.readByte() != 1) {
				throw new CorruptedTagException();
			}
			return 0;
		}).when(p).deserializeValue(any(), anyLong(), any());
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[] { 0, 1 }))) {
			t.deserializeValue(f, 2, in);
		}

		// Bad data
		assertThrows(CorruptedTagException.class, () -> {
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[] { 0 }))) {
				t.deserializeValue(f, 2, in);
			}
		});

		// Bad value
		assertThrows(CorruptedTagException.class, () -> {
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[] { 1, 1 }))) {
				t.deserializeValue(f, 2, in);
			}
		});

		// Bad consumer
		when(p.getValueSize()).thenReturn(Long.valueOf(2));
		doAnswer((invocation) -> {
			ILTagFactory f2 = invocation.getArgument(0);
			if (f2 != f) {
				throw new IllegalArgumentException();
			}
			long valueSize = invocation.getArgument(1);
			if (valueSize != 2) {
				throw new CorruptedTagException();
			}
			DataInput in = invocation.getArgument(2);
			if (in.readByte() != 0) {
				throw new CorruptedTagException();
			}
			return 0;
		}).when(p).deserializeValue(any(), anyLong(), any());
		assertThrows(CorruptedTagException.class, () -> {
			try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[] { 0, 1 }))) {
				t.deserializeValue(f, 2, in);
			}
		});
	}
}
