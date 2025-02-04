/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.tree.expression;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Expression;

import org.hibernate.query.criteria.JpaExpression;
import org.hibernate.query.criteria.JpaSimpleCase;
import org.hibernate.query.internal.QueryHelper;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.SqmExpressable;
import org.hibernate.query.sqm.SemanticQueryWalker;
import org.hibernate.query.sqm.sql.internal.DomainResultProducer;

/**
 * @author Steve Ebersole
 */
public class SqmCaseSimple<T, R>
		extends AbstractSqmExpression<R>
		implements JpaSimpleCase<T, R>, DomainResultProducer<R> {
	private final SqmExpression<T> fixture;
	private final List<WhenFragment<T, R>> whenFragments;
	private SqmExpression<R> otherwise;

	public SqmCaseSimple(SqmExpression<T> fixture, NodeBuilder nodeBuilder) {
		this( fixture, null, nodeBuilder );
	}

	public SqmCaseSimple(SqmExpression<T> fixture, SqmExpressable<R> inherentType, NodeBuilder nodeBuilder) {
		super( inherentType, nodeBuilder );
		this.whenFragments = new ArrayList<>( );
		this.fixture = fixture;
	}

	public SqmCaseSimple(SqmExpression<T> fixture, SqmExpressable<R> inherentType, int estimateWhenSize, NodeBuilder nodeBuilder) {
		super( inherentType, nodeBuilder );
		this.whenFragments = new ArrayList<>( estimateWhenSize );
		this.fixture = fixture;
	}

	public SqmExpression<T> getFixture() {
		return fixture;
	}

	public List<WhenFragment<T,R>> getWhenFragments() {
		return whenFragments;
	}

	public SqmExpression<R> getOtherwise() {
		return otherwise;
	}

	public void otherwise(SqmExpression<R> otherwiseExpression) {
		this.otherwise = otherwiseExpression;

		applyInferableResultType( otherwiseExpression.getNodeType() );
	}

	public void when(SqmExpression<T> test, SqmExpression<R> result) {
		whenFragments.add( new WhenFragment<>( test, result ) );

		applyInferableResultType( result.getNodeType() );
	}

	private void applyInferableResultType(SqmExpressable<?> type) {
		if ( type == null ) {
			return;
		}

		final SqmExpressable<?> oldType = getNodeType();

		final SqmExpressable<?> newType = QueryHelper.highestPrecedenceType2(oldType, type );
		if ( newType != null && newType != oldType ) {
			internalApplyInferableType( newType );
		}
	}

	@Override
	protected void internalApplyInferableType(SqmExpressable newType) {
		super.internalApplyInferableType( newType );

		if ( otherwise != null ) {
			otherwise.applyInferableType( newType );
		}

		if ( whenFragments != null ) {
			whenFragments.forEach(
					whenFragment -> whenFragment.getResult().applyInferableType( newType )
			);
		}
	}

	@Override
	public <X> X accept(SemanticQueryWalker<X> walker) {
		return walker.visitSimpleCaseExpression( this );
	}

	@Override
	public String asLoggableText() {
		return "<simple-case>";
	}

	public static class WhenFragment<T,R> {
		private final SqmExpression<T> checkValue;
		private final SqmExpression<R> result;

		public WhenFragment(SqmExpression<T> checkValue, SqmExpression<R> result) {
			this.checkValue = checkValue;
			this.result = result;
		}

		public SqmExpression<T> getCheckValue() {
			return checkValue;
		}

		public SqmExpression<R> getResult() {
			return result;
		}
	}

	@Override
	public void appendHqlString(StringBuilder sb) {
		sb.append( "case " );
		fixture.appendHqlString( sb );
		for ( WhenFragment<T, R> whenFragment : whenFragments ) {
			sb.append( " when " );
			whenFragment.checkValue.appendHqlString( sb );
			sb.append( " then " );
			whenFragment.result.appendHqlString( sb );
		}

		if ( otherwise != null ) {
			sb.append( " else " );
			otherwise.appendHqlString( sb );
		}
		sb.append( " end" );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// JPA

	@Override
	public JpaExpression<T> getExpression() {
		return getFixture();
	}

	@Override
	public JpaSimpleCase<T, R> when(T condition, R result) {
		when( nodeBuilder().value( condition ), nodeBuilder().value( result ) );
		return this;
	}

	@Override
	public JpaSimpleCase<T, R> when(T condition, Expression<? extends R> result) {
		//noinspection unchecked
		when( nodeBuilder().value( condition, (SqmExpression<T>) result ), (SqmExpression<R>) result );
		return this;
	}

	@Override
	public JpaSimpleCase<T, R> otherwise(R result) {
		otherwise( nodeBuilder().value( result ) );
		return this;
	}

	@Override
	public JpaSimpleCase<T, R> otherwise(Expression<? extends R> result) {
		//noinspection unchecked
		otherwise( (SqmExpression<R>) result );
		return this;
	}

}
