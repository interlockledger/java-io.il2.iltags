package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataInput;
import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.ILTagException;
import io.il2.iltags.tags.TagID;

class BytesTagTest {
	
	private static final byte[] SAMPLE_IDS = {(byte)0xfe, (byte)0xab, (byte) 0x7F, (byte) 0xAC};	

	@Test
	void testBytesTag() {
		BytesTag t = new BytesTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		BytesTag t = new BytesTag(123456);
		t.setValue(SAMPLE_IDS);
		assertEquals(SAMPLE_IDS, t.getValue());
	}

	@Test
	void testSetValue() {
		BytesTag t = new BytesTag(123456);
		t.setValue(SAMPLE_IDS);
		assertEquals(SAMPLE_IDS, t.getValue());
	}

	@Test
	void testGetValueSize() {
		BytesTag t = new BytesTag(123456);
		assertEquals(0, t.getValueSize());
		t.setValue(SAMPLE_IDS);
		assertEquals(4, t.getValueSize());		
	}

	@Test
	void testSerializeValue() throws IOException {
		BytesTag t = new BytesTag(123456);
		t.setValue(SAMPLE_IDS);
		ByteBuffer buff = ByteBuffer.allocate(4);
		ByteBufferDataOutput out = new ByteBufferDataOutput(buff);
		t.serializeValue(out);
		assertArrayEquals(SAMPLE_IDS,buff.array());
	}

	@Test
	void testDeserializeValue() throws IOException, ILTagException {
		ByteBuffer buff = ByteBuffer.wrap(SAMPLE_IDS);
		ByteBufferDataInput in = new ByteBufferDataInput(buff);
		BytesTag t = new BytesTag(123456);
		t.deserializeValue(null, 4, in);
		assertArrayEquals(SAMPLE_IDS, t.getValue());		
		t.deserializeValue(null, 0, in);
		assertThrows(EOFException.class, () -> {
			t.deserializeValue(null, 10, in);
		});
	}

	@Test
	void testCreateStandard() {
		BytesTag t = BytesTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BYTES_TAG_ID);
	}

}
