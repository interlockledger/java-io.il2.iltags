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

/**
 * This package contains the implementation of all reserved tags defined by the
 * <a href=
 * "https://github.com/interlockledger/specification/tree/master/ILTags">ILTags
 * Specification</a>.
 * 
 * <p>
 * The standard tags are mapped as follows:
 * </p>
 * 
 * <table border="1" width="80%">
 * <caption>Map of the classes that implements each standard tag.</caption>
 * <tr>
 * <th>Standard tag</th>
 * <th>Class</th>
 * </tr>
 * <tr>
 * <td>IL_NULL_TAG</td>
 * <td>io.il2.iltags.tags.basic.NullTag</td>
 * </tr>
 * <tr>
 * <td>IL_BOOL_TAG</td>
 * <td>io.il2.iltags.tags.basic.BooleanTag</td>
 * </tr>
 * <tr>
 * <td>IL_INT8_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int8Tag</td>
 * </tr>
 * <tr>
 * <td>IL_UINT8_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int8Tag</td>
 * </tr>
 * <tr>
 * <td>IL_INT16_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int16Tag</td>
 * </tr>
 * <tr>
 * <td>IL_UINT16_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int16Tag</td>
 * </tr>
 * <tr>
 * <td>IL_INT32_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int32Tag</td>
 * </tr>
 * <tr>
 * <td>IL_UINT32_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int32Tag</td>
 * </tr>
 * <tr>
 * <td>IL_INT64_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int64Tag</td>
 * </tr>
 * <tr>
 * <td>IL_UINT64_TAG</td>
 * <td>io.il2.iltags.tags.basic.Int64Tag</td>
 * </tr>
 * <tr>
 * <td>IL_ILINT_TAG</td>
 * <td>io.il2.iltags.tags.basic.ILIntTag</td>
 * </tr>
 * <tr>
 * <td>IL_BIN32_TAG</td>
 * <td>io.il2.iltags.tags.basic.FloatTag</td>
 * </tr>
 * <tr>
 * <td>IL_BIN64_TAG</td>
 * <td>io.il2.iltags.tags.basic.DoubleTag</td>
 * </tr>
 * <tr>
 * <td>IL_BIN128_TAG</td>
 * <td>io.il2.iltags.tags.basic.Binary128Tag</td>
 * </tr>
 * <tr>
 * <td>IL_ILINT_SIGNED_TAG</td>
 * <td>io.il2.iltags.tags.basic.SignedILIntTag</td>
 * </tr>
 * <tr>
 * <td>IL_BYTES_TAG</td>
 * <td>io.il2.iltags.tags.basic.BytesTag</td>
 * </tr>
 * <tr>
 * <td>IL_STRING_TAG</td>
 * <td>io.il2.iltags.tags.basic.StringTag</td>
 * </tr>
 * <tr>
 * <td>IL_BINT_TAG</td>
 * <td>io.il2.iltags.tags.basic.BigIntTag</td>
 * </tr>
 * <tr>
 * <td>IL_BDEC_TAG</td>
 * <td>io.il2.iltags.tags.basic.BigDecTag</td>
 * </tr>
 * <tr>
 * <td>IL_ILINTARRAY_TAG</td>
 * <td>io.il2.iltags.tags.basic.ILIntArrayTag</td>
 * </tr>
 * <tr>
 * <td>IL_ILTAGARRAY_TAG</td>
 * <td>io.il2.iltags.tags.basic.ILTagArrayTag</td>
 * </tr>
 * <tr>
 * <td>IL_ILTAGSEQ_TAG</td>
 * <td>io.il2.iltags.tags.basic.ILTagSequenceTag</td>
 * </tr>
 * <tr>
 * <td>IL_RANGE_TAG</td>
 * <td>io.il2.iltags.tags.basic.RangeTag</td>
 * </tr>
 * <tr>
 * <td>IL_VERSION_TAG</td>
 * <td>io.il2.iltags.tags.basic.VersionTag</td>
 * </tr>
 * <tr>
 * <td>IL_OID_TAG</td>
 * <td>io.il2.iltags.tags.basic.ILIntArrayTag</td>
 * </tr>
 * <tr>
 * <td>IL_DICTIONARY_TAG</td>
 * <td>io.il2.iltags.tags.basic.DictionaryTag</td>
 * </tr>
 * <tr>
 * <td>IL_STRING_DICTIONARY_TAG</td>
 * <td>io.il2.iltags.tags.basic.StringDictionaryTag</td>
 * </tr>
 * </table>
 */
package io.il2.iltags.tags.basic;