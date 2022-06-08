package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.ilint.ILIntEncoder;
import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class Int16TagTest {

	private static final byte[] SAMPLE_IDS = {(byte)0xfe, (byte)0xab, (byte) 0x7F, (byte) 0xAC};
	
	@Test
	void testInt16Tag() {
		Int16Tag t = new Int16Tag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() throws IOException, ILTagException {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short)0xFEl);
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testSetValue() {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short) 0xFEl);
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testGetUnsignedValue() {
		Int16Tag t = new Int16Tag(123456);
		short uInt = (short) 0xFEl & 0xffff;
		t.setValue(uInt);		
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testSetUnsignedValue() {
		Int16Tag t = new Int16Tag(123456);
		short uInt = (short) 0xFEl & 0xffff;
		t.setValue(uInt);		
		assertEquals((short) 0xFEl, t.getValue());
	}

	@Test
	void testGetValueSize() {
		Int16Tag t = new Int16Tag(123456);
		assertEquals(2, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws IOException {
		Int16Tag t = new Int16Tag(123456);
		t.setValue((short) 0xfeab);
		ByteBuffer buff = ByteBuffer.allocate(4);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		t.setValue((short) 0x7fac);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		Int16Tag t = new Int16Tag(123456);
		t.deserializeValue(null, 2, in);
		assertEquals((short) 0xFEAB, t.getValue());
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});			
		
		t.deserializeValue(null, 2, in);
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 2, in);
		});
		
	}

	@Test
	void testCreateStandardSigned() {
		Int16Tag t = Int16Tag.createStandardSigned();
		assertEquals(t.getTagID(), TagID.IL_INT16_TAG_ID);
	}

	@Test
	void testCreateStandardUnsigned() {
		Int16Tag t = Int16Tag.createStandardUnsigned();
		assertEquals(t.getTagID(), TagID.IL_UINT16_TAG_ID);
	}

}
