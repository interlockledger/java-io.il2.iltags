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

import org.junit.jupiter.api.Test;

class TagIDTest {

	@Test
	void testConstants() {
		assertEquals(0, TagID.IL_NULL_TAG_ID);
		assertEquals(1, TagID.IL_BOOL_TAG_ID);
		assertEquals(2, TagID.IL_INT8_TAG_ID);
		assertEquals(3, TagID.IL_UINT8_TAG_ID);
		assertEquals(4, TagID.IL_INT16_TAG_ID);
		assertEquals(5, TagID.IL_UINT16_TAG_ID);
		assertEquals(6, TagID.IL_INT32_TAG_ID);
		assertEquals(7, TagID.IL_UINT32_TAG_ID);
		assertEquals(8, TagID.IL_INT64_TAG_ID);
		assertEquals(9, TagID.IL_UINT64_TAG_ID);
		assertEquals(10, TagID.IL_ILINT_TAG_ID);
		assertEquals(11, TagID.IL_BIN32_TAG_ID);
		assertEquals(12, TagID.IL_BIN64_TAG_ID);
		assertEquals(13, TagID.IL_BIN128_TAG_ID);
		assertEquals(14, TagID.IL_SIGNED_ILINT_TAG_ID);
		assertEquals(16, TagID.IL_BYTES_TAG_ID);
		assertEquals(17, TagID.IL_STRING_TAG_ID);
		assertEquals(18, TagID.IL_BINT_TAG_ID);
		assertEquals(19, TagID.IL_BDEC_TAG_ID);
		assertEquals(20, TagID.IL_ILINTARRAY_TAG_ID);
		assertEquals(21, TagID.IL_ILTAGARRAY_TAG_ID);
		assertEquals(22, TagID.IL_ILTAGSEQ_TAG_ID);
		assertEquals(23, TagID.IL_RANGE_TAG_ID);
		assertEquals(24, TagID.IL_VERSION_TAG_ID);
		assertEquals(25, TagID.IL_OID_TAG_ID);
		assertEquals(30, TagID.IL_DICTIONARY_TAG_ID);
		assertEquals(31, TagID.IL_STRING_DICTIONARY_TAG_ID);
	}

	@Test
	void testIsImplicit() {
		assertTrue(TagID.isImplicit(0));
		assertTrue(TagID.isImplicit(15));
		assertFalse(TagID.isImplicit(16));
		assertFalse(TagID.isImplicit(-1));
	}

	@Test
	void testIsReserved() {
		assertTrue(TagID.isReserved(0));
		assertTrue(TagID.isReserved(31));
		assertFalse(TagID.isReserved(32));
		assertFalse(TagID.isReserved(-1));
	}
}
