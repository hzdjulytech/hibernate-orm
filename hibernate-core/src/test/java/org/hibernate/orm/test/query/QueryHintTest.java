/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.query;

import java.time.LocalDate;
import java.util.Collections;

import org.hibernate.graph.GraphParser;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.IllegalQueryOperationException;

import org.hibernate.testing.orm.assertj.HibernateInitializedCondition;
import org.hibernate.testing.orm.domain.StandardDomainModel;
import org.hibernate.testing.orm.domain.contacts.Contact;
import org.hibernate.testing.orm.domain.contacts.PhoneNumber;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Steve Ebersole
 */
@DomainModel( standardModels = StandardDomainModel.CONTACTS )
@SessionFactory
public class QueryHintTest {
	@Test
	public void testFetchGraphHint(SessionFactoryScope scope) {
		final Contact queried = scope.fromTransaction( (session) -> {
			return session.createQuery( "from Contact", Contact.class )
					.setHint( QueryHints.HINT_FETCHGRAPH, GraphParser.parse( Contact.class, "phoneNumbers", session ) )
					.uniqueResult();
		} );

		assertThat( queried ).is( HibernateInitializedCondition.IS_INITIALIZED );
		assertThat( queried.getPhoneNumbers() ).is( HibernateInitializedCondition.IS_INITIALIZED );
	}

	@Test
	public void testLoadGraphHint(SessionFactoryScope scope) {
		final Contact queried = scope.fromTransaction( (session) -> {
			return session.createQuery( "from Contact", Contact.class )
					.setHint( QueryHints.HINT_LOADGRAPH, GraphParser.parse( Contact.class, "phoneNumbers", session ) )
					.uniqueResult();
		} );

		assertThat( queried ).is( HibernateInitializedCondition.IS_INITIALIZED );
		assertThat( queried.getPhoneNumbers() ).is( HibernateInitializedCondition.IS_INITIALIZED );
	}

	@Test
	public void testQueryTimeoutHint(SessionFactoryScope scope) {
		// see `org.hibernate.test.querytimeout.QueryTimeOutTest`
	}

	@BeforeEach
	public void prepareTestData(SessionFactoryScope scope) {
		scope.inTransaction( (session) -> {
			final Contact contact = new Contact(
					1,
					new Contact.Name( "John", "Jingleheimer-Schmidt"),
					Contact.Gender.MALE,
					LocalDate.of(1970, 1, 1)
			);
			contact.setPhoneNumbers(
					Collections.singletonList(
							new PhoneNumber( 123, 456, 7890, PhoneNumber.Classification.OTHER )
					)
			);
			session.persist( contact );
		} );
	}

	@AfterEach
	public void dropTestData(SessionFactoryScope scope) {
		scope.inTransaction( (session) -> {
			session.createQuery( "from Contact" ).list().forEach( session::delete );
		} );
	}

}
