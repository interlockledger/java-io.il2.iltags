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

import java.io.DataInput;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.basic.BytesTag;
import io.il2.iltags.tags.basic.ILIntTag;
import io.il2.iltags.tags.basic.Int16Tag;
import io.il2.iltags.tags.basic.NullTag;

class AbstractTagFactoryTest {

	private static class AbstractTagFactoryX extends AbstractTagFactory {

		@Override
		public ILTag createTag(long id) throws ILTagException {
			// Simplified version with edge cases.
			if (id == TagID.IL_NULL_TAG_ID) {
				return NullTag.createStandard();
			} else if (id == TagID.IL_INT16_TAG_ID) {
				return Int16Tag.createStandardSigned();
			} else if (id == TagID.IL_ILINT_TAG_ID) {
				return ILIntTag.createStandard();
			} else if (!TagID.isImplicit(id)) {
				return new BytesTag(id);
			} else {
				throw new UnsupportedTagException();
			}
		}
	}

	@Test
	void testCreateTag() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ILTag t = f.createTag(TagID.IL_NULL_TAG_ID);
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.createTag(TagID.IL_INT16_TAG_ID);
		assertInstanceOf(Int16Tag.class, t);
		assertEquals(TagID.IL_INT16_TAG_ID, t.getTagID());

		t = f.createTag(TagID.IL_ILINT_TAG_ID);
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());

		t = f.createTag(16);
		assertInstanceOf(BytesTag.class, t);
		assertEquals(16, t.getTagID());

		assertThrows(UnsupportedTagException.class, () -> {
			f.createTag(15);
		});
	}

	@Test
	void testDeserializeValue() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ByteBufferDataInput in = new ByteBufferDataInput(new byte[] { 0, 1, 2, 3 });
		ILTag t = new Int16Tag(123123);
		f.deserializeValue(t, 2, in);

		in = new ByteBufferDataInput(new byte[] { 0, 1, 2, 3 });
		t = new ILIntTag(123123);
		f.deserializeValue(t, -1, in);

		assertThrows(CorruptedTagException.class, () -> {
			ILTag mt = mock(ILTag.class);
			DataInput in2 = new ByteBufferDataInput(new byte[] { 0, 1, 2, 3 });
			f.deserializeValue(mt, 1, in2);
		});
	}

	@Test
	void testFromBytes() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ILTag t = f.fromBytes(new byte[] { 0 });
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.fromBytes(new byte[] { (byte) 0x4, (byte) 0x12, (byte) 0x34 });
		assertInstanceOf(Int16Tag.class, t);
		assertEquals(TagID.IL_INT16_TAG_ID, t.getTagID());
		assertEquals(0x1234, ((Int16Tag) t).getValue());

		t = f.fromBytes(new byte[] { (byte) 0xA, (byte) 0xf7 });
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF7, ((ILIntTag) t).getValue());

		t = f.fromBytes(new byte[] { (byte) 0xA, (byte) 0xf8, 0x00 });
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF8, ((ILIntTag) t).getValue());

		t = f.fromBytes(new byte[] { (byte) 0x10, 0x02, 0x12, 0x34 });
		assertInstanceOf(BytesTag.class, t);
		assertEquals(TagID.IL_BYTES_TAG_ID, t.getTagID());
		assertArrayEquals(new byte[] { 0x12, 0x34 }, ((BytesTag) t).getValue());

		assertThrows(UnsupportedTagException.class, () -> {
			f.fromBytes(new byte[] { (byte) 0x0F });
		});

		assertThrows(CorruptedTagException.class, () -> {
			f.fromBytes(new byte[] { 0x00, 0x00 });
		});
	}

	@Test
	void testDeserializeDataInput() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ILTag t = f.deserialize(new ByteBufferDataInput(new byte[] { 0 }));
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.deserialize(new ByteBufferDataInput(new byte[] { 0, 0x0f }));
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.deserialize(new ByteBufferDataInput(new byte[] { (byte) 0x4, (byte) 0x12, (byte) 0x34 }));
		assertInstanceOf(Int16Tag.class, t);
		assertEquals(TagID.IL_INT16_TAG_ID, t.getTagID());
		assertEquals(0x1234, ((Int16Tag) t).getValue());

		t = f.deserialize(new ByteBufferDataInput(new byte[] { (byte) 0xA, (byte) 0xf7 }));
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF7, ((ILIntTag) t).getValue());

		t = f.deserialize(new ByteBufferDataInput(new byte[] { (byte) 0xA, (byte) 0xf8, 0x00 }));
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF8, ((ILIntTag) t).getValue());

		t = f.deserialize(new ByteBufferDataInput(new byte[] { (byte) 0x10, 0x02, 0x12, 0x34 }));
		assertInstanceOf(BytesTag.class, t);
		assertEquals(TagID.IL_BYTES_TAG_ID, t.getTagID());
		assertArrayEquals(new byte[] { 0x12, 0x34 }, ((BytesTag) t).getValue());

		assertThrows(UnsupportedTagException.class, () -> {
			f.deserialize(new ByteBufferDataInput(new byte[] { 0x0f }));
		});
	}

	@Test
	void testDeserializeLongDataInput() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ILTag t = f.deserialize(TagID.IL_NULL_TAG_ID, new ByteBufferDataInput(new byte[] { 0 }));
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.deserialize(TagID.IL_NULL_TAG_ID, new ByteBufferDataInput(new byte[] { 0, 0x0f }));
		assertInstanceOf(NullTag.class, t);
		assertEquals(TagID.IL_NULL_TAG_ID, t.getTagID());

		t = f.deserialize(TagID.IL_INT16_TAG_ID,
				new ByteBufferDataInput(new byte[] { (byte) 0x4, (byte) 0x12, (byte) 0x34 }));
		assertInstanceOf(Int16Tag.class, t);
		assertEquals(TagID.IL_INT16_TAG_ID, t.getTagID());
		assertEquals(0x1234, ((Int16Tag) t).getValue());

		t = f.deserialize(TagID.IL_ILINT_TAG_ID, new ByteBufferDataInput(new byte[] { (byte) 0xA, (byte) 0xf7 }));
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF7, ((ILIntTag) t).getValue());

		t = f.deserialize(TagID.IL_ILINT_TAG_ID, new ByteBufferDataInput(new byte[] { (byte) 0xA, (byte) 0xf8, 0x00 }));
		assertInstanceOf(ILIntTag.class, t);
		assertEquals(TagID.IL_ILINT_TAG_ID, t.getTagID());
		assertEquals(0xF8, ((ILIntTag) t).getValue());

		t = f.deserialize(TagID.IL_BYTES_TAG_ID, new ByteBufferDataInput(new byte[] { (byte) 0x10, 0x02, 0x12, 0x34 }));
		assertInstanceOf(BytesTag.class, t);
		assertEquals(TagID.IL_BYTES_TAG_ID, t.getTagID());
		assertArrayEquals(new byte[] { 0x12, 0x34 }, ((BytesTag) t).getValue());

		assertThrows(UnsupportedTagException.class, () -> {
			f.deserialize(0x0f, new ByteBufferDataInput(new byte[] { 0x0f }));
		});

		assertThrows(UnexpectedTagException.class, () -> {
			f.deserialize(TagID.IL_ILINT_TAG_ID, new ByteBufferDataInput(new byte[] { 0x00 }));
		});
	}

	@Test
	void testDeserializeInto() throws Exception {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

		ILTag t = NullTag.createStandard();
		f.deserializeInto(t, new ByteBufferDataInput(new byte[] { 0 }));

		t = Int16Tag.createStandardSigned();
		f.deserializeInto(t, new ByteBufferDataInput(new byte[] { (byte) 0x4, (byte) 0x12, (byte) 0x34 }));
		assertEquals(0x1234, ((Int16Tag) t).getValue());

		t = new Int16Tag(1233);
		assertThrows(UnexpectedTagException.class, () -> {
			ILTag t2 = new Int16Tag(1233);
			f.deserializeInto(t2, new ByteBufferDataInput(new byte[] { (byte) 0x4, (byte) 0x12, (byte) 0x34 }));
		});
	}
}
