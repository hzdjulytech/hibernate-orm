/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.convert.spi;

import jakarta.persistence.AttributeConverter;

import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.type.descriptor.java.JavaType;

/**
 * BasicValueConverter extension for AttributeConverter-specific support
 *
 * @author Steve Ebersole
 */
public interface JpaAttributeConverter<O,R> extends BasicValueConverter<O,R> {
	JavaType<? extends AttributeConverter<O,R>> getConverterJavaTypeDescriptor();

	ManagedBean<? extends AttributeConverter<O,R>> getConverterBean();

	JavaType<O> getDomainJavaTypeDescriptor();
	JavaType<R> getRelationalJavaTypeDescriptor();
}
