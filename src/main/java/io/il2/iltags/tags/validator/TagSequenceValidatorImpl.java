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
import java.util.Arrays;
import java.util.List;

import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.UnexpectedTagException;

/**
 * Default TagSequenceValidator implementation.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.06.05
 */
public class TagSequenceValidatorImpl implements TagSequenceValidator {

	protected ArrayList<TagValidator> validators = new ArrayList<>();

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param validators The list of validators to be applied. This list will be
	 *                   duplicated internally thus changes to it will not affect
	 *                   this instance.
	 */
	public TagSequenceValidatorImpl(ArrayList<TagValidator> validators) {
		this.validators.addAll(validators);
	}

	@Override
	public void validate(List<ILTag> sequence) throws UnexpectedTagException {
		if (sequence.size() != validators.size()) {
			throw new UnexpectedTagException(
					String.format("Expecting %1$d tag(s) but found %2$d tag(s).", validators.size(), sequence.size()));
		}
		for (int i = 0; i < validators.size(); i++) {
			try {
				validators.get(i).validate(sequence.get(i));
			} catch (UnexpectedTagException e) {
				throw new UnexpectedTagException(String.format("Unexpected tag found at %1$d.", i), e);
			}
		}
	}

	@Override
	public void validate(ILTag... sequence) throws UnexpectedTagException {
		validate(Arrays.asList(sequence));
	}
}
