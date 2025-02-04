/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.userguide.mapping.basic;

import java.sql.Types;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.mapping.JdbcMapping;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for mapping byte values
 *
 * @author Steve Ebersole
 */
@DomainModel( annotatedClasses = ByteMappingTests.EntityOfBytes.class )
@SessionFactory
public class ByteMappingTests {

	@Test
	public void testMappings(SessionFactoryScope scope) {
		// first, verify the type selections...
		final MappingMetamodel domainModel = scope.getSessionFactory().getDomainModel();
		final EntityPersister entityDescriptor = domainModel.findEntityDescriptor( EntityOfBytes.class );
		final JdbcTypeRegistry jdbcTypeRegistry = domainModel.getTypeConfiguration()
				.getJdbcTypeDescriptorRegistry();

		{
			final BasicAttributeMapping attribute = (BasicAttributeMapping) entityDescriptor.findAttributeMapping( "wrapper" );
			assertThat( attribute.getJavaTypeDescriptor().getJavaTypeClass(), equalTo( Byte.class ) );

			final JdbcMapping jdbcMapping = attribute.getJdbcMapping();
			assertThat( jdbcMapping.getJavaTypeDescriptor().getJavaTypeClass(), equalTo( Byte.class ) );
			assertThat( jdbcMapping.getJdbcTypeDescriptor(), is( jdbcTypeRegistry.getDescriptor( Types.TINYINT ) ) );
		}

		{
			final BasicAttributeMapping attribute = (BasicAttributeMapping) entityDescriptor.findAttributeMapping( "primitive" );
			assertThat( attribute.getJavaTypeDescriptor().getJavaTypeClass(), equalTo( Byte.class ) );

			final JdbcMapping jdbcMapping = attribute.getJdbcMapping();
			assertThat( jdbcMapping.getJavaTypeDescriptor().getJavaTypeClass(), equalTo( Byte.class ) );
			assertThat( jdbcMapping.getJdbcTypeDescriptor(), is( jdbcTypeRegistry.getDescriptor( Types.TINYINT ) ) );
		}


		// and try to use the mapping
		scope.inTransaction(
				(session) -> session.persist( new EntityOfBytes( 1, (byte) 3, (byte) 5 ) )
		);
		scope.inTransaction(
				(session) -> session.get( EntityOfBytes.class, 1 )
		);
	}

	@AfterEach
	public void dropData(SessionFactoryScope scope) {
		scope.inTransaction(
				(session) -> session.createQuery( "delete EntityOfBytes" ).executeUpdate()
		);
	}

	@Entity( name = "EntityOfBytes" )
	@Table( name = "EntityOfBytes" )
	public static class EntityOfBytes {
		@Id
		Integer id;

		//tag::basic-byte-example-implicit[]
		// these will both be mapped using TINYINT
		Byte wrapper;
		byte primitive;
		//end::basic-byte-example-implicit[]


		public EntityOfBytes() {
		}

		public EntityOfBytes(Integer id, Byte wrapper, byte primitive) {
			this.id = id;
			this.wrapper = wrapper;
			this.primitive = primitive;
		}
	}
}
