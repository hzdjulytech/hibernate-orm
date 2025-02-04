/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.spi;

import org.hibernate.Transaction;

/**
 * Encapsulates settings controlling whether certain aspects of the JPA spec
 * should be strictly followed.
 *
 * @author Steve Ebersole
 */
public interface JpaCompliance {
	/**
	 * Controls whether Hibernate's handling of JPA's
	 * {@link jakarta.persistence.Query} (JPQL, Criteria and native-query) should
	 * strictly follow the JPA spec.  This includes both in terms of parsing or
	 * translating a query as well as calls to the {@link jakarta.persistence.Query}
	 * methods throwing spec defined exceptions where as Hibernate might not.
	 *
	 * Deviations result in an exception if enabled
	 *
	 * @return {@code true} indicates to behave in the spec-defined way
	 */
	boolean isJpaQueryComplianceEnabled();

	/**
	 * Indicates that Hibernate's {@link Transaction} should behave as
	 * defined by the spec for JPA's {@link jakarta.persistence.EntityTransaction}
	 * since it extends the JPA one.
	 *
	 * @return {@code true} indicates to behave in the spec-defined way
	 */
	boolean isJpaTransactionComplianceEnabled();

	/**
	 * Controls how Hibernate interprets a mapped List without an "order columns"
	 * specified.  Historically Hibernate defines this as a "bag", which is a concept
	 * JPA does not have.
	 *
	 * If enabled, Hibernate will recognize this condition as defining
	 * a {@link org.hibernate.collection.internal.PersistentList}, otherwise
	 * Hibernate will treat is as a {@link org.hibernate.collection.internal.PersistentBag}
	 *
	 * @return {@code true} indicates to behave in the spec-defined way, interpreting the
	 * mapping as a "list", rather than a "bag"
	 */
	boolean isJpaListComplianceEnabled();

	/**
	 * JPA defines specific exceptions on specific methods when called on
	 * {@link jakarta.persistence.EntityManager} and {@link jakarta.persistence.EntityManagerFactory}
	 * when those objects have been closed.  This setting controls
	 * whether the spec defined behavior or Hibernate's behavior will be used.
	 *
	 * If enabled Hibernate will operate in the JPA specified way throwing
	 * exceptions when the spec says it should with regard to close checking
	 *
	 * @return {@code true} indicates to behave in the spec-defined way
	 */
	boolean isJpaClosedComplianceEnabled();

	/**
	 * JPA spec says that an {@link jakarta.persistence.EntityNotFoundException}
	 * should be thrown when accessing an entity Proxy which does not have an associated
	 * table row in the database.
	 *
	 * Traditionally, Hibernate does not initialize an entity Proxy when accessing its
	 * identifier since we already know the identifier value, hence we can save a database roundtrip.
	 *
	 * If enabled Hibernate will initialize the entity Proxy even when accessing its identifier.
	 *
	 * @return {@code true} indicates to behave in the spec-defined way
	 */
	boolean isJpaProxyComplianceEnabled();

	/**
	 * Should Hibernate comply with all aspects of caching as defined by JPA?  Or can
	 * it deviate to perform things it believes will be "better"?
	 *
	 * @implNote Effects include marking all secondary tables as non-optional.  The reason
	 * being that optional secondary tables can lead to entity cache being invalidated rather
	 * than updated.
	 *
	 * @return {@code true} says to act the spec-defined way.
	 */
	boolean isJpaCacheComplianceEnabled();

	/**
	 * Should the the scope of {@link jakarta.persistence.TableGenerator#name()} and {@link jakarta.persistence.SequenceGenerator#name()} be
	 * considered globally or locally defined?
	 *
	 * @return {@code true} indicates the generator name scope is considered global.
	 */
	boolean isGlobalGeneratorScopeEnabled();

	/**
	 * Should we strictly handle {@link jakarta.persistence.OrderBy} expressions?
	 *
	 * JPA says the order-items can only be attribute references whereas Hibernate supports a wide range of items.  With
	 * this enabled, Hibernate will throw a compliance error when a non-attribute-reference is used.
	 */
	boolean isJpaOrderByMappingComplianceEnabled();

	/**
	 * JPA says that the id passed to {@link jakarta.persistence.EntityManager#getReference} and
	 * {@link jakarta.persistence.EntityManager#find} should be the exact expected type, allowing
	 * no type coercion.
	 *
	 * Historically, Hibernate behaved the same way.  Since 6.0 however, Hibernate has the ability to
	 * coerce the passed type to the expected type.
	 *
	 * This setting controls whether such a coercion should be allowed.
	 *
	 * @since 6.0
	 */
	boolean isLoadByIdComplianceEnabled();
}
