/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.userguide.mapping.basic.bitset;

import java.util.BitSet;

/**
 * @author Steve Ebersole
 */
public class BitSetHelper {
	public static final String DELIMITER = ",";
	public static final byte[] BYTES = new byte[] { 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	public static String bitSetToString(BitSet bitSet) {
		StringBuilder builder = new StringBuilder();
		for ( long token : bitSet.toLongArray() ) {
			if ( builder.length() > 0 ) {
				builder.append( DELIMITER );
			}
			builder.append( Long.toString( token, 2 ) );
		}
		return builder.toString();
	}

	public static BitSet stringToBitSet(String string) {
		if ( string == null || string.isEmpty() ) {
			return null;
		}
		String[] tokens = string.split( DELIMITER );
		long[] values = new long[tokens.length];

		for ( int i = 0; i < tokens.length; i++ ) {
			values[i] = Long.valueOf( tokens[i], 2 );
		}
		return BitSet.valueOf( values );
	}

	public static byte[] bitSetToBytes(BitSet bitSet) {
		return bitSet == null ? null : bitSet.toByteArray();
	}

	public static BitSet bytesToBitSet(byte[] bytes) {
		return bytes == null || bytes.length == 0
				? null
				: BitSet.valueOf( bytes );
	}
}
