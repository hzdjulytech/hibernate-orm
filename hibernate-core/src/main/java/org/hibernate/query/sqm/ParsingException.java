/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm;

import org.hibernate.HibernateException;
import org.hibernate.query.SemanticException;

/**
 * The root exception for errors (potential bugs) in the sqm parser code itself, as opposed
 * to {@link SemanticException} which indicates problems with the sqm.
 *
 * @author Steve Ebersole
 */
public class ParsingException extends HibernateException {
	public ParsingException(String message) {
		super( message );
	}

	public ParsingException(String message, Throwable cause) {
		super( message, cause );
	}
}
