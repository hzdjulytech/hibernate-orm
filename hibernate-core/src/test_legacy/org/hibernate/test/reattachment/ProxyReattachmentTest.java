/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.reattachment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

/**
 * Test of proxy reattachment semantics
 *
 * @author Steve Ebersole
 */
public class ProxyReattachmentTest extends BaseCoreFunctionalTestCase {
	public String[] getMappings() {
		return new String[] { "reattachment/Mappings.hbm.xml" };
	}

	@SuppressWarnings( {"unchecked"})
	public void testIterateWithClearTopOfLoop() {
		Session s = openSession();
		s.beginTransaction();
		Set parents = new HashSet();
		for (int i=0; i<5; i++) {
			Parent p = new Parent( String.valueOf( i ) );
			Child child = new Child( "child" + i );
			child.setParent( p );
			p.getChildren().add( child );
			s.save( p );
			parents.add(p);
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		int i = 0;
		for ( Iterator it = session.createQuery( "from Parent p " ).iterate(); it.hasNext(); ) {
			i++;
			if (i % 2 == 0) {
				s.flush();
				s.clear();
			}
			Parent p = (Parent) it.next();

			assertEquals( 1, p.getChildren().size() );
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		for ( Object parent : parents ) {
			s.delete( parent );
		}
		s.getTransaction().commit();
		s.close();
	}

	@Test
	@SuppressWarnings( {"unchecked"})
	public void testIterateWithClearBottomOfLoop() {
		Session s = openSession();
		s.beginTransaction();
		Set parents = new HashSet();
		for (int i=0; i<5; i++) {
			Parent p = new Parent( String.valueOf( i ) );
			Child child = new Child( "child" + i );
			child.setParent( p );
			p.getChildren().add( child );
			s.save( p );
			parents.add(p);
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		int i = 0;
		for (Iterator it = session.createQuery( "from Parent p " ).iterate(); it.hasNext(); ) {
			Parent p = (Parent) it.next();
			assertEquals( 1, p.getChildren().size() );
			i++;
			if (i % 2 == 0) {
				s.flush();
				s.clear();
			}
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		for ( Object parent : parents ) {
			s.delete( parent );
		}
		s.getTransaction().commit();
		s.close();
	}

	@Test
	@SuppressWarnings( {"unchecked"})
	public void testIterateWithEvictTopOfLoop() {
		Session s = openSession();
		s.beginTransaction();
		Set parents = new HashSet();
		for (int i=0; i<5; i++) {
			Parent p = new Parent( String.valueOf( i + 100 ) );
			Child child = new Child( "child" + i );
			child.setParent( p );
			p.getChildren().add( child );
			s.save( p );
			parents.add(p);
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		Parent p = null;
		for (Iterator it = session.createQuery( "from Parent" ).iterate(); it.hasNext(); ) {
			if ( p != null) { s.evict(p); }
			p = (Parent) it.next();
			assertEquals( 1, p.getChildren().size() );
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		for ( Object parent : parents ) {
			s.delete( parent );
		}
		s.getTransaction().commit();
		s.close();
	}

	@Test
	@SuppressWarnings( {"unchecked"})
	public void testIterateWithEvictBottomOfLoop() {
		Session s = openSession();
		s.beginTransaction();
		Set parents = new HashSet();
		for (int i=0; i<5; i++) {
			Parent p = new Parent( String.valueOf( i + 100 ) );
			Child child = new Child( "child" + i );
			child.setParent( p );
			p.getChildren().add( child );
			s.save( p );
			parents.add(p);
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		for (Iterator it = s.createQuery( "from Parent" ).iterate(); it.hasNext(); ) {
			Parent p = (Parent) it.next();
			assertEquals( 1, p.getChildren().size() );
			s.evict(p);
		}
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		for ( Object parent : parents ) {
			s.delete( parent );
		}
		s.getTransaction().commit();
		s.close();
	}
	
}
