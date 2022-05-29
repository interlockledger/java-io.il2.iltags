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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

class ByteBufferDataOutputTest {

	private Random random = new Random();

	@Test
	void testByteBufferDataOutput() {
		ByteBuffer b = ByteBuffer.allocate(1);

		ByteBufferDataOutput out = new ByteBufferDataOutput(b);
		assertSame(b, out.dest);
		assertEquals(ByteOrder.BIG_ENDIAN, b.order());

		b.order(ByteOrder.LITTLE_ENDIAN);
		out = new ByteBufferDataOutput(b);
		assertSame(b, out.dest);
		assertEquals(ByteOrder.BIG_ENDIAN, b.order());

		assertThrows(IllegalArgumentException.class, () -> {
			new ByteBufferDataOutput(b.asReadOnlyBuffer());
		});
	}

	@Test
	void testWriteInt() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(32));
		ByteBuffer exp = ByteBuffer.allocate(32);

		while (exp.hasRemaining()) {
			int v = random.nextInt();
			exp.putInt(v);
			out.writeInt(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 3; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeInt(0);
			});
		}
	}

	@Test
	void testWriteByteArray() throws Exception {
		byte[] exp = new byte[32];
		random.nextBytes(exp);
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(exp.length));

		out.write(new byte[0]);
		assertEquals(0, out.dest.position());

		out.write(exp);
		assertEquals(exp.length, out.dest.position());
		assertArrayEquals(exp, out.dest.array());

		out.write(new byte[0]);
		assertEquals(exp.length, out.dest.position());

		out.dest.position(0);
		byte[] small = new byte[16];
		random.nextBytes(small);
		out.write(small);
		assertEquals(16, out.dest.position());
		out.write(small);
		assertEquals(32, out.dest.position());
		System.arraycopy(small, 0, exp, 0, 16);
		System.arraycopy(small, 0, exp, 16, 16);
		assertArrayEquals(exp, out.dest.array());

		out.dest.position(0);
		out.dest.limit(exp.length - 1);
		assertThrows(IOException.class, () -> {
			out.write(exp);
		});
	}

	@Test
	void testWriteByteArrayIntInt() throws Exception {
		byte[] exp = new byte[32];
		byte[] tmp = new byte[34];
		random.nextBytes(exp);
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(exp.length));

		System.arraycopy(exp, 0, tmp, 1, 32);

		out.write(tmp, 1, 0);
		assertEquals(0, out.dest.position());

		out.write(tmp, 1, 32);
		assertEquals(exp.length, out.dest.position());
		assertArrayEquals(exp, out.dest.array());

		out.write(tmp, 1, 0);
		assertEquals(exp.length, out.dest.position());

		out.dest.position(0);
		byte[] small = new byte[18];
		random.nextBytes(small);
		out.write(small, 1, 16);
		assertEquals(16, out.dest.position());
		out.write(small, 1, 16);
		assertEquals(32, out.dest.position());
		System.arraycopy(small, 1, exp, 0, 16);
		System.arraycopy(small, 1, exp, 16, 16);
		assertArrayEquals(exp, out.dest.array());

		out.dest.position(0);
		out.dest.limit(exp.length - 1);
		assertThrows(IOException.class, () -> {
			out.write(tmp, 1, 32);
		});
	}

	@Test
	void testWriteBoolean() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(8));
		ByteBuffer exp = ByteBuffer.allocate(8);

		while (exp.hasRemaining()) {
			int v = (byte) random.nextInt() & 0x1;
			exp.put((byte) v);
			out.writeBoolean(v == 0x1);
		}
		assertArrayEquals(exp.array(), out.dest.array());

		out.dest.position(0);
		out.dest.limit(0);
		assertThrows(IOException.class, () -> {
			out.writeBoolean(true);
		});
	}

	@Test
	void testWriteByte() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(8));
		ByteBuffer exp = ByteBuffer.allocate(8);

		while (exp.hasRemaining()) {
			byte v = (byte) random.nextInt();
			exp.put(v);
			out.writeByte(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());

		out.dest.position(0);
		out.dest.limit(0);
		assertThrows(IOException.class, () -> {
			out.writeInt(0);
		});
	}

	@Test
	void testWriteShort() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(16));
		ByteBuffer exp = ByteBuffer.allocate(16);

		while (exp.hasRemaining()) {
			short v = (short) random.nextInt();
			exp.putShort(v);
			out.writeShort(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 1; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeShort(0);
			});
		}
	}

	@Test
	void testWriteChar() throws Exception {
		Random random = new Random();
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(16));
		ByteBuffer exp = ByteBuffer.allocate(16);

		while (exp.hasRemaining()) {
			char v = (char) random.nextInt();
			exp.putChar(v);
			out.writeChar(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 1; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeChar(0);
			});
		}
	}

	@Test
	void testWriteLong() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(64));
		ByteBuffer exp = ByteBuffer.allocate(64);

		while (exp.hasRemaining()) {
			long v = random.nextLong();
			exp.putLong(v);
			out.writeLong(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 7; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeLong(0);
			});
		}
	}

	@Test
	void testWriteFloat() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(32));
		ByteBuffer exp = ByteBuffer.allocate(32);

		while (exp.hasRemaining()) {
			float v = random.nextFloat();
			exp.putFloat(v);
			out.writeFloat(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 3; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeFloat(0);
			});
		}
	}

	@Test
	void testWriteDouble() throws Exception {
		ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(64));
		ByteBuffer exp = ByteBuffer.allocate(64);

		while (exp.hasRemaining()) {
			double v = random.nextDouble();
			exp.putDouble(v);
			out.writeDouble(v);
		}
		assertArrayEquals(exp.array(), out.dest.array());
		for (int i = 0; i < 7; i++) {
			out.dest.position(0);
			out.dest.limit(i);
			assertThrows(IOException.class, () -> {
				out.writeDouble(0);
			});
		}
	}

	@Test
	void testWriteBytes() throws Exception {
		final ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(64));
		ByteBuffer exp = ByteBuffer.allocate(64);

		exp.limit(32);
		out.dest.limit(32);
		StringBuilder sb = new StringBuilder();
		while (exp.hasRemaining()) {
			char v = (char) random.nextInt();
			sb.append(v);
			exp.put((byte) (v & 0xFF));
		}
		out.writeBytes(sb.toString());
		assertArrayEquals(exp.array(), out.dest.array());

		exp.limit(64);
		out.dest.limit(64);
		out.dest.position(0);
		Arrays.fill(out.dest.array(), (byte) 0);
		out.writeBytes(sb.toString());
		assertArrayEquals(exp.array(), out.dest.array());

		// Test empty
		out.writeBytes("");
		assertEquals(32, out.dest.position());

		// EOF
		out.dest.limit(0);
		out.dest.position(0);
		assertThrows(IOException.class, () -> {
			out.writeBytes("1");
		});
	}

	@Test
	void testWriteChars() throws Exception {
		final ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(64));
		ByteBuffer exp = ByteBuffer.allocate(64);

		exp.limit(32);
		out.dest.limit(32);
		StringBuilder sb = new StringBuilder();
		while (exp.hasRemaining()) {
			char v = (char) random.nextInt();
			sb.append(v);
			exp.putChar(v);
		}
		out.writeChars(sb.toString());
		assertArrayEquals(exp.array(), out.dest.array());

		exp.limit(64);
		out.dest.limit(64);
		out.dest.position(0);
		Arrays.fill(out.dest.array(), (byte) 0);
		out.writeChars(sb.toString());
		assertArrayEquals(exp.array(), out.dest.array());

		// Test empty
		out.writeBytes("");
		assertEquals(32, out.dest.position());

		// EOF
		out.dest.limit(0);
		out.dest.position(0);
		assertThrows(IOException.class, () -> {
			out.writeChars("1");
		});
		out.dest.limit(1);
		out.dest.position(0);
		assertThrows(IOException.class, () -> {
			out.writeChars("1");
		});
	}

	@Test
	void testUtf8CharSize() {
		assertEquals(2, ByteBufferDataOutput.utf8CharSize(0x0000));
		assertEquals(1, ByteBufferDataOutput.utf8CharSize(0x0001));
		assertEquals(1, ByteBufferDataOutput.utf8CharSize(0x007F));
		assertEquals(2, ByteBufferDataOutput.utf8CharSize(0x0080));
		assertEquals(2, ByteBufferDataOutput.utf8CharSize(0x07FF));
		assertEquals(3, ByteBufferDataOutput.utf8CharSize(0x0800));
		assertEquals(3, ByteBufferDataOutput.utf8CharSize(0xFFFF));
	}

	@Test
	void testUtf8Len() {
		assertEquals(0, ByteBufferDataOutput.utf8Len(""));
		assertEquals(2, ByteBufferDataOutput.utf8Len("\0"));
		assertEquals(1, ByteBufferDataOutput.utf8Len("\u0001"));
		assertEquals(1, ByteBufferDataOutput.utf8Len("\u007F"));
		assertEquals(2, ByteBufferDataOutput.utf8Len("\u0080"));
		assertEquals(2, ByteBufferDataOutput.utf8Len("\u07FF"));
		assertEquals(3, ByteBufferDataOutput.utf8Len("\u0800"));
		assertEquals(3, ByteBufferDataOutput.utf8Len("\uFFFF"));
		assertEquals(15, ByteBufferDataOutput.utf8Len("a\u0001\u007F\u0090\u07FF\u0800\uFFFF\0"));
	}

	@Test
	void testWriteUTFCharCore() {
		final ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(4));

		// 0
		out.writeUTFCharCore(0x0000);
		assertEquals(2, out.dest.position());
		assertEquals(0b11000000, out.dest.array()[0] & 0xFF);
		assertEquals(0b10000000, out.dest.array()[1] & 0xFF);

		// 1 byte
		out.dest.position(0);
		out.writeUTFCharCore(0x0001);
		assertEquals(1, out.dest.position());
		assertEquals(0b00000001, out.dest.array()[0] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x002A);
		assertEquals(1, out.dest.position());
		assertEquals(0b00101010, out.dest.array()[0] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x0055);
		assertEquals(1, out.dest.position());
		assertEquals(0b01010101, out.dest.array()[0] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x007F);
		assertEquals(1, out.dest.position());
		assertEquals(0b01111111, out.dest.array()[0] & 0xFF);

		// 2 bytes
		out.dest.position(0);
		out.writeUTFCharCore(0x0080);
		assertEquals(2, out.dest.position());
		assertEquals(0b11000010, out.dest.array()[0] & 0xFF);
		assertEquals(0b10000000, out.dest.array()[1] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x02AA);
		assertEquals(2, out.dest.position());
		assertEquals(0b11001010, out.dest.array()[0] & 0xFF);
		assertEquals(0b10101010, out.dest.array()[1] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x0555);
		assertEquals(2, out.dest.position());
		assertEquals(0b11010101, out.dest.array()[0] & 0xFF);
		assertEquals(0b10010101, out.dest.array()[1] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x07FF);
		assertEquals(2, out.dest.position());
		assertEquals(0b11011111, out.dest.array()[0] & 0xFF);
		assertEquals(0b10111111, out.dest.array()[1] & 0xFF);

		// 3 bytes
		out.dest.position(0);
		out.writeUTFCharCore(0x0800);
		assertEquals(3, out.dest.position());
		assertEquals(0b11100000, out.dest.array()[0] & 0xFF);
		assertEquals(0b10100000, out.dest.array()[1] & 0xFF);
		assertEquals(0b10000000, out.dest.array()[2] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x0800);
		assertEquals(3, out.dest.position());
		assertEquals(0b11100000, out.dest.array()[0] & 0xFF);
		assertEquals(0b10100000, out.dest.array()[1] & 0xFF);
		assertEquals(0b10000000, out.dest.array()[2] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0x5555);
		assertEquals(3, out.dest.position());
		assertEquals(0b11100101, out.dest.array()[0] & 0xFF);
		assertEquals(0b10010101, out.dest.array()[1] & 0xFF);
		assertEquals(0b10010101, out.dest.array()[2] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0xAAAA);
		assertEquals(3, out.dest.position());
		assertEquals(0b11101010, out.dest.array()[0] & 0xFF);
		assertEquals(0b10101010, out.dest.array()[1] & 0xFF);
		assertEquals(0b10101010, out.dest.array()[2] & 0xFF);
		out.dest.position(0);
		out.writeUTFCharCore(0xFFFF);
		assertEquals(3, out.dest.position());
		assertEquals(0b11101111, out.dest.array()[0] & 0xFF);
		assertEquals(0b10111111, out.dest.array()[1] & 0xFF);
		assertEquals(0b10111111, out.dest.array()[2] & 0xFF);
	}

	@Test
	void testWriteUTF() throws IOException {
		String SAMPLE_STRING = "a\u0001\u007F\u0090\u07FF\u0800\uFFFF\0";
		byte[] SAMPLE_STRING_BIN = { (byte) 0x00, (byte) 0x0F, (byte) 0x61, (byte) 0x01, (byte) 0x7F, (byte) 0xC2,
				(byte) 0x90, (byte) 0xDF, (byte) 0xBF, (byte) 0xE0, (byte) 0xA0, (byte) 0x80, (byte) 0xEF, (byte) 0xBF,
				(byte) 0xBF, (byte) 0xC0, (byte) 0x80 };

		final ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(SAMPLE_STRING_BIN.length));
		out.writeUTF(SAMPLE_STRING);
		assertEquals(SAMPLE_STRING_BIN.length, out.dest.position());
		assertArrayEquals(SAMPLE_STRING_BIN, out.dest.array());

		out.dest.position(0);
		out.writeUTF("");
		assertEquals(2, out.dest.position());
		assertEquals(0, out.dest.array()[0]);
		assertEquals(0, out.dest.array()[1]);

		// Cannot write the size
		out.dest.position(0);
		out.dest.limit(0);
		assertThrows(IOException.class, () -> {
			out.writeUTF("");
		});
		out.dest.position(0);
		out.dest.limit(1);
		assertThrows(IOException.class, () -> {
			out.writeUTF("");
		});

		// Cannot write 1
		out.dest.position(0);
		out.dest.limit(2);
		assertThrows(IOException.class, () -> {
			out.writeUTF("a");
		});

		// Cannot write 2
		out.dest.position(0);
		out.dest.limit(3);
		assertThrows(IOException.class, () -> {
			out.writeUTF("\u0080");
		});

		// Cannot write 3
		out.dest.position(0);
		out.dest.limit(4);
		assertThrows(IOException.class, () -> {
			out.writeUTF("\u0800");
		});
	}

	@Test
	void testWriteUTFChar() throws Exception {
		String SAMPLE_STRING = "a\u0001\u007F\u0090\u07FF\u0800\uFFFF\0";
		byte[] SAMPLE_STRING_BIN = { (byte) 0x61, (byte) 0x01, (byte) 0x7F, (byte) 0xC2, (byte) 0x90, (byte) 0xDF,
				(byte) 0xBF, (byte) 0xE0, (byte) 0xA0, (byte) 0x80, (byte) 0xEF, (byte) 0xBF, (byte) 0xBF, (byte) 0xC0,
				(byte) 0x80 };

		final ByteBufferDataOutput out = new ByteBufferDataOutput(ByteBuffer.allocate(SAMPLE_STRING_BIN.length));
		for (int i = 0; i < SAMPLE_STRING.length(); i++) {
			out.writeUTFChar(SAMPLE_STRING.charAt(i));
		}
		assertEquals(SAMPLE_STRING_BIN.length, out.dest.position());
		assertArrayEquals(SAMPLE_STRING_BIN, out.dest.array());

		// Cannot write 1
		out.dest.position(0);
		out.dest.limit(0);
		assertThrows(IOException.class, () -> {
			out.writeUTFChar(0x01);
		});

		// Cannot write 2
		out.dest.position(0);
		out.dest.limit(1);
		assertThrows(IOException.class, () -> {
			out.writeUTFChar(0x0080);
		});

		// Cannot write 3
		out.dest.position(0);
		out.dest.limit(2);
		assertThrows(IOException.class, () -> {
			out.writeUTFChar(0x0800);
		});
	}
}
