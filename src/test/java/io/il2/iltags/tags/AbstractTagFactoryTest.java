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
import java.util.Random;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.tags.basic.BytesTag;
import io.il2.iltags.tags.basic.ILIntTag;
import io.il2.iltags.tags.basic.NullTag;

class AbstractTagFactoryTest {

	private static class AbstractTagFactoryX extends AbstractTagFactory {

		@Override
		public ILTag createTag(long id) throws ILTagException {
			// Simplified version with edge cases.
			if (id == TagID.IL_NULL_TAG_ID) {
				return NullTag.createStandard();
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
	void testFromBytes() {
		AbstractTagFactoryX f = new AbstractTagFactoryX();

	}

	@Test
	void testDeserializeDataInput() {
		fail("Not yet implemented");
	}

	@Test
	void testDeserializeLongDataInput() {
		fail("Not yet implemented");
	}

	@Test
	void testDeserializeInto() {
		fail("Not yet implemented");
	}

}
