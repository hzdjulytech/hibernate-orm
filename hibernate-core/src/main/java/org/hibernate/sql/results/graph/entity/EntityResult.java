/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.entity;

import org.hibernate.sql.results.graph.DomainResult;

/**
 * Further defines a first-level Return that is a reference to an entity
 *
 * @author Steve Ebersole
 */
public interface EntityResult extends EntityResultGraphNode, DomainResult {
}
