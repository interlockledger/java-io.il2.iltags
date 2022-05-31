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

import java.util.HashMap;

import io.il2.iltags.tags.AbstractTagFactory;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;
import io.il2.iltags.tags.UnsupportedTagException;
import io.il2.iltags.tags.basic.NullTag;

/**
 * This class implements the ILTagFactory.
 * 
 * <p>
 * This factory has two modes of operation, the strict mode and the non-strict
 * mode. In the strict mode, all unknown tags will result in an
 * UnsupportedTagException. On the non-strict mode, unknown tags will be loaded
 * as BytesTags.
 * </p>
 * 
 * <p>
 * Instances of this class are thread-safe.
 * </p>
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public class TagFactory extends AbstractTagFactory {

	private final boolean strict;

	private HashMap<Long, TagCreator> creators = new HashMap<>();

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param strict Strict mode.
	 */
	public TagFactory(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Returns the strict mode state.
	 * 
	 * @return True if this factory is working in strict mode or false otherwise.
	 */
	public boolean isStrict() {
		return this.strict;
	}

	/**
	 * Registers a new tag creator.
	 * 
	 * @param tagId   The target tag id. Only non reserved key IDs can be
	 *                registered.
	 * @param creator The tag creator. Set to null to unregister the creator for the
	 *                given id.
	 */
	public synchronized void registerTagId(long tagId, TagCreator creator) {
		if (TagID.isReserved(tagId)) {
			throw new IllegalArgumentException("Registration of handlers for reserved keys are not allowed.");
		}
		if (creator != null) {
			creators.put(tagId, creator);
		} else {
			creators.remove(tagId);
		}
	}

	/**
	 * Returns the creator for the given tag id.
	 * 
	 * @param tagId The tag id.
	 * @return The instance of the that implements the given tag id.
	 */
	protected synchronized TagCreator getCreatorForId(long tagId) {
		return creators.get(tagId);
	}

	/**
	 * Creates a new reserved tag.
	 * 
	 * @param tagId The tag id.
	 * @return The instance that implements the given tag.
	 * @throws UnsupportedTagException If the tag id is not supported.
	 */
	protected ILTag createReserved(long tagId) throws UnsupportedTagException {
		if (!TagID.isReserved(tagId)) {
			throw new IllegalArgumentException(String.format("The tag id %1$X is not reserved.", tagId));
		}
		switch ((int) tagId) {
		case (int) TagID.IL_NULL_TAG_ID:
			return NullTag.createStandard();
		// TODO Register other tags.
		default:
			throw new UnsupportedTagException();
		}
	}

	/**
	 * Creates a registered tag based on its id. This method is responsible to
	 * implement the strict mode.
	 * 
	 * @param tagId The tag id.
	 * @return The instance that implements the given tag id.
	 * @throws UnsupportedTagException If the tag ID is not supported.
	 */
	protected ILTag createRegistered(long tagId) throws UnsupportedTagException {
		TagCreator creator = getCreatorForId(tagId);
		if (creator == null) {
			if (isStrict()) {
				throw new UnsupportedTagException(String.format("Tag with ID %1$X is not supported.", tagId));
			} else {
				// TODO Create a byte array tag here.
				return null;
			}
		}
		return creator.createTag(tagId);
	}

	@Override
	public ILTag createTag(long id) throws ILTagException {
		if (TagID.isReserved(id)) {
			return createReserved(id);
		} else {
			return createRegistered(id);
		}
	}
}
