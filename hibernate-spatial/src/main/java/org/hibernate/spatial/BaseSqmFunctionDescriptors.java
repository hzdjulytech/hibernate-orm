/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.spatial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.query.sqm.function.NamedSqmFunctionDescriptor;
import org.hibernate.query.sqm.function.SqmFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.type.BasicTypeRegistry;

public class BaseSqmFunctionDescriptors implements KeyedSqmFunctionDescriptors {
	protected final Map<FunctionKey, SqmFunctionDescriptor> map = new HashMap<>();

	public BaseSqmFunctionDescriptors(FunctionContributions functionContributions) {
		final BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
		for ( CommonSpatialFunction func : filter( CommonSpatialFunction.values() ) ) {
			final FunctionReturnTypeResolver returnTypeResolver;
			if ( func.getReturnType() == null ) {
				returnTypeResolver = null;
			}
			else {
				returnTypeResolver = StandardFunctionReturnTypeResolvers.invariant(
						basicTypeRegistry.resolve( func.getReturnType() )
				);
			}
			map.put(
					func.getKey(),
					new NamedSqmFunctionDescriptor(
							func.getKey().getName(),
							true,
							StandardArgumentsValidators.exactly( func.getNumArgs() ),
							returnTypeResolver
					)
			);
		}
	}

	public CommonSpatialFunction[] filter(CommonSpatialFunction[] functions) {
		return functions;
	}

	public Map<FunctionKey, SqmFunctionDescriptor> asMap() {
		return Collections.unmodifiableMap( map );
	}
}
