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
package io.il2.iltags;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Base64;
import java.util.Random;

public abstract class TestUtils {

	/**
	 * Sample Tag IDs with all 9 sizes. It does not include implicit IDs.
	 */
	public static final long[] SAMPLE_IDS = { 0x10, 0xFEl, 0xFEDCl, 0xFEDCBAl, 0xFEDCBA98l, 0xFEDCBA9876l,
			0xFEDCBA987654l, 0xFEDCBA98765432l, 0xFEDCBA9876543210l };

	/**
	 * Sample value sizes up to the maximum accepted value size.
	 */
	public static final long[] SAMPLE_VALUE_SIZES = { 0x20L, 0x2000L, 0x200000L, 0x20000000L };

	public static final Charset UTF8 = Charset.forName("utf8");

	/**
	 * Sample extracted from "A Mulher de Preto" by Machado de Assis. This one was
	 * used because it contains multiple accents.
	 */
	public static final String SAMPLE = "Estêvão Soares teve de ir à casa de um ministro de Estado para saber"
			+ " deuns papéis relativos a um parente da província, e aí encontrou o"
			+ " deputado Meneses, que acabava de ter uma conferência política.\n";

	/**
	 * The value of SAMPLE encoded in UTF-8.
	 */
	public static final byte[] SAMPLE_BIN = Base64.getDecoder()
			.decode("RXN0w6p2w6NvIFNvYXJlcyB0ZXZlIGRlIGlyIMOgIGNhc2EgZGUgdW0gbWluaXN0cm8gZGUgRXN0"
					+ "YWRvIHBhcmEgc2FiZXIgZGV1bnMgcGFww6lpcyByZWxhdGl2b3MgYSB1bSBwYXJlbnRlIGRhIHBy"
					+ "b3bDrW5jaWEsIGUgYcOtIGVuY29udHJvdSBvIGRlcHV0YWRvIE1lbmVzZXMsIHF1ZSBhY2FiYXZh"
					+ "IGRlIHRlciB1bWEgY29uZmVyw6puY2lhIHBvbMOtdGljYS4K");

	public static byte[] createSampleByteArray(int size) {
		byte[] v = new byte[size];
		fillSampleByteArray(v, 0, v.length);
		return v;
	}

	public static void fillSampleByteArray(byte[] v, int offs, int size) {

		for (int i = 0; i < size; i++) {
			v[i + offs] = (byte) (i & 0xFF);
		}
	}

	/**
	 * Generates a random string using only single character codepoints.
	 * 
	 * @param size The number of characters.
	 * @return The generated string.
	 */
	public static String genRandomString(int size) {
		return genRandomString(size, false);
	}

	/**
	 * Generates a random string.
	 * 
	 * <p>
	 * If all is set to true, all possible codepoints from U+0 to U+10FFFF are used
	 * with the exception of the reserved range U+D800 to U+DFFF. If all is set to
	 * false, only characters from U+0 to U+FFFF will be used, excluding the
	 * reserved range.
	 * </p>
	 * 
	 * <p>
	 * The result will contain exactly the specified number of characters but may
	 * contain less codepoints due to the use of surrogate pairs.
	 * </p>
	 * 
	 * @param size The number of characters.
	 * @param all  If true, include single and double character codepoints otherwise
	 *             uses only single character codepoints.
	 * @return The generated string.
	 */
	public static String genRandomString(int size, boolean all) {
		Random random = new Random();
		char[] tmp = new char[2];
		int range = all ? 0x10FFFF : 0xFFFF;

		StringBuffer sb = new StringBuffer(size);
		while (size > 0) {
			int cp = random.nextInt(range) + 1;
			if ((cp < 0xD800) || (cp > 0xDFFF)) {
				int len = Character.toChars(cp, tmp, 0);
				if (len == 1) {
					size--;
					sb.append(tmp[0]);
				} else {
					if (size > 1) {
						size -= 2;
						sb.append(tmp, 0, 2);
					}
				}
			}
		}
		return sb.toString();
	}

	public static byte[] stringToUTF8(String s) throws Exception {

		ByteBuffer b = UTF8.newEncoder().onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT).encode(CharBuffer.wrap(s));
		byte[] ret = new byte[b.limit()];
		b.get(ret);
		return ret;
	}
}
