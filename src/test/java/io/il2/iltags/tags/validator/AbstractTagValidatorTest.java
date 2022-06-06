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

import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.UnexpectedTagException;

class AbstractTagValidatorTest {

	private static class AbstractTagValidatorX extends AbstractTagValidator {

		protected AbstractTagValidatorX(boolean acceptNull) {
			super(acceptNull);
		}

		@Override
		protected boolean acceptTagId(long id) {
			return id == 1;
		}

	}

	@Test
	void testAbstractTagValidator() {
		AbstractTagValidatorX v = new AbstractTagValidatorX(false);
		assertFalse(v.isAcceptNull());

		v = new AbstractTagValidatorX(true);
		assertTrue(v.isAcceptNull());
	}

	@Test
	void testAcceptTagId() {
		AbstractTagValidatorX v = new AbstractTagValidatorX(false);

		assertFalse(v.acceptTagId(0));
		assertTrue(v.acceptTagId(1));
		assertFalse(v.acceptTagId(2));

		v = new AbstractTagValidatorX(false);
		assertFalse(v.acceptTagId(0));
		assertTrue(v.acceptTagId(1));
		assertFalse(v.acceptTagId(2));
	}

	@Test
	void testValidate() throws Exception {
		AbstractTagValidatorX v = new AbstractTagValidatorX(false);

		ILTag t = mock(ILTag.class);
		when(t.getTagID()).thenReturn(Long.valueOf(1));

		v.validate(t);

		assertThrows(UnexpectedTagException.class, () -> {
			v.validate(null);
		});

		when(t.getTagID()).thenReturn(Long.valueOf(2));
		assertThrows(UnexpectedTagException.class, () -> {
			v.validate(t);
		});

		AbstractTagValidatorX v2 = new AbstractTagValidatorX(true);

		when(t.getTagID()).thenReturn(Long.valueOf(0));
		v2.validate(t);

		v2.validate(null);

		when(t.getTagID()).thenReturn(Long.valueOf(1));
		v2.validate(t);

		when(t.getTagID()).thenReturn(Long.valueOf(2));
		assertThrows(UnexpectedTagException.class, () -> {
			v2.validate(t);
		});
	}
}
