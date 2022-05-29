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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.jupiter.api.Test;

import io.il2.iltags.TestUtils;

class ByteBufferDataInputTest {

	private static final byte[] SAMPLE_BIN = { (byte) 0xc9, (byte) 0x0d, (byte) 0x65, (byte) 0xe1, (byte) 0x73,
			(byte) 0x4c, (byte) 0x81, (byte) 0x82, (byte) 0x7c, (byte) 0x3e, (byte) 0x0c, (byte) 0xab, (byte) 0x18,
			(byte) 0x72, (byte) 0x74, (byte) 0x0e };

	@Test
	void testByteBufferDataInput() {
		ByteBuffer b = ByteBuffer.allocate(1);

		ByteBufferDataInput in = new ByteBufferDataInput(b);
		assertSame(b, in.source);
		assertEquals(ByteOrder.BIG_ENDIAN, b.order());

		b.order(ByteOrder.LITTLE_ENDIAN);
		in = new ByteBufferDataInput(b);
		assertSame(b, in.source);
		assertEquals(ByteOrder.BIG_ENDIAN, b.order());
	}

	@Test
	void testReadFullyByteArray() throws IOException {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_BIN));
		byte[] tmp = new byte[8];
		byte[] exp = new byte[8];

		in.readFully(tmp);
		System.arraycopy(SAMPLE_BIN, 0, exp, 0, exp.length);
		assertArrayEquals(exp, tmp);

		in.readFully(tmp);
		System.arraycopy(SAMPLE_BIN, 8, exp, 0, exp.length);
		assertArrayEquals(exp, tmp);

		in.source.position(9);
		assertThrows(EOFException.class, () -> {
			in.readFully(tmp);
		});
	}

	@Test
	void testReadFullyByteArrayIntInt() throws Exception {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_BIN));
		byte[] tmp = new byte[8];
		byte[] exp = new byte[8];

		in.readFully(tmp, 1, 6);
		System.arraycopy(SAMPLE_BIN, 0, exp, 1, 6);
		assertArrayEquals(exp, tmp);

		in.readFully(tmp, 1, 6);
		System.arraycopy(SAMPLE_BIN, 6, exp, 1, 6);
		assertArrayEquals(exp, tmp);
		assertThrows(EOFException.class, () -> {
			in.readFully(tmp, 1, 6);
		});
	}

	@Test
	void testSkipBytes() throws Exception {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_BIN));

		assertEquals(0, in.skipBytes(0));
		assertEquals(0, in.source.position());

		assertEquals(1, in.skipBytes(1));
		assertEquals(1, in.source.position());

		assertEquals(15, in.skipBytes(16));
		assertEquals(16, in.source.position());

		assertEquals(0, in.skipBytes(16));
		assertEquals(16, in.source.position());
	}

	@Test
	void testReadBoolean() throws Exception {
		byte[] SAMPLE = new byte[256];
		TestUtils.fillSampleByteArray(SAMPLE, 0, SAMPLE.length);

		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE));
		assertFalse(in.readBoolean());
		for (int i = 0; i < 255; i++) {
			assertTrue(in.readBoolean());
		}
		assertThrows(EOFException.class, () -> {
			in.readBoolean();
		});
	}

	@Test
	void testReadByte() throws Exception {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_BIN));

		for (byte b : SAMPLE_BIN) {
			assertEquals(b, in.readByte());
		}
		assertThrows(EOFException.class, () -> {
			in.readByte();
		});
	}

	@Test
	void testReadUnsignedByte() throws Exception {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_BIN));

		for (byte b : SAMPLE_BIN) {
			assertEquals(b & 0xFF, in.readUnsignedByte());
		}
		assertThrows(EOFException.class, () -> {
			in.readUnsignedByte();
		});
	}

	@Test
	void testReadShort() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			short e = exp.getShort();
			assertEquals(e, in.readShort());
		}

		// EOF
		for (int i = 0; i < 1; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readShort();
			});
		}
	}

	@Test
	void testReadUnsignedShort() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			short e = exp.getShort();
			assertEquals(e & 0xFFFF, in.readUnsignedShort());
		}

		// EOF
		for (int i = 0; i < 1; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readUnsignedShort();
			});
		}
	}

	@Test
	void testReadChar() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			char e = exp.getChar();
			assertEquals(e, in.readChar());
		}

		// EOF
		for (int i = 0; i < 1; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readChar();
			});
		}
	}

	@Test
	void testReadInt() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			int e = exp.getInt();
			assertEquals(e, in.readInt());
		}

		// EOF
		for (int i = 0; i < 3; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readInt();
			});
		}
	}

	@Test
	void testReadLong() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			long e = exp.getLong();
			assertEquals(e, in.readLong());
		}

		// EOF
		for (int i = 0; i < 7; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readLong();
			});
		}
	}

	@Test
	void testReadFloat() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			float e = exp.getFloat();
			assertEquals(e, in.readFloat());
		}

		// EOF
		for (int i = 0; i < 3; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readFloat();
			});
		}
	}

	@Test
	void testReadDouble() throws Exception {
		ByteBuffer exp = ByteBuffer.wrap(SAMPLE_BIN);
		ByteBufferDataInput in = new ByteBufferDataInput(exp.duplicate());

		while (exp.hasRemaining()) {
			double e = exp.getDouble();
			assertEquals(e, in.readDouble());
		}

		// EOF
		for (int i = 0; i < 7; i++) {
			in.source.position(0);
			in.source.limit(i);
			assertThrows(EOFException.class, () -> {
				in.readDouble();
			});
		}
	}

	private static final byte[] SAMPLE_ISO_8859_1 = { (byte) 0x0a, (byte) 0x0d, (byte) 0x0d, (byte) 0x0a, (byte) 0x41,
			(byte) 0x20, (byte) 0x70, (byte) 0x72, (byte) 0xe1, (byte) 0x74, (byte) 0x69, (byte) 0x63, (byte) 0x61,
			(byte) 0x20, (byte) 0x66, (byte) 0x61, (byte) 0x7a, (byte) 0x20, (byte) 0x61, (byte) 0x20, (byte) 0x70,
			(byte) 0x65, (byte) 0x72, (byte) 0x66, (byte) 0x65, (byte) 0x69, (byte) 0xe7, (byte) 0xe3, (byte) 0x6f,
			(byte) 0x2e, (byte) 0x0a, (byte) 0x41, (byte) 0x20, (byte) 0x75, (byte) 0x6e, (byte) 0x69, (byte) 0xe3,
			(byte) 0x6f, (byte) 0x20, (byte) 0x66, (byte) 0x61, (byte) 0x7a, (byte) 0x20, (byte) 0x61, (byte) 0x20,
			(byte) 0x66, (byte) 0x6f, (byte) 0x72, (byte) 0xe7, (byte) 0x61, (byte) 0x2e, (byte) 0x0d, (byte) 0x4f,
			(byte) 0x6e, (byte) 0x64, (byte) 0x65, (byte) 0x20, (byte) 0x68, (byte) 0xe1, (byte) 0x20, (byte) 0x66,
			(byte) 0x75, (byte) 0x6d, (byte) 0x61, (byte) 0xe7, (byte) 0x61, (byte) 0x2c, (byte) 0x20, (byte) 0x68,
			(byte) 0xe1, (byte) 0x20, (byte) 0x66, (byte) 0x6f, (byte) 0x67, (byte) 0x6f, (byte) 0x2e, (byte) 0x0D,
			(byte) 0x0a, (byte) 0x4f, (byte) 0x20, (byte) 0x71, (byte) 0x75, (byte) 0x65, (byte) 0x20, (byte) 0x76,
			(byte) 0x65, (byte) 0x6d, (byte) 0x20, (byte) 0x66, (byte) 0xe1, (byte) 0x63, (byte) 0x69, (byte) 0x6c,
			(byte) 0x2c, (byte) 0x20, (byte) 0x76, (byte) 0x61, (byte) 0x69, (byte) 0x20, (byte) 0x66, (byte) 0xe1,
			(byte) 0x63, (byte) 0x69, (byte) 0x6c, (byte) 0x2e };

	@Test
	void testReadLine() throws Exception {
		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_ISO_8859_1));

		assertEquals("", in.readLine());
		assertEquals("", in.readLine());
		assertEquals("", in.readLine());
		assertEquals("A prática faz a perfeição.", in.readLine());
		assertEquals("A união faz a força.", in.readLine());
		assertEquals("Onde há fumaça, há fogo.", in.readLine());
		assertEquals("O que vem fácil, vai fácil.", in.readLine());
		assertNull(in.readLine());
	}

	@Test
	void testExtractUTF8Body() throws Exception {
		ByteBuffer buff = ByteBuffer.allocate(8);

		// Extract 1
		buff.array()[0] = (byte) 0b10111111;
		buff.array()[1] = (byte) 0b10000000;
		buff.array()[2] = (byte) 0b00111111;
		buff.array()[3] = (byte) 0b01111111;
		buff.array()[4] = (byte) 0b11111111;
		buff.position(0);
		assertEquals(0b10111111, ByteBufferDataInput.extractUTF8Body(buff, 1));
		assertEquals(0b10000000, ByteBufferDataInput.extractUTF8Body(buff, 1));
		for (int i = 0; i < 3; i++) {
			assertThrows(UTFDataFormatException.class, () -> {
				ByteBufferDataInput.extractUTF8Body(buff, 1);
			});
		}
		buff.position(8);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Body(buff, 1);
		});

		// Extract 2
		buff.array()[0] = (byte) 0b10111111;
		buff.array()[1] = (byte) 0b10000000;
		buff.array()[2] = (byte) 0b10000000;
		buff.array()[3] = (byte) 0b10111111;
		buff.array()[4] = (byte) 0b10111111;
		buff.array()[5] = (byte) 0b00111111;
		buff.array()[6] = (byte) 0b01111111;
		buff.array()[7] = (byte) 0b10000000;
		buff.position(0);
		assertEquals(0b10111111_10000000, ByteBufferDataInput.extractUTF8Body(buff, 2));
		assertEquals(0b10000000_10111111, ByteBufferDataInput.extractUTF8Body(buff, 2));
		for (int i = 0; i < 2; i++) {
			assertThrows(UTFDataFormatException.class, () -> {
				ByteBufferDataInput.extractUTF8Body(buff, 2);
			});
		}
		buff.position(7);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Body(buff, 2);
		});
		buff.position(8);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Body(buff, 2);
		});
	}

	@Test
	void testExtractUTF8Char() throws Exception {
		ByteBuffer buff = ByteBuffer.allocate(8);

		// Read \0
		// Although modified UTF-8 states that zero is encoded as 2 bytes, the actual
		// implementation can read 0 as \0.
		buff.array()[0] = (byte) 0;
		assertEquals(0, ByteBufferDataInput.extractUTF8Char(buff));

		buff.array()[0] = (byte) 0b11000000;
		buff.array()[1] = (byte) 0b10000000;
		buff.position(0);
		assertEquals(0, ByteBufferDataInput.extractUTF8Char(buff));

		// Read anything with 1 byte
		for (int i = 1; i < 0x80; i++) {
			buff.array()[0] = (byte) i;
			buff.position(0);
			assertEquals(i, ByteBufferDataInput.extractUTF8Char(buff));
		}
		// EOF cannot be tested

		// Read 2 bytes
		buff.array()[0] = (byte) 0b11000010;
		buff.array()[1] = (byte) 0b10000000;
		buff.position(0);
		assertEquals(0x80, ByteBufferDataInput.extractUTF8Char(buff));
		buff.array()[0] = (byte) 0b11011111;
		buff.array()[1] = (byte) 0b10111111;
		buff.position(0);
		assertEquals(0x07FF, ByteBufferDataInput.extractUTF8Char(buff));

		// Broken 2 bytes
		buff.array()[0] = (byte) 0b11011111;
		buff.array()[1] = (byte) 0b00111111;
		buff.position(0);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Char(buff);
		});

		// Read 3 bytes
		buff.array()[0] = (byte) 0b11100000;
		buff.array()[1] = (byte) 0b10100000;
		buff.array()[2] = (byte) 0b10000000;
		buff.position(0);
		assertEquals(0x0800, ByteBufferDataInput.extractUTF8Char(buff));

		buff.array()[0] = (byte) 0b11101111;
		buff.array()[1] = (byte) 0b10111111;
		buff.array()[2] = (byte) 0b10111111;
		buff.position(0);
		assertEquals(0xFFFF, ByteBufferDataInput.extractUTF8Char(buff));

		// Broken 3 bytes
		buff.array()[0] = (byte) 0b11101111;
		buff.array()[1] = (byte) 0b10111111;
		buff.array()[2] = (byte) 0b00111111;
		buff.position(0);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Char(buff);
		});

		// Invalid headers
		buff.array()[0] = (byte) 0b10000000;
		buff.array()[1] = (byte) 0b11110000;
		buff.array()[2] = (byte) 0b11111000;
		buff.array()[3] = (byte) 0b11111100;
		buff.array()[4] = (byte) 0b11111110;
		buff.array()[5] = (byte) 0b11111111;
		buff.position(0);
		for (int i = 0; i < 6; i++) {
			assertThrows(UTFDataFormatException.class, () -> {
				ByteBufferDataInput.extractUTF8Char(buff);
			});
		}

		// Test EOF 2 bytes
		buff.array()[0] = (byte) 0b11000010;
		buff.array()[1] = (byte) 0b10000000;
		buff.position(0);
		buff.limit(1);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Char(buff);
		});

		// Test EOF 3 bytes
		buff.array()[0] = (byte) 0b11101111;
		buff.array()[1] = (byte) 0b10111111;
		buff.array()[2] = (byte) 0b00111111;
		buff.position(0);
		buff.limit(1);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Char(buff);
		});
		buff.position(0);
		buff.limit(2);
		assertThrows(UTFDataFormatException.class, () -> {
			ByteBufferDataInput.extractUTF8Char(buff);
		});
	}

	@Test
	void testReadUTF() throws Exception {
		String SAMPLE_STRING = "a\u0001\u007F\u0090\u07FF\u0800\uFFFF\0\0";
		byte[] SAMPLE_STRING_BIN = { (byte) 0x00, (byte) 0x10, (byte) 0x61, (byte) 0x01, (byte) 0x7F, (byte) 0xC2,
				(byte) 0x90, (byte) 0xDF, (byte) 0xBF, (byte) 0xE0, (byte) 0xA0, (byte) 0x80, (byte) 0xEF, (byte) 0xBF,
				(byte) 0xBF, (byte) 0xC0, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		ByteBufferDataInput in = new ByteBufferDataInput(ByteBuffer.wrap(SAMPLE_STRING_BIN));
		assertEquals(SAMPLE_STRING, in.readUTF());
		assertEquals("", in.readUTF());

		// EOF Exception
		in.source.position(0);
		in.source.limit(1);
		assertThrows(EOFException.class, () -> {
			in.readUTF();
		});
		in.source.position(0);
		in.source.limit(2);
		assertThrows(EOFException.class, () -> {
			in.readUTF();
		});
		in.source.position(0);
		in.source.limit(SAMPLE_STRING_BIN.length - 3);
		assertThrows(EOFException.class, () -> {
			in.readUTF();
		});

		// Bad encoding
		in.source.position(0);
		in.source.limit(SAMPLE_STRING_BIN.length);
		SAMPLE_STRING_BIN[7] = 0;
		assertThrows(UTFDataFormatException.class, () -> {
			in.readUTF();
		});
	}

	@Test
	void testReadUTFChar() throws Exception {
		String SAMPLE_STRING = "a\u0001\u007F\u0090\u07FF\u0800\uFFFF\0";

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bOut)) {
			out.writeUTF(SAMPLE_STRING);
		}
		ByteBuffer b = ByteBuffer.wrap(bOut.toByteArray());
		b.position(2);
		ByteBufferDataInput in = new ByteBufferDataInput(b);

		for (int i = 0; i < SAMPLE_STRING.length(); i++) {
			assertEquals(SAMPLE_STRING.charAt(i), in.readUTFChar() & 0xFFFF);
		}
	}
}
