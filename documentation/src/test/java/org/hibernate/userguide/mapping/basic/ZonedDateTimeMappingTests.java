/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.userguide.mapping.basic;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.metamodel.MappingMetamodel;
import org.hibernate.metamodel.mapping.JdbcMapping;
import org.hibernate.metamodel.mapping.internal.BasicAttributeMapping;
import org.hibernate.persister.entity.EntityPersister;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isOneOf;

/**
 * @author Steve Ebersole
 */
@DomainModel( annotatedClasses = ZonedDateTimeMappingTests.EntityWithZonedDateTime.class )
@SessionFactory
public class ZonedDateTimeMappingTests {

	@Test
	public void verifyMappings(SessionFactoryScope scope) {
		final MappingMetamodel domainModel = scope.getSessionFactory().getDomainModel();
		final EntityPersister entityDescriptor = domainModel.findEntityDescriptor( EntityWithZonedDateTime.class );

		final BasicAttributeMapping attributeMapping = (BasicAttributeMapping) entityDescriptor.findAttributeMapping( "zonedDateTime" );
		final JdbcMapping jdbcMapping = attributeMapping.getJdbcMapping();
		assertThat( jdbcMapping.getJavaTypeDescriptor().getJavaTypeClass(), equalTo( ZonedDateTime.class ) );
		assertThat( jdbcMapping.getJdbcTypeDescriptor().getJdbcTypeCode(), isOneOf( Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE ) );

		scope.inTransaction(
				(session) -> {
					session.persist( new EntityWithZonedDateTime( 1, ZonedDateTime.now() ) );
				}
		);

		scope.inTransaction(
				(session) -> session.find( EntityWithZonedDateTime.class, 1 )
		);
	}

	@Entity( name = "EntityWithZonedDateTime" )
	@Table( name = "EntityWithZonedDateTime" )
	public static class EntityWithZonedDateTime {
		@Id
		private Integer id;

		//tag::basic-ZonedDateTime-example[]
		// mapped as TIMESTAMP or TIMESTAMP_WITH_TIMEZONE
		private ZonedDateTime zonedDateTime;
		//end::basic-ZonedDateTime-example[]

		public EntityWithZonedDateTime() {
		}

		public EntityWithZonedDateTime(Integer id, ZonedDateTime zonedDateTime) {
			this.id = id;
			this.zonedDateTime = zonedDateTime;
		}
	}
}
