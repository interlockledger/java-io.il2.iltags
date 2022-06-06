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

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import io.il2.iltags.tags.UnexpectedTagException;
import io.il2.iltags.tags.basic.NullTag;

class TagSequenceValidatorImplTest {

	@Test
	void testTagSequenceValidatorImpl() {
		ArrayList<TagValidator> vals = new ArrayList<>();

		TagSequenceValidatorImpl v = new TagSequenceValidatorImpl(vals);
		assertNotSame(v.validators, vals);
		assertEquals(v.validators.size(), vals.size());

		vals.add(new SingleIDTagValidator(true, 0));
		vals.add(new SingleIDTagValidator(true, 1));
		vals.add(new SingleIDTagValidator(true, 2));

		v = new TagSequenceValidatorImpl(vals);
		assertNotSame(v.validators, vals);
		assertEquals(v.validators.size(), vals.size());
		assertSame(v.validators.get(0), vals.get(0));
		assertSame(v.validators.get(1), vals.get(1));
		assertSame(v.validators.get(2), vals.get(2));
	}

	@Test
	void testValidateArray() throws Exception {
		ArrayList<TagValidator> vals = new ArrayList<>();
		TagSequenceValidatorImpl v = new TagSequenceValidatorImpl(vals);

		v.validate();
		assertThrows(UnexpectedTagException.class, () -> {
			v.validate(new NullTag(0));
		});

		vals.add(new SingleIDTagValidator(true, 1));
		vals.add(new MultiIDTagValidator(true, 2, 3, 4));
		vals.add(new SingleIDTagValidator(false, 5));
		vals.add(new MultiIDTagValidator(false, 6, 7));
		TagSequenceValidatorImpl v2 = new TagSequenceValidatorImpl(vals);

		v2.validate(new NullTag(1), new NullTag(2), new NullTag(5), new NullTag(6));
		v2.validate(new NullTag(0), new NullTag(0), new NullTag(5), new NullTag(7));
		v2.validate(null, null, new NullTag(5), new NullTag(7));

		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(0), new NullTag(0), new NullTag(5));
		});
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(1), new NullTag(2), new NullTag(5), new NullTag(6), new NullTag(6));
		});
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(1), new NullTag(2), new NullTag(6), new NullTag(6));
		});
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(1), new NullTag(2), new NullTag(6), new NullTag(6));
		});
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(1), new NullTag(2), new NullTag(0), new NullTag(6));
		});
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(new NullTag(1), new NullTag(2), new NullTag(5), null);
		});
	}
}
