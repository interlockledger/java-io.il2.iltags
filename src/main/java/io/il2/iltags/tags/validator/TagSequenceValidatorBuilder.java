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

import java.util.ArrayList;

import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnexpectedTagException;

/**
 * This class implements a TagSequenceValidator builder. It can be used to
 * create TagSequenceValidator by adding the validators one by one.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.05
 */
public class TagSequenceValidatorBuilder {

	protected static final TagValidator NULL_TAG_VALIDATOR = new TagValidator() {
		@Override
		public void validate(ILTag tag) throws UnexpectedTagException {
			if ((tag != null) && (tag.getTagID() != TagID.IL_NULL_TAG_ID)) {
				throw new UnexpectedTagException("Null tag expected.");
			}
		}
	};

	private ArrayList<TagValidator> validators = new ArrayList<>();

	/**
	 * Creates a new instance of this class.
	 */
	public TagSequenceValidatorBuilder() {
	}

	/**
	 * Adds a new validator that requires a null tag.
	 * 
	 * @return Returns this, allowing the chaining of methods during the build.
	 */
	public TagSequenceValidatorBuilder addNull() {
		return add(NULL_TAG_VALIDATOR);
	}

	/**
	 * Adds a new validator for a single tag id.
	 * 
	 * @param nullable If true, allows null tags alongsize the target tag id.
	 * @param tagID    The expected tag id.
	 * @return Returns this, allowing the chaining of methods during the build.
	 */
	public TagSequenceValidatorBuilder add(boolean nullable, long tagID) {
		return add(new SingleIDTagValidator(nullable, tagID));
	}

	/**
	 * Adds a new validator that accepts one or more tags.
	 * 
	 * @param nullable If true, allows null tags alongsize the target tag id.
	 * @param tagIDs   List of tags to be accepted.
	 * @return Returns this, allowing the chaining of methods during the build.
	 */
	public TagSequenceValidatorBuilder add(boolean nullable, long... tagIDs) {
		return add(new MultiIDTagValidator(nullable, tagIDs));
	}

	/**
	 * Adds a custom validator.
	 * 
	 * @param tagIDs List of tags to be accepted.
	 * @return Returns this, allowing the chaining of methods during the build.
	 */
	public TagSequenceValidatorBuilder add(TagValidator validator) {
		validators.add(validator);
		return this;
	}

	/**
	 * Creates the new tag sequence validator.
	 * 
	 * @return The new tag sequence validator.
	 */
	public TagSequenceValidator build() {
		return new TagSequenceValidatorImpl(validators);
	}
}
