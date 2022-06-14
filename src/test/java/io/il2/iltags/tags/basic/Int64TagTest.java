package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.CorruptedTagException;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class Int64TagTest {
	
	private static final byte[] SAMPLE_IDS = {(byte)0xFE, (byte)0xAB, (byte) 0xFC, (byte) 0xDD, (byte)0xAD, (byte)0xCB, (byte) 0xFB, (byte) 0xEE};	


	@Test
	void testInt64Tag() {
		Int64Tag t = new Int64Tag(123456);
		assertEquals(123456, t.getTagID());	}

	@Test
	void testGetValue() {
		Int64Tag t = new Int64Tag(123456);
		t.setValue((long)0xFEABFCDDADCBFBEEL);
		assertEquals((long)0xFEABFCDDADCBFBEEL, t.getValue());
	}

	@Test
	void testSetValue() {
		Int64Tag t = new Int64Tag(123456);
		t.setValue((long)0xFEABFCDDADCBFBEEL);
		assertEquals((long)0xFEABFCDDADCBFBEEL, t.getValue());
	}

	@Test
	void testGetValueSize() {
		Int64Tag t = new Int64Tag(123456);
		assertEquals(8, t.getValueSize());
		t.setValue((long)0xFEABFCDDADCBFBEEL);
		assertEquals(8, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws IOException {
		Int64Tag t = new Int64Tag(123456);
		t.setValue((long) 0xFEABFCDDADCBFBEEL);
		ByteBuffer buff = ByteBuffer.allocate(8);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);

		Int64Tag t = new Int64Tag(123456);
		t.deserializeValue(null, 8, in);
		assertEquals((long) 0xFEABFCDDADCBFBEEL, t.getValue());
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 0, in);
		});
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 1, in);
		});				
		assertThrows(CorruptedTagException.class, () -> {
			t.deserializeValue(null, 4, in);
		});		
		assertThrows(IOException.class, () -> {
			t.deserializeValue(null, 8, in);
		});				
	}

	@Test
	void testCreateStandardSigned() {
		Int64Tag t = Int64Tag.createStandardSigned();
		assertEquals(t.getTagID(), TagID.IL_INT64_TAG_ID);
	}

	@Test
	void testCreateStandardUnsigned() {
		Int64Tag t = Int64Tag.createStandardUnsigned();
		assertEquals(t.getTagID(), TagID.IL_UINT64_TAG_ID);
	}

}
