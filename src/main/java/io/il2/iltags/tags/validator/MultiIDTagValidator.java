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

import java.util.Arrays;

/**
 * This is a tag validator that accepts multiple tag ids.
 * 
 * <p>
 * This implementation has been designed to handle a small set of IDs as it is
 * the expected use case for most applications.
 * </p>
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.06
 */
public class MultiIDTagValidator extends AbstractTagValidator {

	protected final long[] ids;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param acceptNull If true, it will accept null tags.
	 * @param ids        The IDs that will be accepted.
	 */
	public MultiIDTagValidator(boolean acceptNull, long... ids) {
		super(acceptNull);
		if (ids.length == 0) {
			throw new IllegalArgumentException("At least one tag id must be set.");
		}
		this.ids = ids;
		Arrays.sort(this.ids);
	}

	@Override
	protected boolean acceptTagId(long id) {
		return Arrays.binarySearch(ids, id) >= 0;
	}
}
