/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.usertype;

import java.util.Properties;
import java.util.function.BiConsumer;

import org.hibernate.MappingException;
import org.hibernate.type.BasicType;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.type.spi.TypeConfigurationAware;

/**
 * Convenience UserType implementation to mimic the legacy `@Type` annotation
 * which based on the {@code hbm.xml} mapping's String-based type support
 *
 * @see org.hibernate.annotations.CustomType
 */
public class UserTypeLegacyBridge extends BaseUserTypeSupport<Object> implements ParameterizedType, TypeConfigurationAware {
	public static final String TYPE_NAME_PARAM_KEY = "hbm-type-name";

	private TypeConfiguration typeConfiguration;
	private String hbmStyleTypeName;

	public UserTypeLegacyBridge() {
	}

	public UserTypeLegacyBridge(String hbmStyleTypeName) {
		this.hbmStyleTypeName = hbmStyleTypeName;
	}

	@Override
	public TypeConfiguration getTypeConfiguration() {
		return typeConfiguration;
	}

	@Override
	public void setTypeConfiguration(TypeConfiguration typeConfiguration) {
		this.typeConfiguration = typeConfiguration;
	}

	@Override
	public void setParameterValues(Properties parameters) {
		if ( hbmStyleTypeName != null ) {
			// assume it was ctor-injected
			return;
		}

		hbmStyleTypeName = parameters.getProperty( TYPE_NAME_PARAM_KEY );
		if ( hbmStyleTypeName == null ) {
			throw new MappingException( "Missing `@Parameter` for `" + TYPE_NAME_PARAM_KEY + "`" );
		}
	}

	@Override
	protected void resolve(BiConsumer resolutionConsumer) {
		assert typeConfiguration != null;

		final BasicType<Object> registeredType = typeConfiguration
				.getBasicTypeRegistry()
				.getRegisteredType( hbmStyleTypeName );

		resolutionConsumer.accept(
				registeredType.getJavaTypeDescriptor(),
				registeredType.getJdbcTypeDescriptor()
		);
	}
}
