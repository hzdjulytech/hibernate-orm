/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dynamicentity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
public class ProxyHelper {

	public static Person newPersonProxy() {
		return newPersonProxy( null );
	}

	public static Person newPersonProxy(Object id) {
		return ( Person ) Proxy.newProxyInstance(
				Person.class.getClassLoader(),
		        new Class[] {Person.class},
		        new DataProxyHandler( Person.class.getName(), id )
		);
	}

	public static Customer newCustomerProxy() {
		return newCustomerProxy( null );
	}

	public static Customer newCustomerProxy(Object id) {
		return ( Customer ) Proxy.newProxyInstance(
				Customer.class.getClassLoader(),
		        new Class[] {Customer.class},
		        new DataProxyHandler( Customer.class.getName(), id )
		);
	}

	public static Company newCompanyProxy() {
		return newCompanyProxy( null );
	}

	public static Company newCompanyProxy(Object id) {
		return ( Company ) Proxy.newProxyInstance(
				Company.class.getClassLoader(),
		        new Class[] {Company.class},
		        new DataProxyHandler( Company.class.getName(), id )
		);
	}

	public static Address newAddressProxy() {
		return newAddressProxy( null );
	}

	public static Address newAddressProxy(Object id) {
		return ( Address ) Proxy.newProxyInstance(
				Address.class.getClassLoader(),
		        new Class[] {Address.class},
		        new DataProxyHandler( Address.class.getName(), id )
		);
	}

	public static String extractEntityName(Object object) {
		// Our custom java.lang.reflect.Proxy instances actually bundle
		// their appropriate entity name, so we simply extract it from there
		// if this represents one of our proxies; otherwise, we return null
		if ( Proxy.isProxyClass( object.getClass() ) ) {
			InvocationHandler handler = Proxy.getInvocationHandler( object );
			if ( DataProxyHandler.class.isAssignableFrom( handler.getClass() ) ) {
				DataProxyHandler myHandler = ( DataProxyHandler ) handler;
				return myHandler.getEntityName();
			}
		}
		return null;
	}
}
