/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.basic;

import java.sql.Types;
import java.util.Set;

import org.hibernate.annotations.CustomType;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.usertype.UserTypeSupport;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Steve Ebersole
 */
public class CollectionAsBasicTest {
	@Test
	public void testCollectionAsBasic() {
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
		try {
			Metadata metadata = new MetadataSources(ssr).addAnnotatedClass( Post.class ).getMetadataBuilder().build();
			PersistentClass postBinding = metadata.getEntityBinding( Post.class.getName() );
			Property tagsAttribute = postBinding.getProperty( "tags" );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( ssr );
		}
	}

	@Entity
	@Table( name = "post")
	public static class Post {
		@Id
		public Integer id;
		public String name;
		@Basic
		@CustomType( DelimitedStringsJavaTypeDescriptor.class )
		Set<String> tags;
	}

	public static class DelimitedStringsJavaTypeDescriptor extends UserTypeSupport<Set> {
		public DelimitedStringsJavaTypeDescriptor() {
			super( Set.class, Types.VARCHAR );
		}
	}
}
