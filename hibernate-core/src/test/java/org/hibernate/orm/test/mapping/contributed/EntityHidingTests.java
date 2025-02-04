/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.contributed;

import org.hibernate.boot.spi.AdditionalJaxbMappingProducer;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.RuntimeMetamodels;
import org.hibernate.metamodel.model.domain.EntityDomainType;
import org.hibernate.persister.entity.EntityPersister;

import org.hibernate.testing.orm.junit.BootstrapServiceRegistry;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.NotImplementedYet;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

/**
 * @author Steve Ebersole
 */
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService( role = AdditionalJaxbMappingProducer.class, impl = BasicContributorTests.Contributor.class )
)
@ServiceRegistry(
		settings = @Setting(
				name = AvailableSettings.STATIC_METAMODEL_POPULATION,
				value = "skipUnsupported"
		)
)
@DomainModel( annotatedClasses = BasicContributorTests.MainEntity.class )
@SessionFactory
public class EntityHidingTests {
	@Test
	@NotImplementedYet( reason = "Contributed entity hiding is not yet implemented", strict = false )
	public void testModel(SessionFactoryScope scope) {
		final SessionFactoryImplementor sessionFactory = scope.getSessionFactory();
		final RuntimeMetamodels runtimeMetamodels = sessionFactory.getRuntimeMetamodels();

		final EntityDomainType<Object> jpaModelDescriptor = runtimeMetamodels.getJpaMetamodel().entity( "DynamicEntity" );
		assertThat( jpaModelDescriptor, nullValue() );

		final EntityPersister mappingModelDescriptor = runtimeMetamodels.getMappingMetamodel()
				.findEntityDescriptor( "DynamicEntity" );
		assertThat( mappingModelDescriptor, nullValue() );
	}
}
