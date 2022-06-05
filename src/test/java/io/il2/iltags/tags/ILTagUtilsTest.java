package io.il2.iltags.tags;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;

class ILTagUtilsTest {

	@Test
	void testAssertTagSizeLimit() throws Exception {
		ILTagUtils.assertTagSizeLimit(0);
		ILTagUtils.assertTagSizeLimit(ILTag.MAX_TAG_VALUE_SIZE);

		assertThrows(TagTooLargeException.class, () -> {
			ILTagUtils.assertTagSizeLimit(ILTag.MAX_TAG_VALUE_SIZE + 1);
		});
		assertThrows(TagTooLargeException.class, () -> {
			ILTagUtils.assertTagSizeLimit(-1);
		});
	}

	@Test
	void testReadHeader() throws Exception {

		ILTagHeader h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 0 }));
		assertEquals(0, h.tagId);
		assertEquals(0, h.valueSize);

		h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 1, 1 }));
		assertEquals(1, h.tagId);
		assertEquals(1, h.valueSize);

		h = ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 16, 17 }));
		assertEquals(16, h.tagId);
		assertEquals(17, h.valueSize);

		// Bad ID
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08, 0 }));
		});

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readHeader(new ByteBufferDataInput(new byte[] { 16, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }));
		});
	}

	@Test
	void testReadILInt() throws Exception {

		assertEquals(0xF7l, ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xF7 }), "Test"));

		assertEquals(0xFFFF_FFFF_FFFF_FFFFl,
				ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x07 }), "Test"));

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }), "Test");
		});
	}

	@Test
	void testReadSignedILInt() throws Exception {

		assertEquals(0, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x0 }), "Test"));
		assertEquals(-1, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x1 }), "Test"));
		assertEquals(1, ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0x2 }), "Test"));
		assertEquals(9223372036854775807l,
				ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x06 }), "Test"));
		assertEquals(-9223372036854775808l,
				ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x07 }), "Test"));

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.readSignedILInt(new ByteBufferDataInput(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x08 }), "Test");
		});

	}

	@Test
	void testAssertArraySize() throws Exception {

		ILTagUtils.assertArraySize(0, 0, 0);

		ILTagUtils.assertArraySize(1, 1, 1);
		ILTagUtils.assertArraySize(1, 1, 2);

		ILTagUtils.assertArraySize(2, 2, 4);
		ILTagUtils.assertArraySize(2, 2, 5);

		// Special case with count as 2^64 -1 and value size as 2^64 -1 and minimum
		// entry size = 1
		ILTagUtils.assertArraySize(-1, 1, -1);

		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(1, 1, 0);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(2, 2, 3);
		});
		assertThrows(CorruptedTagException.class, () -> {
			ILTagUtils.assertArraySize(-1, 1, 1);
		});
	}
}
