package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.TagID;

class BooleanTagTest {
	
	@Test
	void testBooleanTag() {
		BooleanTag t = new BooleanTag(123456);
		assertEquals(123456, t.getTagID());
	}

	@Test
	void testGetValue() {
		BooleanTag t = new BooleanTag(123456);
		t.setValue(true);
		assertEquals(true, t.getValue());
		t.setValue(false);
		assertEquals(false, t.getValue());		
	}

	@Test
	void testSetValue() {
		BooleanTag t = new BooleanTag(123456);
		t.setValue(true);
		assertEquals(true, t.getValue());
		t.setValue(false);
		assertEquals(false, t.getValue());		
	}

	@Test
	void testGetValueSize() {
		BooleanTag t = new BooleanTag(123456);
		assertEquals(1, t.getValueSize());
	}

	@Test
	void testSerializeValue() throws IOException {
		fail("Not yet implemented");					
	}
	

	@Test
	void testDeserializeValue() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateStandard() {
		BooleanTag t = BooleanTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_BOOL_TAG_ID);
	}

}
