package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class DoubleTagTest {
	
	// hex representation of double 3.6 - 0x400ccccccccccccd
	private static final byte[] SAMPLE_IDS = {(byte)0x40, (byte)0x0c, (byte) 0xcc, (byte) 0xcc,  (byte)0xcc, (byte)0xcc, (byte) 0xcc, (byte) 0xcd};		
	

	@Test
	void testDoubleTag() {
		DoubleTag t = new DoubleTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		DoubleTag t = new DoubleTag(123456);
		t.setValue(3.6);
		assertEquals(3.6, t.getValue());
	}

	@Test
	void testSetValue() {
		DoubleTag t = new DoubleTag(123456);
		t.setValue(3.6);
		assertEquals(3.6, t.getValue());
	}

	@Test
	void testGetValueSize() {
		DoubleTag t = new DoubleTag(123456);
		assertEquals(8, t.getValueSize());
		t.setValue(3.6);
		assertEquals(8, t.getValueSize());	
	}

	@Test
	void testSerializeValue() throws IOException {
		DoubleTag t = new DoubleTag(123456);
		t.setValue(3.6);
		ByteBuffer buff = ByteBuffer.allocate(8);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		DoubleTag t = new DoubleTag(123456);
		t.deserializeValue(null, 8, in);
		assertEquals(3.6, t.getValue());
		
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});			
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});		
		
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 8, in);
		});		
	}

	@Test
	void testCreateStandard() {
		DoubleTag t = DoubleTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BIN64_TAG_ID);
	}

}
