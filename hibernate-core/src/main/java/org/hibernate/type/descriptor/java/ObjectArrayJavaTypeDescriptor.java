/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.type.descriptor.WrapperOptions;

/**
 * Descriptor for {@code Object[]} handling, usually used for tuples.
 *
 * @author Christian Beikov
 */
public class ObjectArrayJavaTypeDescriptor extends AbstractClassJavaTypeDescriptor<Object[]> {

	private final JavaType<Object>[] components;

	public ObjectArrayJavaTypeDescriptor(JavaType<?>[] components) {
		super(
				Object[].class,
				ImmutableMutabilityPlan.INSTANCE,
				new ComponentArrayComparator( (JavaType<Object>[]) components )
		);
		this.components = (JavaType<Object>[]) components;
	}

	@Override
	public String toString(Object[] value) {
		final StringBuilder sb = new StringBuilder();
		sb.append( '(' );
		sb.append( components[0].toString( value[0] ) );
		for ( int i = 1; i < components.length; i++ ) {
			sb.append( ", " );
			sb.append( components[i].toString( value[i] ) );
		}
		sb.append( ')' );
		return sb.toString();
	}

	@Override
	public boolean areEqual(Object[] one, Object[] another) {
		if ( one == another ) {
			return true;
		}
		if ( one != null && another != null && one.length == another.length ) {
			for ( int i = 0; i < components.length; i++ ) {
				if ( !components[i].areEqual( one[i], another[i] ) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int extractHashCode(Object[] objects) {
		int hashCode = 1;
		for ( int i = 0; i < objects.length; i++ ) {
			hashCode = 31 * hashCode + components[i].extractHashCode( objects[i] );
		}
		return hashCode;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <X> X unwrap(Object[] value, Class<X> type, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( Object[].class.isAssignableFrom( type ) ) {
			return (X) value;
		}
		throw unknownUnwrap( type );
	}

	@Override
	public <X> Object[] wrap(X value, WrapperOptions options) {
		if ( value == null ) {
			return null;
		}
		if ( Object[].class.isInstance( value ) ) {
			return (Object[]) value;
		}
		throw unknownWrap( value.getClass() );
	}
}
