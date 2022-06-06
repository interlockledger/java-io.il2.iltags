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

import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnexpectedTagException;

/**
 * This is the base class for TagValidator implementations.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.06
 */
public abstract class AbstractTagValidator implements TagValidator {

	private final boolean acceptNull;

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param acceptNull If true, null tags will be accepted.
	 */
	protected AbstractTagValidator(boolean acceptNull) {
		this.acceptNull = acceptNull;
	}

	/**
	 * Returns true if null is accepted.
	 * 
	 * @return true if null is accepted or false otherwise.
	 */
	public boolean isAcceptNull() {
		return acceptNull;
	}

	/**
	 * Verifies if the given tag id is valid.
	 * 
	 * @param id The tag id to be validated.
	 * @return true if it is valid or false otherwise.
	 */
	protected abstract boolean acceptTagId(long id);

	public void validate(ILTag tag) throws UnexpectedTagException {
		long tagId = TagID.IL_NULL_TAG_ID;
		if (tag != null) {
			tagId = tag.getTagID();
		}
		if ((tagId == TagID.IL_NULL_TAG_ID) && (acceptNull)) {
			return;
		}
		if (!acceptTagId(tagId)) {
			throw new UnexpectedTagException(String.format("Unexpected tag with id %1$X.", tagId));
		}
	}

}
