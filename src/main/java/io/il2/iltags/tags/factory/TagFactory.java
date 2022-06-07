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
import io.il2.iltags.tags.basic.BigDecTag;
import io.il2.iltags.tags.basic.BigIntTag;
import io.il2.iltags.tags.basic.Binary128Tag;
import io.il2.iltags.tags.basic.BooleanTag;
import io.il2.iltags.tags.basic.BytesTag;
import io.il2.iltags.tags.basic.DictonaryTag;
import io.il2.iltags.tags.basic.DoubleTag;
import io.il2.iltags.tags.basic.FloatTag;
import io.il2.iltags.tags.basic.ILIntArrayTag;
import io.il2.iltags.tags.basic.ILIntTag;
import io.il2.iltags.tags.basic.ILTagArrayTag;
import io.il2.iltags.tags.basic.ILTagSequenceTag;
import io.il2.iltags.tags.basic.Int16Tag;
import io.il2.iltags.tags.basic.Int32Tag;
import io.il2.iltags.tags.basic.Int64Tag;
import io.il2.iltags.tags.basic.Int8Tag;
import io.il2.iltags.tags.basic.NullTag;
import io.il2.iltags.tags.basic.RangeTag;
import io.il2.iltags.tags.basic.SignedILIntTag;
import io.il2.iltags.tags.basic.StringDictonaryTag;
import io.il2.iltags.tags.basic.StringTag;
import io.il2.iltags.tags.basic.VersionTag;

/**
 * This class implements the ILTagFactory interface.
 * 
 * <p>
 * This factory has two modes of operation, the strict mode and the non-strict
 * mode. In the strict mode, all unknown tags will result in an
 * UnsupportedTagException. On the non-strict mode, unknown tags will
 * deserialized as instances of BytesTags.
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

	/**
	 * This is the default tag creator. It will always returns an instance of
	 * BytesTag with the desired tag ID.
	 */
	public static TagCreator DEFAULT_TAG_CREATOR = new TagCreator() {
		@Override
		public ILTag createTag(long id) {
			if (TagID.isImplicit(id)) {
				throw new IllegalArgumentException("This creator cannot handle implicit tags.");
			}
			return new BytesTag(id);
		}
	};

	private final boolean strict;

	protected final HashMap<Long, TagCreator> creators = new HashMap<>();

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
	public void registerTagId(long tagId, TagCreator creator) {
		if (TagID.isReserved(tagId)) {
			throw new IllegalArgumentException("Registration of handlers for reserved keys are not allowed.");
		}
		synchronized (creators) {
			if (creator != null) {
				creators.put(tagId, creator);
			} else {
				creators.remove(tagId);
			}
		}
	}

	/**
	 * Returns the creator for the given tag id. If this factory is running in
	 * strict mode, all non registered tags will throw a UnsupportedTagException. If
	 * it is running in non strict mode, all unknown tags will return the
	 * DEFAULT_TAG_CREATOR.
	 * 
	 * @param tagId The tag id.
	 * @return The instance of the that implements the given tag id.
	 * @throws UnsupportedTagException If the tag is not supported.
	 */
	protected TagCreator getCreatorForId(long tagId) throws UnsupportedTagException {
		TagCreator creator;
		synchronized (creators) {
			creator = creators.get(tagId);
		}
		if (creator != null) {
			return creator;
		} else {
			if (strict) {
				throw new UnsupportedTagException(String.format("Tag with ID %1$X is not supported.", tagId));
			} else {
				return DEFAULT_TAG_CREATOR;
			}
		}
	}

	/**
	 * Creates a new reserved tag.
	 * 
	 * @param tagId The tag id.
	 * @return The instance that implements the given tag.
	 * @throws UnsupportedTagException If the tag id is not supported.
	 */
	public static ILTag createReserved(long tagId) throws UnsupportedTagException {
		if (!TagID.isReserved(tagId)) {
			throw new IllegalArgumentException(String.format("The tag id %1$X is not reserved.", tagId));
		}
		switch ((int) tagId) {
		case (int) TagID.IL_NULL_TAG_ID:
			return NullTag.createStandard();
		case (int) TagID.IL_BOOL_TAG_ID:
			return BooleanTag.createStandard();
		case (int) TagID.IL_INT8_TAG_ID:
			return Int8Tag.createStandardSigned();
		case (int) TagID.IL_UINT8_TAG_ID:
			return Int8Tag.createStandardUnsigned();
		case (int) TagID.IL_INT16_TAG_ID:
			return Int16Tag.createStandardSigned();
		case (int) TagID.IL_UINT16_TAG_ID:
			return Int16Tag.createStandardUnsigned();
		case (int) TagID.IL_INT32_TAG_ID:
			return Int32Tag.createStandardSigned();
		case (int) TagID.IL_UINT32_TAG_ID:
			return Int32Tag.createStandardUnsigned();
		case (int) TagID.IL_INT64_TAG_ID:
			return Int64Tag.createStandardSigned();
		case (int) TagID.IL_UINT64_TAG_ID:
			return Int64Tag.createStandardUnsigned();
		case (int) TagID.IL_ILINT_TAG_ID:
			return ILIntTag.createStandard();
		case (int) TagID.IL_BIN32_TAG_ID:
			return FloatTag.createStandard();
		case (int) TagID.IL_BIN64_TAG_ID:
			return DoubleTag.createStandard();
		case (int) TagID.IL_BIN128_TAG_ID:
			return Binary128Tag.createStandard();
		case (int) TagID.IL_SIGNED_ILINT_TAG_ID:
			return SignedILIntTag.createStandard();
		case (int) TagID.IL_BYTES_TAG_ID:
			return BytesTag.createStandard();
		case (int) TagID.IL_STRING_TAG_ID:
			return StringTag.createStandard();
		case (int) TagID.IL_BINT_TAG_ID:
			return BigIntTag.createStandard();
		case (int) TagID.IL_BDEC_TAG_ID:
			return BigDecTag.createStandard();
		case (int) TagID.IL_ILINTARRAY_TAG_ID:
			return ILIntArrayTag.createStandard();
		case (int) TagID.IL_ILTAGARRAY_TAG_ID:
			return ILTagArrayTag.createStandard();
		case (int) TagID.IL_ILTAGSEQ_TAG_ID:
			return ILTagSequenceTag.createStandard();
		case (int) TagID.IL_RANGE_TAG_ID:
			return RangeTag.createStandard();
		case (int) TagID.IL_VERSION_TAG_ID:
			return VersionTag.createStandard();
		case (int) TagID.IL_OID_TAG_ID:
			return ILIntArrayTag.createStandardOIDTag();
		case (int) TagID.IL_DICTIONARY_TAG_ID:
			return DictonaryTag.createStandard();
		case (int) TagID.IL_STRING_DICTIONARY_TAG_ID:
			return StringDictonaryTag.createStandard();
		default:
			throw new UnsupportedTagException(String.format("Tag with ID %1$X is not supported/defined.", tagId));
		}
	}

	@Override
	public ILTag createTag(long tagId) throws ILTagException {
		if (TagID.isReserved(tagId)) {
			return createReserved(tagId);
		} else {
			return getCreatorForId(tagId).createTag(tagId);
		}
	}
}
