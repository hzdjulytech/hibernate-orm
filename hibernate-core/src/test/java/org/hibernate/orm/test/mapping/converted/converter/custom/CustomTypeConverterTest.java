/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.converted.converter.custom;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tool.schema.Action;
import org.hibernate.type.spi.TypeConfiguration;

import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Steve Ebersole
 */
public class CustomTypeConverterTest extends BaseUnitTestCase {
	@Test
	public void testConverterAppliedScopedRegistration() {

		try ( final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder()
				.applySetting( AvailableSettings.HBM2DDL_AUTO, Action.CREATE_DROP )
				.build() ) {
			final MetadataSources metadataSources = new MetadataSources( ssr )
					.addAnnotatedClass( PayloadWrapperConverter.class )
					.addAnnotatedClass( MyEntity.class );
			final MetadataBuilderImplementor metadataBuilder = (MetadataBuilderImplementor) metadataSources.getMetadataBuilder();

			// now the new scoped way
			final TypeConfiguration bootTypeConfiguration = metadataBuilder.getBootstrapContext().getTypeConfiguration();
			bootTypeConfiguration.getJavaTypeDescriptorRegistry()
					.addDescriptor( PayloadWrapperJavaType.INSTANCE );
			bootTypeConfiguration.getJdbcTypeDescriptorRegistry()
					.addDescriptor( PayloadWrapperJdbcType.INSTANCE );

			performAssertions( metadataBuilder, bootTypeConfiguration );
		}
	}

	protected void performAssertions(
			MetadataBuilderImplementor metadataBuilder,
			TypeConfiguration bootTypeConfiguration) {
		try ( final SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) metadataBuilder.build().buildSessionFactory()) {
			assertThat(
					sessionFactory.getMetamodel().getTypeConfiguration(),
					sameInstance( bootTypeConfiguration )
			);

			assertThat(
					bootTypeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor( PayloadWrapper.class ),
					sameInstance( PayloadWrapperJavaType.INSTANCE )
			);

			assertThat(
					bootTypeConfiguration.getJdbcTypeDescriptorRegistry().getDescriptor( PayloadWrapperJdbcType.INSTANCE.getJdbcTypeCode() ),
					sameInstance( PayloadWrapperJdbcType.INSTANCE )
			);

			final EntityPersister entityPersister = sessionFactory.getMetamodel().entityPersister( MyEntity.class );
			entityPersister.getPropertyType( "customType" );
		}
	}
}
