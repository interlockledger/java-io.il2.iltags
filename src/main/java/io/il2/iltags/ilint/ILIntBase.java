package io.il2.iltags.ilint;

/**
 * This interface defines some u
 * 
 * @author Fabio Jun Takada Chino
 * @since 2022.05.25
 */
public interface ILIntBase {

	/**
	 * LInt base value. All values smaller than this value are encoded as single
	 * byte.
	 */
	static final int ILINT_BASE = 0xF8;

	/**
	 * The base ILInt value as a 64 bit integer.
	 */
	static final long ILINT_BASE64 = (long) ILINT_BASE;

	/**
	 * Maximum value of the body. Any value larger than it will result in an
	 * overflow.
	 */
	static final long MAX_BODY_VALUE = 0xFFFF_FFFF_FFFF_FF07l;
}
