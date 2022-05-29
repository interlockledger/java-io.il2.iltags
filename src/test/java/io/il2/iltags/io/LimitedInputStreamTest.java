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
package io.il2.iltags.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import io.il2.iltags.TestUtils;

class LimitedInputStreamTest {

	@Test
	void testLimitedInputStreamInputStreamLong() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456)) {
			assertSame(in, lr.source);
			assertEquals(123456, lr.size);
			assertFalse(lr.isOwner());
		}
		verify(in, times(0)).close();
	}

	@Test
	void testLimitedInputStreamInputStreamLongBoolean() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456, false)) {
			assertSame(in, lr.source);
			assertEquals(123456, lr.size);
			assertFalse(lr.isOwner());
		}

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456, true)) {
			assertSame(in, lr.source);
			assertEquals(123456, lr.size);
			assertTrue(lr.owner);
		}
	}

	@Test
	void testRead() throws Exception {
		byte[] src = new byte[17];
		TestUtils.fillSampleByteArray(src, 0, src.length);

		try (LimitedInputStream lr = new LimitedInputStream(new ByteArrayInputStream(src), 16)) {
			for (int i = 0; i < 16; i++) {
				assertEquals(16 - i, lr.remaining());
				assertEquals(i, lr.read());
				assertEquals(16 - i - 1, lr.remaining());
			}
			assertEquals(-1, lr.read());
			assertEquals(0, lr.remaining());
			assertEquals(-1, lr.read());
			assertEquals(0, lr.remaining());
		}
	}

	@Test
	void testReadByteArrayIntInt() throws Exception {
		byte[] src = new byte[16];
		TestUtils.fillSampleByteArray(src, 0, src.length);

		try (LimitedInputStream lr = new LimitedInputStream(new ByteArrayInputStream(src), 16)) {
			byte[] dst = new byte[16];
			byte[] exp = new byte[16];
			assertEquals(0, lr.read(dst, 0, 0));
			assertEquals(16, lr.remaining());
			assertArrayEquals(exp, dst);

			assertEquals(16, lr.read(dst, 0, dst.length));
			assertEquals(0, lr.remaining());
			assertArrayEquals(src, dst);

			assertEquals(-1, lr.read(dst, 0, dst.length));
		}

		try (LimitedInputStream lr = new LimitedInputStream(new ByteArrayInputStream(src), 16)) {
			byte[] dst = new byte[16];
			byte[] exp = new byte[16];
			assertEquals(8, lr.read(dst, 0, 8));
			assertEquals(8, lr.remaining());
			System.arraycopy(src, 0, exp, 0, 8);
			assertArrayEquals(exp, dst);

			assertEquals(4, lr.read(dst, 8, 4));
			assertEquals(4, lr.remaining());
			System.arraycopy(src, 8, exp, 8, 4);
			assertArrayEquals(exp, dst);

			assertEquals(4, lr.read(dst, 12, 4));
			assertEquals(0, lr.remaining());
			System.arraycopy(src, 12, exp, 12, 4);
			assertArrayEquals(exp, dst);

			assertEquals(-1, lr.read(dst, 0, dst.length));
		}

		try (LimitedInputStream lr = new LimitedInputStream(new ByteArrayInputStream(src), 8)) {
			byte[] dst = new byte[16];
			byte[] exp = new byte[16];
			assertEquals(8, lr.read(dst, 0, dst.length));
			assertEquals(0, lr.remaining());
			System.arraycopy(src, 0, exp, 0, 8);
			assertArrayEquals(exp, dst);

			assertEquals(-1, lr.read(dst, 0, dst.length));
		}
	}

	@Test
	void testSkip() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 16)) {
			doReturn(Long.valueOf(16)).when(in).skip(16);
			assertEquals(16, lr.skip(16));
			verify(in, times(1)).skip(16);
			assertEquals(0, lr.remaining());
			assertEquals(0, lr.skip(16));
		}

		try (LimitedInputStream lr = new LimitedInputStream(in, 16)) {
			doReturn(Long.valueOf(8)).when(in).skip(8);
			assertEquals(8, lr.skip(8));
			verify(in, times(1)).skip(8);
			assertEquals(8, lr.remaining());

			doReturn(Long.valueOf(8)).when(in).skip(8);
			assertEquals(8, lr.skip(8));
			verify(in, times(2)).skip(8);
			assertEquals(0, lr.remaining());

			reset(in);
			assertEquals(0, lr.skip(8));
			verify(in, times(0)).skip(anyLong());
		}

		reset(in);
		try (LimitedInputStream lr = new LimitedInputStream(in, 16)) {
			doReturn(Long.valueOf(8)).when(in).skip(16);
			assertEquals(8, lr.skip(16));
			assertEquals(8, lr.remaining());
			verify(in, times(1)).skip(16);

			doReturn(Long.valueOf(7)).when(in).skip(8);
			assertEquals(7, lr.skip(16));
			assertEquals(1, lr.remaining());
			verify(in, times(1)).skip(8);

			doReturn(Long.valueOf(1)).when(in).skip(1);
			assertEquals(1, lr.skip(16));
			assertEquals(0, lr.remaining());
			verify(in, times(1)).skip(1);

			reset(in);
			assertEquals(0, lr.skip(8));
			verify(in, times(0)).skip(anyLong());
		}
	}

	@Test
	void testAvailable() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 16)) {
			doReturn(32).when(in).available();
			assertEquals(16, lr.available());
			lr.size = 15;
			assertEquals(15, lr.available());

			doReturn(5).when(in).available();
			assertEquals(5, lr.available());
		}
	}

	@Test
	void testClose() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456, false)) {
		}
		verify(in, times(0)).close();

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456, true)) {
		}
		verify(in, times(1)).close();
	}

	@Test
	void testRemaining() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 123456, false)) {
			assertEquals(123456, lr.remaining());

		}
	}

	@Test
	void testEmpty() throws Exception {
		InputStream in = mock(InputStream.class);

		try (LimitedInputStream lr = new LimitedInputStream(in, 1, false)) {
			assertFalse(lr.empty());
			lr.size = 0;
			assertTrue(lr.empty());
		}
	}
}
