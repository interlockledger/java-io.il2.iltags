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
package io.il2.iltags.tags.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnsupportedTagException;
import io.il2.iltags.tags.basic.BigDecTag;
import io.il2.iltags.tags.basic.BigIntTag;
import io.il2.iltags.tags.basic.Binary128Tag;
import io.il2.iltags.tags.basic.BooleanTag;
import io.il2.iltags.tags.basic.BytesTag;
import io.il2.iltags.tags.basic.DictonaryTag;
import io.il2.iltags.tags.basic.DoubleTag;
import io.il2.iltags.tags.basic.FloatTag;
import io.il2.iltags.tags.basic.ILIntArrayTag;
import io.il2.iltags.tags.basic.ILIntTag;
import io.il2.iltags.tags.basic.ILTagArrayTag;
import io.il2.iltags.tags.basic.ILTagSequenceTag;
import io.il2.iltags.tags.basic.Int16Tag;
import io.il2.iltags.tags.basic.Int32Tag;
import io.il2.iltags.tags.basic.Int64Tag;
import io.il2.iltags.tags.basic.Int8Tag;
import io.il2.iltags.tags.basic.NullTag;
import io.il2.iltags.tags.basic.RangeTag;
import io.il2.iltags.tags.basic.SignedILIntTag;
import io.il2.iltags.tags.basic.StringDictonaryTag;
import io.il2.iltags.tags.basic.StringTag;
import io.il2.iltags.tags.basic.VersionTag;

class TagFactoryTest {

	private static final long[] RESERVED_IDS = { TagID.IL_NULL_TAG_ID, TagID.IL_BOOL_TAG_ID, TagID.IL_INT8_TAG_ID,
			TagID.IL_UINT8_TAG_ID, TagID.IL_INT16_TAG_ID, TagID.IL_UINT16_TAG_ID, TagID.IL_INT32_TAG_ID,
			TagID.IL_UINT32_TAG_ID, TagID.IL_INT64_TAG_ID, TagID.IL_UINT64_TAG_ID, TagID.IL_ILINT_TAG_ID,
			TagID.IL_BIN32_TAG_ID, TagID.IL_BIN64_TAG_ID, TagID.IL_BIN128_TAG_ID, TagID.IL_SIGNED_ILINT_TAG_ID,
			TagID.IL_BYTES_TAG_ID, TagID.IL_STRING_TAG_ID, TagID.IL_BINT_TAG_ID, TagID.IL_BDEC_TAG_ID,
			TagID.IL_ILINTARRAY_TAG_ID, TagID.IL_ILTAGARRAY_TAG_ID, TagID.IL_ILTAGSEQ_TAG_ID, TagID.IL_RANGE_TAG_ID,
			TagID.IL_VERSION_TAG_ID, TagID.IL_OID_TAG_ID, TagID.IL_DICTIONARY_TAG_ID,
			TagID.IL_STRING_DICTIONARY_TAG_ID };

	private static final long[] UNUSED_RESERVED_IDS = { 15, 26, 27, 28, 29 };

	private static final Class<?>[] RESERVED_TAG_CLASSES = { NullTag.class, BooleanTag.class, Int8Tag.class,
			Int8Tag.class, Int16Tag.class, Int16Tag.class, Int32Tag.class, Int32Tag.class, Int64Tag.class,
			Int64Tag.class, ILIntTag.class, FloatTag.class, DoubleTag.class, Binary128Tag.class, SignedILIntTag.class,
			BytesTag.class, StringTag.class, BigIntTag.class, BigDecTag.class, ILIntArrayTag.class, ILTagArrayTag.class,
			ILTagSequenceTag.class, RangeTag.class, VersionTag.class, ILIntArrayTag.class, DictonaryTag.class,
			StringDictonaryTag.class, };

	@Test
	void testDEFAULT_TAG_CREATOR() {

		ILTag t = TagFactory.DEFAULT_TAG_CREATOR.createTag(16);
		assertInstanceOf(BytesTag.class, t);
		assertEquals(16, t.getTagID());

		t = TagFactory.DEFAULT_TAG_CREATOR.createTag(-1);
		assertInstanceOf(BytesTag.class, t);
		assertEquals(-1, t.getTagID());

		assertThrows(IllegalArgumentException.class, () -> {
			TagFactory.DEFAULT_TAG_CREATOR.createTag(0);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			TagFactory.DEFAULT_TAG_CREATOR.createTag(15);
		});
	}

	@Test
	void testTagFactory() {
		TagFactory f = new TagFactory(false);
		assertFalse(f.isStrict());
		assertEquals(0, f.creators.size());

		f = new TagFactory(true);
		assertTrue(f.isStrict());
		assertEquals(0, f.creators.size());
	}

	@Test
	void testRegisterTagId() {
		TagFactory f = new TagFactory(false);

		TagCreator c1 = mock(TagCreator.class);
		TagCreator c2 = mock(TagCreator.class);

		f.registerTagId(1234, c1);
		f.registerTagId(4321, c2);
		assertEquals(2, f.creators.size());
		assertSame(c1, f.creators.get(Long.valueOf(1234)));
		assertSame(c2, f.creators.get(Long.valueOf(4321)));

		f.registerTagId(4321, null);
		assertSame(c1, f.creators.get(Long.valueOf(1234)));
		assertNull(f.creators.get(Long.valueOf(4321)));
	}

	@Test
	void testGetCreatorForId() throws Exception {
		TagFactory f = new TagFactory(false);
		TagCreator c1 = mock(TagCreator.class);
		TagCreator c2 = mock(TagCreator.class);
		TagCreator c3 = mock(TagCreator.class);

		assertSame(TagFactory.DEFAULT_TAG_CREATOR, f.getCreatorForId(1234));

		f.registerTagId(1234, c1);
		f.registerTagId(4321, c2);
		f.registerTagId(32, c3);

		assertSame(c1, f.getCreatorForId(1234));
		assertSame(c2, f.getCreatorForId(4321));
		assertSame(c3, f.getCreatorForId(32));
		assertSame(TagFactory.DEFAULT_TAG_CREATOR, f.getCreatorForId(12346));

		for (long id = 0; id < 32; id++) {
			long tmp = id;
			assertThrows(IllegalArgumentException.class, () -> {
				f.registerTagId(tmp, c2);
			});
		}

		TagFactory f2 = new TagFactory(true);

		assertThrows(UnsupportedTagException.class, () -> {
			f2.getCreatorForId(1234);
		});
		f2.registerTagId(1234, c1);
		f2.registerTagId(4321, c2);
		f2.registerTagId(32, c3);
		assertSame(c1, f2.getCreatorForId(1234));
		assertSame(c2, f2.getCreatorForId(4321));
		assertSame(c3, f2.getCreatorForId(32));

		for (long id = 0; id < 32; id++) {
			long tmp = id;
			assertThrows(IllegalArgumentException.class, () -> {
				f2.registerTagId(tmp, c2);
			});
		}
	}

	@Test
	void testCreateReserved() throws Exception {

		for (int i = 0; i < RESERVED_IDS.length; i++) {
			long id = RESERVED_IDS[i];
			ILTag t = TagFactory.createReserved(id);
			assertEquals(id, t.getTagID());
			assertInstanceOf(RESERVED_TAG_CLASSES[i], t);
		}

		for (long id : UNUSED_RESERVED_IDS) {
			assertThrows(UnsupportedTagException.class, () -> {
				TagFactory.createReserved(id);
			});
		}
	}

	@Test
	void testCreateTag() throws Exception {

		// Reserved tags
		for (boolean strict : new boolean[] { false, true }) {
			TagFactory f = new TagFactory(strict);
			for (int i = 0; i < RESERVED_IDS.length; i++) {
				long id = RESERVED_IDS[i];
				ILTag t = f.createTag(id);
				assertEquals(id, t.getTagID());
				assertInstanceOf(RESERVED_TAG_CLASSES[i], t);
			}

			for (long id : UNUSED_RESERVED_IDS) {
				assertThrows(UnsupportedTagException.class, () -> {
					f.createTag(id);
				});
			}
		}

		TagFactory f = new TagFactory(false);
		ILTag t = f.createTag(32);
		assertInstanceOf(BytesTag.class, t);
		assertEquals(32, t.getTagID());
		f.registerTagId(1234, (tagId) -> {
			return new NullTag(tagId);
		});
		t = f.createTag(32);
		assertInstanceOf(BytesTag.class, t);
		assertEquals(32, t.getTagID());
		t = f.createTag(1234);
		assertInstanceOf(NullTag.class, t);
		assertEquals(1234, t.getTagID());

		TagFactory f2 = new TagFactory(true);
		assertThrows(UnsupportedTagException.class, () -> {
			f2.createTag(32);
		});
		f2.registerTagId(1234, (tagId) -> {
			return new NullTag(tagId);
		});
		t = f2.createTag(1234);
		assertInstanceOf(NullTag.class, t);
		assertEquals(1234, t.getTagID());
		assertThrows(UnsupportedTagException.class, () -> {
			f2.createTag(32);
		});
	}
}
