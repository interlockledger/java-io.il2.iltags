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
package io.il2.iltags.utils;

import static io.il2.iltags.TestUtils.genRandomString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import org.junit.jupiter.api.Test;

public class UTF8UtilsTest {

	private static final int[] WIKI_CODEPOINTS = { 0x24, 0xA2, 0x939, 0x20AC, 0x10348 };

	private static final byte[][] WIKI_CODEPOINTS_UTF8 = { { (byte) 0x24 }, { (byte) 0xC2, (byte) 0xA2 },
			{ (byte) 0xE0, (byte) 0xA4, (byte) 0xB9 }, { (byte) 0xE2, (byte) 0x82, (byte) 0xAC },
			{ (byte) 0xF0, (byte) 0x90, (byte) 0x8D, (byte) 0x88 } };

	@Test
	public void testGetEncodedSize() throws Exception {

		for (int size = 0; size < 256; size++) {
			String s = genRandomString(size);
			ByteBuffer b = UTF8Utils.newEncoder().encode(CharBuffer.wrap(s));
			assertEquals(b.limit(), UTF8Utils.getEncodedSize(s));
		}
	}

	@Test
	public void testGetUTF8CharSize() {

		assertEquals(1, UTF8Utils.getUTF8CharSize(0));
		assertEquals(1, UTF8Utils.getUTF8CharSize(0x7F));
		assertEquals(2, UTF8Utils.getUTF8CharSize(0x80));
		assertEquals(2, UTF8Utils.getUTF8CharSize(0x7FF));
		assertEquals(3, UTF8Utils.getUTF8CharSize(0x800));
		assertEquals(3, UTF8Utils.getUTF8CharSize(0xFFFF));
		assertEquals(4, UTF8Utils.getUTF8CharSize(0x10000));
		assertEquals(4, UTF8Utils.getUTF8CharSize(0x10FFFF));
	}

	@Test
	public void testGetUTF8CharSizeInvalidNegative() {
		assertThrows(IllegalArgumentException.class, () -> {
			UTF8Utils.getUTF8CharSize(-1);
		});
	}

	@Test
	public void testGetUTF8CharSizeInvalidTooLarge() {
		assertThrows(IllegalArgumentException.class, () -> {
			UTF8Utils.getUTF8CharSize(0x110000);
		});
	}

	@Test
	public void testGetUTF8EncodedCharSize() {
		assertEquals(1, UTF8Utils.getUTF8EncodedCharSize((byte) 0b00000000));
		assertEquals(1, UTF8Utils.getUTF8EncodedCharSize((byte) 0b01111111));
		assertEquals(2, UTF8Utils.getUTF8EncodedCharSize((byte) 0b10000000));
		assertEquals(2, UTF8Utils.getUTF8EncodedCharSize((byte) 0b11011111));
		assertEquals(3, UTF8Utils.getUTF8EncodedCharSize((byte) 0b11100000));
		assertEquals(3, UTF8Utils.getUTF8EncodedCharSize((byte) 0b11101111));
		assertEquals(4, UTF8Utils.getUTF8EncodedCharSize((byte) 0b11110000));
		assertEquals(4, UTF8Utils.getUTF8EncodedCharSize((byte) 0b11110111));
	}

	@Test
	public void testGetUTF8EncodedCharSizeFail() {
		assertThrows(IllegalArgumentException.class, () -> {
			UTF8Utils.getUTF8EncodedCharSize((byte) 0b11111000);
		});
	}

	@Test
	public void testNewDecoder() {
		CharsetDecoder d = UTF8Utils.newDecoder();

		assertEquals(UTF8Utils.UTF8.name(), d.charset().name());
		assertEquals(CodingErrorAction.REPORT, d.malformedInputAction());
		assertEquals(CodingErrorAction.REPORT, d.unmappableCharacterAction());
	}

	@Test
	public void testNewEncoder() {
		CharsetEncoder e = UTF8Utils.newEncoder();

		assertEquals(UTF8Utils.UTF8.name(), e.charset().name());
		assertEquals(CodingErrorAction.REPORT, e.malformedInputAction());
		assertEquals(CodingErrorAction.REPORT, e.unmappableCharacterAction());
	}

	@Test
	public void testNextCodepoint() throws Exception {
		CharBuffer src;
		CharBuffer control;

		src = CharBuffer.wrap("abcdefg");
		control = src.duplicate();
		while (src.hasRemaining()) {
			assertEquals(control.get() & 0xFF, UTF8Utils.nextCodepoint(src));
		}

		src = CharBuffer.wrap(Character.toChars(0x10348));
		while (src.hasRemaining()) {
			assertEquals(0x10348, UTF8Utils.nextCodepoint(src));
		}

		String s = genRandomString(32, true);
		char[] cs = s.toCharArray();
		src = CharBuffer.wrap(s);
		while (src.hasRemaining()) {
			int cp = Character.codePointAt(cs, src.position(), cs.length);
			assertEquals(cp, UTF8Utils.nextCodepoint(src));
		}
	}

	@Test
	public void testToCodepoint1() {
		byte[] sample = new byte[1];

		assertEquals(0, UTF8Utils.toCodepoint(sample, 1));
		sample[0] = (byte) 1;
		assertEquals(1, UTF8Utils.toCodepoint(sample, 1));
		sample[0] = (byte) 0b01111111;
		assertEquals(0x7F, UTF8Utils.toCodepoint(sample, 1));
	}

	@Test
	public void testToCodepoint2() {
		byte[] sample = new byte[2];

		sample[0] = (byte) 0b11000000;
		sample[1] = (byte) 0b10000000;
		assertEquals(0, UTF8Utils.toCodepoint(sample, 2));

		sample[0] = (byte) 0b11000000;
		sample[1] = (byte) 0b10000001;
		assertEquals(1, UTF8Utils.toCodepoint(sample, 2));

		sample[0] = (byte) 0b11010101;
		sample[1] = (byte) 0b10101010;
		assertEquals(0b10101101010, UTF8Utils.toCodepoint(sample, 2));

		sample[0] = (byte) 0b11011111;
		sample[1] = (byte) 0b10111111;
		assertEquals(0b11111111111, UTF8Utils.toCodepoint(sample, 2));
	}

	@Test
	public void testToCodepoint2PlusFail() {
		byte[] sample = new byte[2];

		sample[0] = (byte) 0b11000000;
		try {
			sample[1] = (byte) 0b00000000;
			UTF8Utils.toCodepoint(sample, 2);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			sample[1] = (byte) 0b01000000;
			UTF8Utils.toCodepoint(sample, 2);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			sample[1] = (byte) 0b11000000;
			UTF8Utils.toCodepoint(sample, 2);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testToCodepoint3() {
		byte[] sample = new byte[3];

		sample[0] = (byte) 0b11100000;
		sample[1] = (byte) 0b10000000;
		sample[2] = (byte) 0b10000000;
		assertEquals(0, UTF8Utils.toCodepoint(sample, 3));

		sample[0] = (byte) 0b11100000;
		sample[1] = (byte) 0b10000000;
		sample[2] = (byte) 0b10000001;
		assertEquals(1, UTF8Utils.toCodepoint(sample, 3));

		sample[0] = (byte) 0b11101010;
		sample[1] = (byte) 0b10010101;
		sample[2] = (byte) 0b10001100;
		assertEquals(0b1010010101001100, UTF8Utils.toCodepoint(sample, 3));

		sample[0] = (byte) 0b11101111;
		sample[1] = (byte) 0b10111111;
		sample[2] = (byte) 0b10111111;
		assertEquals(0b1111111111111111, UTF8Utils.toCodepoint(sample, 3));
	}

	@Test
	public void testToCodepoint4() {
		byte[] sample = new byte[4];

		sample[0] = (byte) 0b11110000;
		sample[1] = (byte) 0b10000000;
		sample[2] = (byte) 0b10000000;
		sample[3] = (byte) 0b10000000;
		assertEquals(0, UTF8Utils.toCodepoint(sample, 4));

		sample[0] = (byte) 0b11110000;
		sample[1] = (byte) 0b10000000;
		sample[2] = (byte) 0b10000000;
		sample[3] = (byte) 0b10000001;
		assertEquals(1, UTF8Utils.toCodepoint(sample, 4));

		sample[0] = (byte) 0b11110101;
		sample[1] = (byte) 0b10101010;
		sample[2] = (byte) 0b10010101;
		sample[3] = (byte) 0b10001100;
		assertEquals(0b101101010010101001100, UTF8Utils.toCodepoint(sample, 4));

		sample[0] = (byte) 0b11110111;
		sample[1] = (byte) 0b10111111;
		sample[2] = (byte) 0b10111111;
		sample[3] = (byte) 0b10111111;
		assertEquals(0b111111111111111111111, UTF8Utils.toCodepoint(sample, 4));
	}

	@Test
	public void testToCodepointWiki() {

		for (int i = 0; i < WIKI_CODEPOINTS.length; i++) {
			assertEquals(WIKI_CODEPOINTS[i],
					UTF8Utils.toCodepoint(WIKI_CODEPOINTS_UTF8[i], WIKI_CODEPOINTS_UTF8[i].length));
		}
	}

	@Test
	public void testToUTF8_1() {
		byte[] tmp = new byte[1];
		byte[] exp = new byte[1];

		assertEquals(tmp.length, UTF8Utils.toUTF8(0, tmp));
		exp[0] = 0;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(1, tmp));
		exp[0] = 1;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x7f, tmp));
		exp[0] = (byte) 0x7F;
		assertArrayEquals(exp, tmp);
	}

	@Test
	public void testToUTF8_2() {
		byte[] tmp = new byte[2];
		byte[] exp = new byte[2];

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x80, tmp));
		exp[0] = (byte) 0b11000010;
		exp[1] = (byte) 0b10000000;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0b10101010101, tmp));
		exp[0] = (byte) 0b11010101;
		exp[1] = (byte) 0b10010101;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x7FF, tmp));
		exp[0] = (byte) 0b11011111;
		exp[1] = (byte) 0b10111111;
		assertArrayEquals(exp, tmp);
	}

	@Test
	public void testToUTF8_3() {
		byte[] tmp = new byte[3];
		byte[] exp = new byte[3];

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x800, tmp));
		exp[0] = (byte) 0b11100000;
		exp[1] = (byte) 0b10100000;
		exp[2] = (byte) 0b10000000;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0b1010010100111100, tmp));
		exp[0] = (byte) 0b11101010;
		exp[1] = (byte) 0b10010100;
		exp[2] = (byte) 0b10111100;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0b1111111111111111, tmp));
		exp[0] = (byte) 0b11101111;
		exp[1] = (byte) 0b10111111;
		exp[2] = (byte) 0b10111111;
		assertArrayEquals(exp, tmp);
	}

	@Test
	public void testToUTF8_4() {
		byte[] tmp = new byte[4];
		byte[] exp = new byte[4];

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x10000, tmp));
		exp[0] = (byte) 0b11110000;
		exp[1] = (byte) 0b10010000;
		exp[2] = (byte) 0b10000000;
		exp[3] = (byte) 0b10000000;
		assertArrayEquals(exp, tmp);

		assertEquals(tmp.length, UTF8Utils.toUTF8(0x10FFFF, tmp));
		exp[0] = (byte) 0b11110100;
		exp[1] = (byte) 0b10001111;
		exp[2] = (byte) 0b10111111;
		exp[3] = (byte) 0b10111111;
		assertArrayEquals(exp, tmp);
	}

	@Test
	public void testToUTF8Wiki() {

		for (int i = 0; i < WIKI_CODEPOINTS.length; i++) {
			byte[] tmp = new byte[WIKI_CODEPOINTS_UTF8[i].length];
			assertEquals(tmp.length, UTF8Utils.toUTF8(WIKI_CODEPOINTS[i], tmp));
			assertArrayEquals(WIKI_CODEPOINTS_UTF8[i], tmp);
		}
	}

	@Test
	public void testUTF8() {
		assertEquals("UTF-8", UTF8Utils.UTF8.name());
	}
}
