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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.TestUtils;
import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;

class ILTagHeaderTest {

	@Test
	void testILTagHeader() {
		ILTagHeader h = new ILTagHeader();
		assertEquals(0, h.tagId);
		assertEquals(0, h.valueSize);
	}

	@Test
	void testILTagHeaderLongLong() {
		ILTagHeader h = new ILTagHeader(1, 2);
		assertEquals(1, h.tagId);
		assertEquals(2, h.valueSize);
	}

	@Test
	void testGetSerializedSize() {
		// Implicit
		for (long id = 0; id < 16; id++) {
			ILTagHeader h = new ILTagHeader(id, -1);
			assertEquals(ILIntEncoder.encodedSize(id), h.getSerializedSize());
		}
		// Explicit
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_IDS) {
				ILTagHeader h = new ILTagHeader(id, size);
				assertEquals(ILIntEncoder.encodedSize(id) + ILIntEncoder.encodedSize(size), h.getSerializedSize());
			}
		}
	}

	@Test
	void testGetSerializedSizeLongLong() {
		// Implicit
		for (long id = 0; id < 16; id++) {
			assertEquals(ILIntEncoder.encodedSize(id), ILTagHeader.getSerializedSize(id, -1));
		}
		// Explicit
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_IDS) {
				assertEquals(ILIntEncoder.encodedSize(id) + ILIntEncoder.encodedSize(size),
						ILTagHeader.getSerializedSize(id, size));
			}
		}
	}

	@Test
	void testSerializeDataOutput() throws Exception {

		// Implicit
		for (long id = 0; id < 16; id++) {
			ILTagHeader h = new ILTagHeader(id, -1);

			ByteArrayOutputStream bExp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bExp)) {
				ILIntEncoder.encode(id, out);
			}
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				h.serialize(out);
			}
			assertArrayEquals(bExp.toByteArray(), bOut.toByteArray());
		}

		// Implicit
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_IDS) {
				ILTagHeader h = new ILTagHeader(id, size);

				ByteArrayOutputStream bExp = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bExp)) {
					ILIntEncoder.encode(id, out);
					ILIntEncoder.encode(size, out);
				}
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bOut)) {
					h.serialize(out);
				}
				assertArrayEquals(bExp.toByteArray(), bOut.toByteArray());
			}
		}
	}

	@Test
	void testSerializeLongLongDataOutput() throws Exception {

		// Implicit
		for (long id = 0; id < 16; id++) {
			ByteArrayOutputStream bExp = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bExp)) {
				ILIntEncoder.encode(id, out);
			}
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILTagHeader.serialize(id, -1, out);
			}
			assertArrayEquals(bExp.toByteArray(), bOut.toByteArray());
		}

		// Implicit
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_IDS) {
				ByteArrayOutputStream bExp = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bExp)) {
					ILIntEncoder.encode(id, out);
					ILIntEncoder.encode(size, out);
				}
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bOut)) {
					ILTagHeader.serialize(id, size, out);
				}
				assertArrayEquals(bExp.toByteArray(), bOut.toByteArray());
			}
		}
	}

	@Test
	void testDeserialize() throws Exception {
		ILTagHeader h = new ILTagHeader();

		// Implicit
		for (long id = 0; id < 16; id++) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			h.deserialize(in);
			assertEquals(id, h.tagId);
			assertEquals(TagID.getImplicitValueSize(id), h.valueSize);
		}

		// General
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_VALUE_SIZES) {
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bOut)) {
					ILIntEncoder.encode(id, out);
					ILIntEncoder.encode(size, out);
				}
				ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
				h.deserialize(in);
				assertEquals(id, h.tagId);
				assertEquals(size, h.valueSize);
			}
		}

		for (long id : TestUtils.SAMPLE_IDS) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
				ILIntEncoder.encode(ILTag.MAX_TAG_VALUE_SIZE, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			h.deserialize(in);
			assertEquals(id, h.tagId);
			assertEquals(ILTag.MAX_TAG_VALUE_SIZE, h.valueSize);
		}

		for (long id : TestUtils.SAMPLE_IDS) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
				ILIntEncoder.encode(ILTag.MAX_TAG_VALUE_SIZE + 1, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			assertThrows(TagTooLargeException.class, () -> {
				h.deserialize(in);
			});
		}
	}

	@Test
	void testDeserializeHeader() throws Exception {

		// Implicit
		for (long id = 0; id < 16; id++) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			ILTagHeader h = ILTagHeader.deserializeHeader(in);
			assertEquals(id, h.tagId);
			assertEquals(TagID.getImplicitValueSize(id), h.valueSize);
		}

		// General
		for (long id : TestUtils.SAMPLE_IDS) {
			for (long size : TestUtils.SAMPLE_VALUE_SIZES) {
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				try (DataOutputStream out = new DataOutputStream(bOut)) {
					ILIntEncoder.encode(id, out);
					ILIntEncoder.encode(size, out);
				}
				ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
				ILTagHeader h = ILTagHeader.deserializeHeader(in);
				assertEquals(id, h.tagId);
				assertEquals(size, h.valueSize);
			}
		}

		for (long id : TestUtils.SAMPLE_IDS) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
				ILIntEncoder.encode(ILTag.MAX_TAG_VALUE_SIZE, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			ILTagHeader h = ILTagHeader.deserializeHeader(in);
			assertEquals(id, h.tagId);
			assertEquals(ILTag.MAX_TAG_VALUE_SIZE, h.valueSize);
		}

		for (long id : TestUtils.SAMPLE_IDS) {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			try (DataOutputStream out = new DataOutputStream(bOut)) {
				ILIntEncoder.encode(id, out);
				ILIntEncoder.encode(ILTag.MAX_TAG_VALUE_SIZE + 1, out);
			}
			ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(bOut.toByteArray()));
			assertThrows(TagTooLargeException.class, () -> {
				ILTagHeader.deserializeHeader(in);
			});
		}
	}

}
