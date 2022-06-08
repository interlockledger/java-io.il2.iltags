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
package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import io.il2.iltags.TestUtils;
import io.il2.iltags.tags.ILTag;
import io.il2.iltags.tags.ILTagException;

public class TagTestUtils {

	private static Random random = new Random(System.nanoTime());

	/**
	 * Asserts that 2 tags are equal. It compares the tag id, the classes and
	 * finally the contents (using the tag serialization).
	 * 
	 * @param a Tag a.
	 * @param b Tag b.
	 */
	public static void assertTagEquals(ILTag a, ILTag b) {
		assertEquals(a.getTagID(), b.getTagID(), "Tag IDs does not match.");
		assertInstanceOf(a.getClass(), b, "Tag classes don't match.");

		try {
			byte[] binA = a.toBytes();
			byte[] binB = b.toBytes();
			assertArrayEquals(binA, binB, "Tag serialization does not match.");
		} catch (ILTagException e) {
			fail(e);
		}
	}

	/**
	 * Creates a sample with unique strings.
	 * 
	 * @param n The number of strings to create.
	 * @return An array with the sample strings.
	 */
	public static String[] createSampleStrings(int n) {
		String[] tmp = new String[n];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = Integer.toBinaryString(i);
		}
		return tmp;
	}

	public static ILTag createRandomTag() {
		switch (random.nextInt(5)) {
		case 0:
			Int64Tag t0 = Int64Tag.createStandardSigned();
			t0.setValue(random.nextLong());
			return t0;
		case 1:
			BooleanTag t1 = BooleanTag.createStandard();
			t1.setValue(random.nextBoolean());
			return t1;
		case 2:
			ILIntTag t2 = ILIntTag.createStandard();
			t2.setValue(random.nextLong());
			return t2;
		case 3:
			StringTag t3 = StringTag.createStandard();
			t3.setValue(TestUtils.genRandomString(random.nextInt(16)));
			return t3;
		case 4:
			BytesTag t4 = new BytesTag(Math.abs(random.nextInt()) + 32);
			byte[] v = new byte[random.nextInt(256)];
			random.nextBytes(v);
			t4.setValue(v);
			return t4;
		default:
			return NullTag.createStandard();
		}
	}

	public static ILTag[] createSampleTags(int n) {
		ILTag[] sample = new ILTag[n];

		for (int i = 0; i < n; i++) {
			sample[i] = createRandomTag();
		}
		return sample;
	}

}
