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
package io.il2.iltags.ilint;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ILIntBaseTest {

	public static class Sample {
		private final long value;
		private final byte[] encoded;

		public Sample(long value, byte[] encoded) {
			super();
			this.value = value;
			this.encoded = encoded;
		}

		public long getValue() {
			return value;
		}

		public byte[] getEncoded() {
			return encoded;
		}

		public int getEncodedSize() {
			return encoded.length;
		}
	}

	public static final Sample[] SAMPLES = {
			// Base values
			new Sample(0x0000l, new byte[] { (byte) 0x00 }), new Sample(0x00F7l, new byte[] { (byte) 0xF7 }),
			new Sample(0x00F8l, new byte[] { (byte) 0xF8, (byte) 0x00 }),
			new Sample(0x01F7l, new byte[] { (byte) 0xF8, (byte) 0xFF }),
			new Sample(0x01F8l, new byte[] { (byte) 0xF9, (byte) 0x01, (byte) 0x00 }),
			new Sample(0x0001_00F7l, new byte[] { (byte) 0xF9, (byte) 0xFF, (byte) 0xFF }),
			new Sample(0x0001_00F8l, new byte[] { (byte) 0xFA, (byte) 0x01, (byte) 0x00, (byte) 0x00 }),
			new Sample(0x0100_00F7l, new byte[] { (byte) 0xFA, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }),
			new Sample(0x0100_00F8l, new byte[] { (byte) 0xFB, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00 }),
			new Sample(0x0001_0000_00F7l,
					new byte[] { (byte) 0xFB, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }),
			new Sample(0x0001_0000_00F8l,
					new byte[] { (byte) 0xFC, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }),
			new Sample(0x0100_0000_00F7l,
					new byte[] { (byte) 0xFC, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }),
			new Sample(0x0100_0000_00F8l,
					new byte[] { (byte) 0xFD, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
							(byte) 0x00 }),
			new Sample(0x0001_0000_0000_00F7l,
					new byte[] { (byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
							(byte) 0xFF }),
			new Sample(0x0001_0000_0000_00F8l,
					new byte[] { (byte) 0xFE, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
							(byte) 0x00, (byte) 0x00 }),
			new Sample(0x0100_0000_0000_00F7l,
					new byte[] { (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
							(byte) 0xFF, (byte) 0xFF }),
			new Sample(0x0100_0000_0000_00F8l,
					new byte[] { (byte) 0xFF, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
							(byte) 0x00, (byte) 0x00, (byte) 0x00 }),
			new Sample(0xFFFFFFFFFFFFFFFFl,
					new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
							(byte) 0xFF, (byte) 0xFF, (byte) 0x07 }),
			// Variable.
			new Sample(0x53, new byte[] { (byte) 0x53 }), new Sample(0xF8, new byte[] { (byte) 0xF8, (byte) 0x00 }),
			new Sample(0x021B, new byte[] { (byte) 0xF9, (byte) 0x01, (byte) 0x23, }),
			new Sample(0x01243D, new byte[] { (byte) 0xFA, (byte) 0x01, (byte) 0x23, (byte) 0x45, }),
			new Sample(0x0123465F, new byte[] { (byte) 0xFB, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67 }),
			new Sample(0x0123456881l,
					new byte[] { (byte) 0xFC, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89 }),
			new Sample(0x012345678AA3l,
					new byte[] { (byte) 0xFD, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
							(byte) 0xAB }),
			new Sample(0x123456789ACC5l,
					new byte[] { (byte) 0xFE, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89,
							(byte) 0xAB, (byte) 0xCD }),
			new Sample(0x123456789ABCEE7l, new byte[] { (byte) 0xFF, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
					(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF }) };

	public static final Sample[] SIGNED_SAMPLES = {
			// Base values
			new Sample(0, new byte[] { (byte) 0x00 }), new Sample(1, new byte[] { (byte) 0x02 }),
			new Sample(-1, new byte[] { (byte) 0x01 }),
			new Sample(9_223_372_036_854_775_807l,
					new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
							(byte) 0xFF, (byte) 0xFF, (byte) 0x06 }),
			new Sample(-9_223_372_036_854_775_808l, new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x07 }) };

	@Test
	void testConstants() {
		assertEquals(0xF8, ILIntBase.ILINT_BASE);
		assertEquals(0xF8, ILIntBase.ILINT_BASE64);
		assertEquals(0xFFFF_FFFF_FFFF_FFFFl - 0xF8, ILIntBase.MAX_BODY_VALUE);
	}
}
