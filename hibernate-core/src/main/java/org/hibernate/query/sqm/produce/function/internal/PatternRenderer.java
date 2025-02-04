/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.produce.function.internal;

import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.sql.ast.SqlAstNodeRenderingMode;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Distinct;
import org.hibernate.sql.ast.tree.expression.Star;
import org.hibernate.sql.ast.tree.predicate.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Delegate for handling function "templates".
 *
 * @author Steve Ebersole
 */
public class PatternRenderer {
	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( PatternRenderer.class );

	private final String[] chunks;
	private final int[] paramIndexes;
	private final int varargParam;
	private final int maxParamIndex;
	private final SqlAstNodeRenderingMode argumentRenderingMode;

	public PatternRenderer(String pattern) {
		this( pattern, SqlAstNodeRenderingMode.DEFAULT );
	}

	/**
	 * Constructs a template renderer
	 *
	 * @param pattern The template
	 * @param argumentRenderingMode The rendering mode for arguments
	 */
	public PatternRenderer(String pattern, SqlAstNodeRenderingMode argumentRenderingMode) {
		final Set<Integer> paramNumbers = new HashSet<>();
		final List<String> chunkList = new ArrayList<>();
		final List<Integer> paramList = new ArrayList<>();
		final StringBuilder chunk = new StringBuilder( 10 );
		final StringBuilder index = new StringBuilder( 2 );

		int vararg = -1;
		int max = 0;

		int i = 0;
		final int len = pattern.length();
		while ( i < len ) {
			char c = pattern.charAt( i );
			if ( c == '?' ) {
				chunkList.add( chunk.toString() );
				chunk.setLength(0);

				while ( ++i < pattern.length() ) {
					c = pattern.charAt( i );
					if ( Character.isDigit( c ) ) {
						index.append( c );
					}
					else if ( c == '.' ) {
						index.append( c );
					}
					else if ( c  == '?' ) {
						i--;
						break;
					}
					else {
						chunk.append( c );
						break;
					}
				}

				if ( index.toString().endsWith("...") ) {
					vararg = paramList.size();
				}
				else {
					int paramNumber = Integer.valueOf( index.toString() );
					paramNumbers.add( paramNumber );
					paramList.add( paramNumber );
					index.setLength(0);
					if ( paramNumber > max ) {
						max = paramNumber;
					}
				}
			}
			else {
				chunk.append( c );
			}
			i++;
		}

		if ( chunk.length() > 0 ) {
			chunkList.add( chunk.toString() );
		}

		this.varargParam = vararg;
		this.maxParamIndex = max;

		this.chunks = chunkList.toArray( new String[chunkList.size()] );
		int[] paramIndexes = new int[paramList.size()];
		for ( i = 0; i < paramIndexes.length; ++i ) {
			paramIndexes[i] = paramList.get( i );
		}
		this.paramIndexes = paramIndexes;
		this.argumentRenderingMode = argumentRenderingMode;
	}

	public boolean hasVarargs() {
		return varargParam >= 0;
	}

	public int getParamCount() {
		return maxParamIndex;
	}

	/**
	 * The rendering code.
	 *
	 * @param sqlAppender
	 * @param args The arguments to inject into the template
	 * @return The rendered template with replacements
	 */
	@SuppressWarnings({ "UnusedDeclaration" })
	public void render(
			SqlAppender sqlAppender,
			List<SqlAstNode> args,
			SqlAstTranslator<?> translator) {
		render( sqlAppender, args, null, translator );
	}

	public void render(
			SqlAppender sqlAppender,
			List<SqlAstNode> args,
			Predicate filter,
			SqlAstTranslator<?> translator) {
		final int numberOfArguments = args.size();
		final boolean caseWrapper = filter != null && !translator.supportsFilterClause();
		if ( numberOfArguments < maxParamIndex ) {
			LOG.missingArguments( maxParamIndex, numberOfArguments );
		}

		for ( int i = 0; i < chunks.length; i++ ) {
			if ( i == varargParam ) {
				for ( int j = i; j < numberOfArguments; j++ ) {
					final SqlAstNode arg = args.get( j );
					if ( arg != null ) {
						sqlAppender.appendSql( chunks[i] );
						if ( caseWrapper && !( arg instanceof Distinct ) && !( arg instanceof Star ) ) {
							sqlAppender.appendSql( "case when " );
							filter.accept( translator );
							sqlAppender.appendSql( " then " );
							translator.render( arg, argumentRenderingMode );
							sqlAppender.appendSql( " else null end" );
						}
						else {
							translator.render( arg, argumentRenderingMode );
						}
					}
				}
			}
			else if ( i < paramIndexes.length ) {
				final int index = paramIndexes[i] - 1;
				final SqlAstNode arg = index < numberOfArguments ? args.get( index ) : null;
				if ( arg != null || i == 0 ) {
					sqlAppender.appendSql( chunks[i] );
				}
				if ( arg != null ) {
					if ( caseWrapper && !( arg instanceof Distinct ) && !( arg instanceof Star ) ) {
						sqlAppender.appendSql( "case when " );
						filter.accept( translator );
						sqlAppender.appendSql( " then " );
						translator.render( arg, argumentRenderingMode );
						sqlAppender.appendSql( " else null end" );
					}
					else {
						translator.render( arg, argumentRenderingMode );
					}
				}
			}
			else {
				sqlAppender.appendSql( chunks[i] );
			}
		}

		if ( filter != null && !caseWrapper ) {
			sqlAppender.appendSql( " filter (where " );
			filter.accept( translator );
			sqlAppender.appendSql( ')' );
		}
	}
}
