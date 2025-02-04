/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.spatial.testing.dialects.oracle;

import org.hibernate.spatial.JTSGeometryJavaTypeDescriptor;
import org.hibernate.spatial.dialect.oracle.SDOGeometryValueExtractor;
import org.hibernate.spatial.testing.AbstractExpectationsFactory;
import org.hibernate.spatial.testing.NativeSQLStatement;

import org.geolatte.geom.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

/**
 * Expectations factory for Oracle 10g (SDOGeometry).
 *
 * @Author Karel Maesen, Geovise BVBA
 */
public class SDOGeometryExpectationsFactory extends AbstractExpectationsFactory {

	private final SDOGeometryValueExtractor decoder = new SDOGeometryValueExtractor(
			JTSGeometryJavaTypeDescriptor.GEOMETRY_INSTANCE,
			null
	);

	public SDOGeometryExpectationsFactory() {
		super();
	}

	@Override
	public NativeSQLStatement createNativeTouchesStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Touch(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Touch(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeOverlapsStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Overlap(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Overlap(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeRelateStatement(Geometry geom, String matrix) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Relate(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326), '" + matrix + "') from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Relate(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326), '" + matrix + "') = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeDwithinStatement(Point geom, double distance) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, 1 from GEOMTEST T where MDSYS.SDO_WITHIN_DISTANCE(t.GEOM, SDO_GEOMETRY(? , 4326), 'distance = " + distance + "') = 'TRUE' and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeIntersectsStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Intersects(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Intersects(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeFilterStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, 1 from GEOMTEST t where SDO_FILTER(t.GEOM, MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326).GEOM)  = 'TRUE' ",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeDistanceStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Distance(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeDimensionSQL() {
		return createNativeSQLStatement(
				"select ID, MDSYS.OGC_DIMENSION(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM( T.GEOM)) FROM GEOMTEST T"
		);
	}

	@Override
	public NativeSQLStatement createNativeBufferStatement(Double distance) {
		return createNativeSQLStatement(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Buffer(?).GEOM from GEOMTEST T where t.GEOM.SDO_SRID = 4326",
				new Double[] { distance }
		);
	}

	@Override
	public NativeSQLStatement createNativeConvexHullStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Union(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)).ST_ConvexHull().GEOM from GEOMTEST T where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeIntersectionStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Intersection(MDSYS.ST_GEOMETRY.FROM_WKT(?,4326)).GEOM FROM GEOMTEST t where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeDifferenceStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Difference(MDSYS.ST_GEOMETRY.FROM_WKT(?,4326)).GEOM FROM GEOMTEST t where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeSymDifferenceStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_SymmetricDifference(MDSYS.ST_GEOMETRY.FROM_WKT(?,4326)).GEOM FROM GEOMTEST t where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeGeomUnionStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Union(MDSYS.ST_GEOMETRY.FROM_WKT(?,4326)).GEOM FROM GEOMTEST t where t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeAsTextStatement() {
		return createNativeSQLStatement( "select t.ID, t.GEOM.GET_WKT() FROM GEOMTEST T" );
	}

	@Override
	public NativeSQLStatement createNativeSridStatement() {
		return createNativeSQLStatement( "SELECT t.ID, t.GEOM.SDO_SRID FROM GEOMTEST t" );
	}

	@Override
	public NativeSQLStatement createNativeIsSimpleStatement() {
		return createNativeSQLStatement(
				"SELECT t.ID, MDSYS.OGC_ISSIMPLE(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)) FROM GEOMTEST t where MDSYS.OGC_ISSIMPLE(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)) = 1"
		);

	}

	@Override
	public NativeSQLStatement createNativeIsEmptyStatement() {
		return createNativeSQLStatement(
				"SELECT t.ID, MDSYS.OGC_ISEMPTY(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)) FROM GEOMTEST t"
		);
	}

	@Override
	public NativeSQLStatement createNativeIsNotEmptyStatement() {
		return createNativeSQLStatement(
				"SELECT t.ID, CASE MDSYS.OGC_ISEMPTY(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)) WHEN 0 THEN 1 ELSE 0 END FROM GEOMTEST t"
		);
	}

	@Override
	public NativeSQLStatement createNativeBoundaryStatement() {
		return createNativeSQLStatement(
				"SELECT t.ID, MDSYS.OGC_BOUNDARY(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)).GEOM FROM GEOMTEST t"
		);
	}

	@Override
	public NativeSQLStatement createNativeEnvelopeStatement() {
		return createNativeSQLStatement(
				"SELECT t.ID, MDSYS.OGC_ENVELOPE(MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM)).GEOM FROM GEOMTEST t"
		);
	}

	@Override
	public NativeSQLStatement createNativeAsBinaryStatement() {
		return createNativeSQLStatement( "select t.ID, t.GEOM.GET_WKB() FROM GEOMTEST T" );
	}

	@Override
	public NativeSQLStatement createNativeGeometryTypeStatement() {
		return createNativeSQLStatement(
				"select t.id, CASE t.geom.Get_GType() WHEN 1 THEN 'POINT' WHEN 2 THEN 'LINESTRING' WHEN 3 THEN 'POLYGON' WHEN 5 THEN 'MULTIPOINT' WHEN 6 THEN 'MULTILINE' WHEN 7 THEN 'MULTIPOLYGON' END from GEOMTEST t"
		);
	}

	@Override
	public NativeSQLStatement createNativeWithinStatement(Geometry testPolygon) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, mdsys.OGC_WITHIN( MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM), MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where mdsys.OGC_WITHIN( MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM), MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				testPolygon.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeEqualsStatement(Geometry testPolygon) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Equals(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Equals(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				testPolygon.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeCrossesStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Cross(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Cross(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeContainsStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Contains(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Contains(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeDisjointStatement(Geometry geom) {
		return createNativeSQLStatementAllWKTParams(
				"select t.id, MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Disjoint(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) from GEOMTEST T where MDSYS.ST_GEOMETRY.FROM_SDO_GEOM(t.GEOM).ST_Disjoint(MDSYS.ST_GEOMETRY.FROM_WKT(?, 4326)) = 1 and t.GEOM.SDO_SRID = 4326",
				geom.toText()
		);
	}

	@Override
	public NativeSQLStatement createNativeTransformStatement(int epsg) {
		return createNativeSQLStatement(
				"select t.id, MDSYS.SDO_CS.transform(t.geom," + epsg + ") from GeomTest t where t.geom.SDO_SRID = 4326"
		);
	}

	@Override
	public NativeSQLStatement createNativeHavingSRIDStatement(int srid) {
		return createNativeSQLStatement( "select t.id, 1 from GeomTest t where t.geom.SDO_SRID =  " + srid );
	}

	@Override
	protected Geometry decode(Object o) {
		return ( o != null ) ? JTS.to( decoder.convert( o ) ) : null;
	}
}
