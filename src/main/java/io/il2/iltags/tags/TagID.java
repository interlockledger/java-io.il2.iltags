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

/**
 * This class implements helper functions used to handle tag IDs.
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.27
 */
public final class TagID {

	/**
	 * Standard null tag ID.
	 */
	public static final long IL_NULL_TAG_ID = 0;

	/**
	 * Standard bool tag ID.
	 */
	public static final long IL_BOOL_TAG_ID = 1;

	/**
	 * Standard signed 8-bit integer tag ID.
	 */
	public static final long IL_INT8_TAG_ID = 2;

	/**
	 * Standard unsigned 8-bit integer tag ID.
	 */
	public static final long IL_UINT8_TAG_ID = 3;

	/**
	 * Standard signed 16-bit integer tag ID.
	 */
	public static final long IL_INT16_TAG_ID = 4;

	/**
	 * Standard unsigned 16-bit integer tag ID.
	 */
	public static final long IL_UINT16_TAG_ID = 5;

	/**
	 * Standard signed 32-bit integer tag ID.
	 */
	public static final long IL_INT32_TAG_ID = 6;

	/**
	 * Standard unsigned 32-bit integer tag ID.
	 */
	public static final long IL_UINT32_TAG_ID = 7;

	/**
	 * Standard signed 64-bit integer tag ID.
	 */
	public static final long IL_INT64_TAG_ID = 8;

	/**
	 * Standard unsigned 64-bit integer tag ID.
	 */
	public static final long IL_UINT64_TAG_ID = 9;

	/**
	 * Standard ILInt tag ID.
	 */
	public static final long IL_ILINT_TAG_ID = 10;

	/**
	 * Standard 32-bit floating point tag ID.
	 */
	public static final long IL_BIN32_TAG_ID = 11;

	/**
	 * Standard 64-bit floating point tag ID.
	 */
	public static final long IL_BIN64_TAG_ID = 12;

	/**
	 * Standard 128-bit floating point tag ID.
	 */
	public static final long IL_BIN128_TAG_ID = 13;

	/**
	 * Standard Signed ILInt tag ID.
	 */
	public static final long IL_SIGNED_ILINT_TAG_ID = 14;

	/**
	 * Standard byte array tag ID.
	 */
	public static final long IL_BYTES_TAG_ID = 16;

	/**
	 * Standard string tag ID.
	 */
	public static final long IL_STRING_TAG_ID = 17;

	/**
	 * Standard big integer tag ID.
	 */
	public static final long IL_BINT_TAG_ID = 18;

	/**
	 * Standard big decimal tag ID.
	 */
	public static final long IL_BDEC_TAG_ID = 19;

	/**
	 * Standard ILInt array tag ID.
	 */
	public static final long IL_ILINTARRAY_TAG_ID = 20;

	/**
	 * Standard ILTag array tag ID.
	 */
	public static final long IL_ILTAGARRAY_TAG_ID = 21;

	/**
	 * Standard ILTag sequence tag ID.
	 */
	public static final long IL_ILTAGSEQ_TAG_ID = 22;

	/**
	 * Standard range tag ID.
	 */
	public static final long IL_RANGE_TAG_ID = 23;

	/**
	 * Standard version tag ID.
	 */
	public static final long IL_VERSION_TAG_ID = 24;

	/**
	 * Standard OID tag ID.
	 */
	public static final long IL_OID_TAG_ID = 25;

	/**
	 * Standard dictionary tag ID.
	 */
	public static final long IL_DICTIONARY_TAG_ID = 30;

	/**
	 * Standard string-only dictionary tag ID.
	 */
	public static final long IL_STRING_DICTIONARY_TAG_ID = 31;

	private TagID() {
	}

	public static boolean isImplicit(long tagId) {
		return Long.compareUnsigned(tagId, 16) < 0;
	}

	public static boolean isReserved(long tagId) {
		return Long.compareUnsigned(tagId, 32) < 0;
	}

}
