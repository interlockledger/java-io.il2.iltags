package io.il2.iltags.tags;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
	void testReadHeader() {
		fail("Not yet implemented");
	}

	@Test
	void testReadILInt() {
		fail("Not yet implemented");
	}

	@Test
	void testReadSignedILInt() {
		fail("Not yet implemented");
	}

	@Test
	void testAssertArraySize() {
		fail("Not yet implemented");
	}
}
