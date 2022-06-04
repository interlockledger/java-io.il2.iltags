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

import java.io.EOFException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class LimitedDataInputTest {

	private static final byte[] SAMPLE = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
			(byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D,
			(byte) 0x0E, (byte) 0x0F };

	@Test
	void testLimitedDataInput() throws Exception {
		ByteBufferDataInput src = new ByteBufferDataInput(SAMPLE);
		LimitedDataInput in = new LimitedDataInput(src, 10);
		assertSame(src, in.source);
		assertEquals(10, in.remaining());
	}

	@Test
	void testUpdateRead() throws Exception {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 10);

		assertEquals(10, in.remaining());
		in.updateRead(0);
		assertEquals(10, in.remaining());
		in.updateRead(4);
		assertEquals(6, in.remaining());
		in.updateRead(4);
		assertEquals(2, in.remaining());
		assertThrows(EOFException.class, () -> {
			in.updateRead(3);
		});
		in.updateRead(2);
		assertEquals(0, in.remaining());
		in.updateRead(0);
		assertThrows(EOFException.class, () -> {
			in.updateRead(1);
		});
	}

	@Test
	void testRemaining() {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 2);

		assertTrue(in.hasRemaining());
		assertEquals(2, in.remaining());

		in.size = 1;
		assertTrue(in.hasRemaining());
		assertEquals(1, in.remaining());

		in.size = 0;
		assertFalse(in.hasRemaining());
		assertEquals(0, in.remaining());
	}

	@Test
	void testReadFullyByteArray() throws Exception {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 11);

		byte[] r = new byte[5];
		byte[] exp = new byte[5];
		in.readFully(r);
		System.arraycopy(SAMPLE, 0, exp, 0, 5);
		assertArrayEquals(exp, r);

		in.readFully(r);
		System.arraycopy(SAMPLE, 5, exp, 0, 5);
		assertArrayEquals(exp, r);

		assertThrows(EOFException.class, () -> {
			in.readFully(r);
		});
	}

	@Test
	void testReadFullyByteArrayIntInt() throws Exception {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 11);

		byte[] r = new byte[10];
		byte[] exp = new byte[10];

		in.readFully(r, 0, 5);
		System.arraycopy(SAMPLE, 0, exp, 0, 5);
		assertArrayEquals(exp, r);

		in.readFully(r, 5, 5);
		System.arraycopy(SAMPLE, 5, exp, 5, 5);
		assertArrayEquals(exp, r);

		assertThrows(EOFException.class, () -> {
			in.readFully(r, 5, 5);
		});
		in.size = 0;
		assertThrows(EOFException.class, () -> {
			in.readFully(r, 5, 5);
		});
		in.readFully(r, 5, 0);
		assertArrayEquals(exp, r);
	}

	@Test
	void testSkipBytes() throws Exception {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 11);
		assertEquals(5, in.skipBytes(5));
		assertEquals(5, in.readByte());
		assertEquals(5, in.skipBytes(10));
		assertEquals(0, in.skipBytes(10));
	}

	@Test
	void testReadBoolean() throws Exception {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 2);

		assertFalse(in.readBoolean());
		assertTrue(in.readBoolean());
		assertThrows(EOFException.class, () -> {
			in.readBoolean();
		});
	}

	@Test
	void testReadByte() throws Exception {
		LimitedDataInput in = new LimitedDataInput(
				new ByteBufferDataInput(new byte[] { (byte) 0x01, (byte) 0xFF, (byte) 0x00 }), 2);

		assertEquals(1, in.readByte());
		assertEquals(-1, in.readByte());
		assertThrows(EOFException.class, () -> {
			in.readByte();
		});
	}

	@Test
	void testReadUnsignedByte() throws IOException {
		LimitedDataInput in = new LimitedDataInput(
				new ByteBufferDataInput(new byte[] { (byte) 0x01, (byte) 0xFF, (byte) 0x00 }), 2);

		assertEquals(0x01, in.readUnsignedByte());
		assertEquals(0xFF, in.readUnsignedByte());
		assertThrows(EOFException.class, () -> {
			in.readUnsignedByte();
		});
	}

	@Test
	void testReadShort() throws IOException {
		LimitedDataInput in = new LimitedDataInput(
				new ByteBufferDataInput(new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x00 }),
				4);

		assertEquals(1, in.readShort());
		assertEquals(-256, in.readShort());
		assertThrows(EOFException.class, () -> {
			in.readShort();
		});
	}

	@Test
	void testReadUnsignedShort() throws IOException {
		LimitedDataInput in = new LimitedDataInput(
				new ByteBufferDataInput(new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x00 }),
				4);

		assertEquals(1, in.readUnsignedShort());
		assertEquals(0xFF00, in.readUnsignedShort());
		assertThrows(EOFException.class, () -> {
			in.readUnsignedShort();
		});
	}

	@Test
	void testReadChar() throws IOException {
		LimitedDataInput in = new LimitedDataInput(
				new ByteBufferDataInput(new byte[] { (byte) 0x00, (byte) 0x01, (byte) 0xFF, (byte) 0x00, (byte) 0x00 }),
				4);

		assertEquals((char) 0x0001, in.readChar());
		assertEquals((char) 0xFF00, in.readChar());
		assertThrows(EOFException.class, () -> {
			in.readChar();
		});
	}

	@Test
	void testReadInt() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 8);

		assertEquals(0x00010203, in.readInt());
		assertEquals(0x04050607, in.readInt());
		assertThrows(EOFException.class, () -> {
			in.readInt();
		});
	}

	@Test
	void testReadLong() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 16);

		assertEquals(0x00010203_04050607l, in.readLong());
		assertEquals(0x08090a0b_0c0d0e0fl, in.readLong());
		assertThrows(EOFException.class, () -> {
			in.readLong();
		});
	}

	@Test
	void testReadFloat() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 8);

		assertEquals(9.25571648671185E-41, in.readFloat());
		assertEquals(1.5636842486455404E-36, in.readFloat());
		assertThrows(EOFException.class, () -> {
			in.readFloat();
		});
	}

	@Test
	void testReadDouble() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 16);

		assertEquals(1.40159977307889E-309, in.readDouble());
		assertEquals(5.924543410270741E-270, in.readDouble());
		assertThrows(EOFException.class, () -> {
			in.readDouble();
		});
	}

	@Test
	void testReadLine() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 16);
		assertThrows(UnsupportedOperationException.class, () -> {
			in.readLine();
		});
	}

	@Test
	void testReadUTF() throws IOException {
		LimitedDataInput in = new LimitedDataInput(new ByteBufferDataInput(SAMPLE), 16);
		assertThrows(UnsupportedOperationException.class, () -> {
			in.readUTF();
		});
	}
}
