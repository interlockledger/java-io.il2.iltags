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
package io.il2.iltags.tags.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import io.il2.iltags.tags.UnexpectedTagException;
import io.il2.iltags.tags.basic.NullTag;

class TagSequenceValidatorBuilderTest {

	@Test
	void testNULL_TAG_VALIDATOR() throws Exception {

		TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR.validate(null);
		TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR.validate(NullTag.createStandard());
		assertThrows(UnexpectedTagException.class, () -> {
			TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR.validate(new NullTag(1));
		});
	}

	@Test
	void testTagSequenceValidatorBuilder() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();
		assertEquals(0, b.validators.size());
	}

	@Test
	void testAddNull() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();

		b.addNull();
		assertEquals(1, b.validators.size());
		assertSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(0));
		b.add(false, 1);
		b.addNull();
		assertEquals(3, b.validators.size());
		assertSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(0));
		assertNotSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(1));
		assertSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(2));
	}

	@Test
	void testAddBooleanLong() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();

		b.add(true, 10);
		assertEquals(1, b.validators.size());
		assertInstanceOf(SingleIDTagValidator.class, b.validators.get(0));
		SingleIDTagValidator v = (SingleIDTagValidator) b.validators.get(0);
		assertTrue(v.isAcceptNull());
		assertEquals(10, v.id);

		b.addNull();
		b.add(false, 11);
		assertEquals(3, b.validators.size());
		assertInstanceOf(SingleIDTagValidator.class, b.validators.get(0));
		v = (SingleIDTagValidator) b.validators.get(0);
		assertTrue(v.isAcceptNull());
		assertEquals(10, v.id);

		assertSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(1));

		assertInstanceOf(SingleIDTagValidator.class, b.validators.get(2));
		v = (SingleIDTagValidator) b.validators.get(2);
		assertFalse(v.isAcceptNull());
		assertEquals(11, v.id);
	}

	@Test
	void testAddBooleanLongArray() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();

		b.add(true, 10, 11);
		assertEquals(1, b.validators.size());
		assertInstanceOf(MultiIDTagValidator.class, b.validators.get(0));
		MultiIDTagValidator v = (MultiIDTagValidator) b.validators.get(0);
		assertTrue(v.isAcceptNull());
		assertArrayEquals(new long[] { 10, 11 }, v.ids);

		b.addNull();
		b.add(false, 12, 13, 14);
		assertEquals(3, b.validators.size());
		assertInstanceOf(MultiIDTagValidator.class, b.validators.get(0));
		v = (MultiIDTagValidator) b.validators.get(0);
		assertTrue(v.isAcceptNull());
		assertArrayEquals(new long[] { 10, 11 }, v.ids);

		assertSame(TagSequenceValidatorBuilder.NULL_TAG_VALIDATOR, b.validators.get(1));

		assertInstanceOf(MultiIDTagValidator.class, b.validators.get(2));
		v = (MultiIDTagValidator) b.validators.get(2);
		assertFalse(v.isAcceptNull());
		assertArrayEquals(new long[] { 12, 13, 14 }, v.ids);
	}

	@Test
	void testAddTagValidator() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();

		TagValidator v1 = mock(TagValidator.class);
		TagValidator v2 = mock(TagValidator.class);
		TagValidator v3 = mock(TagValidator.class);

		b.add(v1);
		assertEquals(1, b.validators.size());
		assertSame(v1, b.validators.get(0));

		b.add(v2);
		assertEquals(2, b.validators.size());
		assertSame(v1, b.validators.get(0));
		assertSame(v2, b.validators.get(1));

		b.add(v3);
		assertEquals(3, b.validators.size());
		assertSame(v1, b.validators.get(0));
		assertSame(v2, b.validators.get(1));
		assertSame(v3, b.validators.get(2));

		b.add(v1);
		assertEquals(4, b.validators.size());
		assertSame(v1, b.validators.get(0));
		assertSame(v2, b.validators.get(1));
		assertSame(v3, b.validators.get(2));
		assertSame(v1, b.validators.get(3));
	}

	@Test
	void testBuild() {
		TagSequenceValidatorBuilder b = new TagSequenceValidatorBuilder();

		TagValidator v1 = mock(TagValidator.class);
		TagValidator v2 = mock(TagValidator.class);
		TagValidator v3 = mock(TagValidator.class);
		b.add(v1);
		b.add(v2);

		TagSequenceValidator va = b.build();
		assertInstanceOf(TagSequenceValidatorImpl.class, va);
		TagSequenceValidatorImpl va1 = (TagSequenceValidatorImpl) va;
		assertNotSame(b.validators, va1.validators);
		assertEquals(2, va1.validators.size());
		assertSame(v1, va1.validators.get(0));
		assertSame(v2, va1.validators.get(1));

		// Ensure they are independent
		b.add(v3);
		TagSequenceValidator vb = b.build();
		assertInstanceOf(TagSequenceValidatorImpl.class, vb);
		TagSequenceValidatorImpl vb1 = (TagSequenceValidatorImpl) vb;

		assertNotSame(b.validators, vb1.validators);
		assertEquals(3, vb1.validators.size());
		assertSame(v1, vb1.validators.get(0));
		assertSame(v2, vb1.validators.get(1));
		assertSame(v3, vb1.validators.get(2));

		assertNotSame(b.validators, va1.validators);
		assertEquals(2, va1.validators.size());
		assertSame(v1, va1.validators.get(0));
		assertSame(v2, va1.validators.get(1));
	}
}
