/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.basic;

import org.hibernate.Internal;
import org.hibernate.metamodel.model.convert.spi.BasicValueConverter;
import org.hibernate.query.NavigablePath;
import org.hibernate.sql.results.graph.AssemblerCreationState;
import org.hibernate.sql.results.graph.DomainResult;
import org.hibernate.sql.results.graph.DomainResultAssembler;
import org.hibernate.type.descriptor.java.JavaType;

/**
 * @author Steve Ebersole
 */
public class BasicResult<T> implements DomainResult<T>, BasicResultGraphNode<T> {
	private final String resultVariable;
	private final JavaType<T> javaTypeDescriptor;

	private final NavigablePath navigablePath;

	private final DomainResultAssembler<T> assembler;

	public BasicResult(
			int jdbcValuesArrayPosition,
			String resultVariable,
			JavaType<T> javaTypeDescriptor) {
		this( jdbcValuesArrayPosition, resultVariable, javaTypeDescriptor, (NavigablePath) null );
	}

	public BasicResult(
			int jdbcValuesArrayPosition,
			String resultVariable,
			JavaType<T> javaTypeDescriptor,
			NavigablePath navigablePath) {
		this.resultVariable = resultVariable;
		this.javaTypeDescriptor = javaTypeDescriptor;

		this.navigablePath = navigablePath;

		this.assembler = new BasicResultAssembler<>( jdbcValuesArrayPosition, javaTypeDescriptor );
	}

	public BasicResult(
			int valuesArrayPosition,
			String resultVariable,
			JavaType<T> javaTypeDescriptor,
			BasicValueConverter<T,?> valueConverter) {
		this( valuesArrayPosition, resultVariable, javaTypeDescriptor, valueConverter, null );
	}

	public BasicResult(
			int valuesArrayPosition,
			String resultVariable,
			JavaType<T> javaTypeDescriptor,
			BasicValueConverter<T,?> valueConverter,
			NavigablePath navigablePath) {
		this.resultVariable = resultVariable;
		this.javaTypeDescriptor = javaTypeDescriptor;
		this.navigablePath = navigablePath;

		this.assembler = new BasicResultAssembler<>( valuesArrayPosition, javaTypeDescriptor, valueConverter );
	}

	@Override
	public String getResultVariable() {
		return resultVariable;
	}

	@Override
	public JavaType<T> getResultJavaTypeDescriptor() {
		return javaTypeDescriptor;
	}

	@Override
	public NavigablePath getNavigablePath() {
		return navigablePath;
	}

	/**
	 * For testing purposes only
	 */
	@Internal
	public DomainResultAssembler<T> getAssembler() {
		return assembler;
	}

	@Override
	public DomainResultAssembler<T> createResultAssembler(AssemblerCreationState creationState) {
		return assembler;
	}
}
