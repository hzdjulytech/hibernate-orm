/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.SqmQuerySource;
import org.hibernate.query.sqm.internal.SqmUtil;
import org.hibernate.query.sqm.tree.expression.SqmParameter;
import org.hibernate.query.sqm.internal.ParameterCollector;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractSqmStatement<T> extends AbstractSqmNode implements SqmStatement<T>, ParameterCollector {
	private final SqmQuerySource querySource;

	public AbstractSqmStatement(
			SqmQuerySource querySource,
			NodeBuilder builder) {
		super( builder );
		this.querySource = querySource;
	}

	private Set<SqmParameter<?>> parameters;

	@Override
	public SqmQuerySource getQuerySource() {
		return querySource;
	}

	@Override
	public void addParameter(SqmParameter<?> parameter) {
		if ( parameters == null ) {
			parameters = new HashSet<>();
		}

		parameters.add( parameter );
	}

	@Override
	public Set<SqmParameter<?>> getSqmParameters() {
		if ( querySource == SqmQuerySource.CRITERIA ) {
			assert parameters == null : "SqmSelectStatement (as Criteria) should not have collected parameters";

			return org.hibernate.query.sqm.tree.jpa.ParameterCollector.collectParameters(
					this,
					sqmParameter -> {},
					nodeBuilder().getServiceRegistry()
			);
		}

		return parameters == null ? Collections.emptySet() : Collections.unmodifiableSet( parameters );
	}

	@Override
	public ParameterResolutions resolveParameters() {
		return SqmUtil.resolveParameters( this );
	}
}
