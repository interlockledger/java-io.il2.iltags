package io.il2.iltags.tags.basic;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import io.il2.iltags.io.ByteBufferDataOutput;
import io.il2.iltags.tags.TagID;

class RangeTagTest {

	@Test
	void testRangeTag() {
		RangeTag t = new RangeTag(123456);
		assertEquals(123456, t.getTagID());	
	}

	@Test
	void testGetFirst() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getFirst());	
	}

	@Test
	void testSetFirst() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getFirst());	
		t.setFirst(1);
		assertEquals(1, t.getFirst());			
	}

	@Test
	void testGetCount() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getCount());
		t.setCount(5);
		assertEquals(5, t.getCount());		
	}

	@Test
	void testSetCount() {
		RangeTag t = new RangeTag(123456);
		assertEquals(0, t.getCount());
		t.setCount(5);
		assertEquals(5, t.getCount());	
	}

	@Test
	void testGetValueSize() {
		RangeTag t = new RangeTag(123456);
		assertEquals(3, t.getValueSize()); //3 = first(1) + 2
		t.setCount(5);
		assertEquals(3, t.getValueSize()); 	
		t.setFirst(5);
		assertEquals(3, t.getValueSize()); 		
		
	}

	@Test
	void testSerializeValue() {
		fail("Not yet implemented");
	}

	@Test
	void testDeserializeValue() {
		fail("Not yet implemented");
	}

	@Test
	void testCreateStandard() {
		RangeTag t = RangeTag.createStandard();
		assertEquals(t.getTagID(), TagID.IL_RANGE_TAG_ID);
	}

}
