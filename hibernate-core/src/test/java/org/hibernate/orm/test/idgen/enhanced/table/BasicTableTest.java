/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.idgen.enhanced.table;

import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.persister.entity.EntityPersister;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hibernate.id.IdentifierGeneratorHelper.BasicHolder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@DomainModel( xmlMappings = "org/hibernate/orm/test/idgen/enhanced/table/Basic.hbm.xml" )
@SessionFactory
public class BasicTableTest {

	@Test
	public void testNormalBoundary(SessionFactoryScope scope) {
		final EntityPersister persister = scope.getSessionFactory().getEntityPersister( Entity.class.getName() );
		assertThat( persister.getIdentifierGenerator(), instanceOf( TableGenerator.class ) );

		final TableGenerator generator = ( TableGenerator ) persister.getIdentifierGenerator();

		scope.inTransaction(
				(s) -> {
					int count = 5;
					for ( int i = 0; i < count; i++ ) {
						final Entity entity = new Entity( "" + ( i + 1 ) );
						s.save( entity );
						long expectedId = i + 1;
						assertEquals( expectedId, entity.getId().longValue() );
						assertEquals( expectedId, generator.getTableAccessCount() );
						assertEquals( expectedId, ( (BasicHolder) generator.getOptimizer().getLastSourceValue() ).getActualLongValue() );
					}
				}
		);
	}

	@AfterEach
	public void cleanTestData(SessionFactoryScope scope) {
		scope.inTransaction(
				(session) -> session.createQuery( "delete Entity" ).executeUpdate()
		);
	}
}
