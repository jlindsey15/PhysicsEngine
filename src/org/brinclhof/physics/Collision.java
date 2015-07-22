
package org.brinclhof.physics;

import org.magnos.MathUtils.Mat2;
import org.magnos.MathUtils.Vec2;


//I wouldn't have been able to do all of this Collision handling stuff without Randy Gaul's
//"Oriented Rigid Bodies" tutorial.  It's worth reading if you want to understand what's going on
//with some of this math - at least until I comment this better...
//http://gamedevelopment.tutsplus.com/tutorials/custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032
//

public class Collision {

	public Entity entity1, entity2;
	public double intersectionDepth;
	public Vec2 normalVector;
	public final Vec2[] intersectionPoints;
	public int numIntersectionPoints;
	public double restitution, kineticFriction, staticFriction;
	public int intersectedFaceIndex;
	public int diskPolyFaceIndex;
	public PolygonRegion intersected, intersecting;
	public double minDepth = Double.MAX_VALUE;

	public Collision( Entity first, Entity second ) {
		entity1 = first;
		entity2 = second;
		intersectionPoints = new Vec2[2];
		intersectionPoints[0] = new Vec2();
		intersectionPoints[1] = new Vec2();
		normalVector = new Vec2();
		//averages!!!
		staticFriction =  ( entity1.staticFriction + entity2.staticFriction ) / 2;
		kineticFriction =  ( entity1.kineticFriction + entity2.kineticFriction ) / 2;
		restitution =  ( entity1.restitution + entity2.restitution ) / 2;



		for (int i = 0; i < numIntersectionPoints; i++) {

			// find relative velocity, TAKING INTO ACCOUNT ROTATION
			Vec2 relativeVelocity = getRelativeVelocity( entity1, entity2, i );

			if (relativeVelocity.lengthSq() < World.NEGLIGIBLE_VELOCITY) {
				restitution = 0; //hack way of making things not jittery
			}
		}
	}



	public void detectCollisions() {
		numIntersectionPoints = 0;

		if ( entity1 instanceof PolygonRegion && entity2 instanceof Disk ) {
			Entity temp = entity1;
			entity1 = entity2;
			entity2 = temp;
		}

		if ( entity1 instanceof Disk && entity2 instanceof Disk ) {
			// Circles are nice!!!

			Disk first = (Disk)entity1;
			Disk second = (Disk)entity2;
			normalVector = second.position.sub( first.position );

			double distance = normalVector.length();
			double totalRadius = first.radius + second.radius;

			// No collision... also, the errors when this isn't here are so annoyingly subtle...
			if ( distance >= totalRadius ) return;

			numIntersectionPoints = 1;

			intersectionDepth = totalRadius - distance;
			normalVector.normalize();

			//get the intersection point
			intersectionPoints[0].set( normalVector );
			intersectionPoints[0].muli( first.radius );
			intersectionPoints[0].addi( first.position );
		}
		else if ( ( entity1 instanceof Disk && entity2 instanceof PolygonRegion ) ) {
			Disk disk = (Disk)entity1;
			PolygonRegion polyRegion = (PolygonRegion)entity2;


			Vec2 diskPosPolyCoords = toLocalAxes( polyRegion, disk.position.sub( polyRegion.position ) );
			setPolyDiskDepth( disk, polyRegion, diskPosPolyCoords );
			Vec2 normal = polyRegion.normals[diskPolyFaceIndex];

			//sets face using the given index
			Vec2 temp1 = polyRegion.vertices[diskPolyFaceIndex];
			//next index, loop if needed
			int swag = diskPolyFaceIndex + 1;
			if ( swag >= polyRegion.numVertices ) {
				swag = 0;
			}
			Vec2 temp2 = polyRegion.vertices[swag];
			Face face = new Face( temp1, temp2 );
			

			setPolyDiskIntersection( disk, polyRegion, diskPosPolyCoords, normal, face );




		}
		else if ( entity1 instanceof PolygonRegion && entity2 instanceof PolygonRegion ) {
			PolygonRegion first = (PolygonRegion)entity1;
			PolygonRegion second = (PolygonRegion)entity2;

			//this stuff determines which polygon is the "intersector" and which is the "intersected"

			double intersection2into1 = getMinimumIntersectionDepth( first, second);
			double intersection1into2 = getMinimumIntersectionDepth( second, first);
			if (intersection2into1 <= 0) {
				return;
			}
			//sets the vertices of the intersected face
			Face intersectedFace = makeGlobalFace( intersectedFaceIndex, intersected );
			//craete an empty array that will eventually contain the vertices of the intersecting face
			Vec2 intersectedNormal = intersected.normals[intersectedFaceIndex];
			//This takes you from intersected's coordinates to intersector's coordinates
			intersectedNormal = intersected.matrix.mul( intersectedNormal ); 
			Face intersectingFace = findIntersectingFace( intersecting, intersectedNormal );
			setPolygonIntersection( intersectedFace, intersectedNormal, intersectingFace );
			normalVector.set( intersectedNormal );
			if (intersection1into2 <= intersection2into1){
				normalVector.negi();
			}

		}

	}



	private void setPolyDiskDepth( Disk disk, PolygonRegion polyRegion, Vec2 diskPosPolyCoords )
	{
		double distance = -Double.MAX_VALUE;
		for ( int i = 0; i < polyRegion.numVertices; i++ ) {
			Vec2 vertexToDisk = diskPosPolyCoords.sub( polyRegion.vertices[i] );
			double tempDist = Vec2.dot( vertexToDisk, polyRegion.normals[i] );

			if (tempDist > distance) {
				distance = tempDist;
				diskPolyFaceIndex = i;
			}
		}
		if (distance > disk.radius) {
			return;
		}
		intersectionDepth = disk.radius - distance;
	}



	private void setPolyDiskIntersection( Disk disk, PolygonRegion polyRegion, Vec2 diskPosPolyCoords, Vec2 normal, Face face )
	{
		Vec2 vertex1ToDisk = diskPosPolyCoords.sub( face.p1 );
		Vec2 vertex2ToDisk = diskPosPolyCoords.sub( face.p2 );

		if (Vec2.dot(face.vecBetween(),  vertex1ToDisk ) <= 0) {
			//no collision...
			if ( vertex1ToDisk.length() > disk.radius ) {
				return;
			}

			setPolyDiskEdgeIntersection( polyRegion, diskPosPolyCoords, face.p1 );
		}

		else if (Vec2.dot( face.vecBetween().neg(), vertex2ToDisk ) <= 0) {
			//no collision...
			if ( vertex2ToDisk.length() > disk.radius ) {
				return;
			}

			setPolyDiskEdgeIntersection( polyRegion, diskPosPolyCoords, face.p2 );
		}

		else {
			//no collision...
			if (Vec2.dot( normal, vertex1ToDisk ) > disk.radius) {
				return;
			}

			normalVector = toGlobalAxes( polyRegion, normal );
			normalVector.negi();
			intersectionPoints[0].set( disk.position );
			intersectionPoints[0].addi( normalVector.mul( disk.radius ) );
		}
		numIntersectionPoints = 1;

	}



	private void setPolyDiskEdgeIntersection( PolygonRegion polyRegion, Vec2 diskPosPolyCoords, Vec2 point )
	{
		normalVector.set( point.sub( diskPosPolyCoords ) );
		normalVector = toGlobalAxes( polyRegion, normalVector );
		normalVector.normalize();
		intersectionPoints[0] = toGlobalCoords( polyRegion, point );
	}

	private Face findIntersectingFace( PolygonRegion intersecting, Vec2 intersectedNormal )
	{
		Vec2 intersectedNormalintersectingCoordinates = toLocalAxes( intersecting, intersectedNormal ); 

		double minsoFar = Double.MAX_VALUE;
		//This gets you  the intersecting face - basically the guy who's the most "opposite" the intersected face
		int intersectingFaceIndex = 0;

		for(int i = 0; i < intersecting.numVertices; ++i)
		{
			double temp = Vec2.dot( intersectedNormalintersectingCoordinates, intersecting.normals[i] );
			if(temp < minsoFar)
			{
				minsoFar = temp;
				intersectingFaceIndex = i;
			}
		}


		//Sets the vertices of the intersecting face based on which one was most perpendicular-y
		Face intersectingFace = makeGlobalFace( intersectingFaceIndex, intersecting );
		return intersectingFace;
	}



	



	private void setPolygonIntersection( Face intersectedFace, Vec2 intersectedNormal, Face intersectingFace )
	{
		//if the vertex of the intersected face is on a line perp to its normal (which it should be),
		//then its distance from the origin should be this value based on ax+by+c = 0


		numIntersectionPoints = 0;

		double depth = -Vec2.dot( intersectedNormal, intersectingFace.p1.sub( intersectedFace.p1 ) );
		intersectionDepth += depth;
		if ( depth > 0 ) {
			intersectionPoints[numIntersectionPoints].set( intersectingFace.p1 );
			numIntersectionPoints++;
		}
		depth = -Vec2.dot( intersectedNormal, intersectingFace.p2.sub( intersectedFace.p2 ) );
		intersectionDepth += depth;
		if ( depth > 0 ) {
			intersectionPoints[numIntersectionPoints].set( intersectingFace.p2 );
			numIntersectionPoints++;
		}

		//You want the average depth per contact
		if (numIntersectionPoints > 0) intersectionDepth /= numIntersectionPoints;
	}






	public  Vec2 getRelativeVelocity ( Entity entity1, Entity entity2, int intersectionIndex ) {
		Vec2 radius1 = intersectionPoints[intersectionIndex].sub( entity1.position );
		Vec2 radius2 = intersectionPoints[intersectionIndex].sub( entity2.position );
		Vec2 relativeVelocity = new Vec2( entity2.velocity );
		relativeVelocity.addi( Vec2.cross( entity2.angularVelocity, radius2, new Vec2() ) );
		relativeVelocity.subi( entity1.velocity );
		relativeVelocity.subi( Vec2.cross( entity1.angularVelocity, radius1, new Vec2() ) );
		return relativeVelocity;
	}




	public void respondToCollisions() {


		//Upcoming: lots of math that is worth understanding, but not worth explaining here.  hmu for details

		for (int i = 0; i < numIntersectionPoints; i++) {

			NormalForce swag = ( new NormalForce( entity1, entity2, this ) ) ;
			swag.exert();
			Force.accelerate( entity1 );
			Force.accelerate( entity2 );

			FrictionForce swagger = new FrictionForce( swag, this )  ;
			swagger.exert();
			Force.accelerate( entity1 );
			Force.accelerate( entity2 );


		}
	}


	public double getMinimumIntersectionDepth( PolygonRegion intersectedCand, PolygonRegion intersectingCand )
	{

		int index = -1;
		double minSoFar = Double.MAX_VALUE;
		for ( int i = 0; i < intersectedCand.numVertices; i++ ) {

			Vec2 normal = toLocalAxes( intersectingCand, toGlobalAxes( intersectedCand, intersectedCand.normals[i] ) );
			Vec2 candidateVertex = intersectingCand.farthestVertexInDirection( normal.neg() );
			Vec2 intersectedVertex = toLocalCoords( intersectingCand, toGlobalCoords( intersectedCand, intersectedCand.vertices[i] ) );
			double distance = -Vec2.dot( normal, candidateVertex.sub( intersectedVertex ) );
			if (distance < minSoFar) {
				minSoFar = distance;
				index = i;
			}
		}

		if (minSoFar < minDepth)  {
			minDepth = minSoFar;
			intersected = intersectedCand;
			intersecting = intersectingCand;
			intersectedFaceIndex = index;

		}
		return minSoFar;
	}







	public void avoidSinkage() {
		// avoids sinking due to floating point errors by making sure objects don't stay fallen into each other


		//this avoids constant jittering
		if (intersectionDepth <= World.NEGLIGIBLE_INTERSECTION) return;

		double change = intersectionDepth / (1/entity1.mass + 1/entity2.mass) * World.SINKAGE_AVOID_CONSTANT;


		if (!entity1.fixed) entity1.position.addsi( normalVector, -1/entity1.mass * change );
		if (!entity2.fixed) entity2.position.addsi( normalVector, 1/entity2.mass * change );
	}
	
	private Vec2 toLocalAxes( Entity entity, Vec2 direction )
	{
		Vec2 returned = entity.matrix.transpose( ).mul( direction );
		return returned;
	}



	private Face makeGlobalFace( int faceIndex, PolygonRegion polygonRegion )
	{
		Vec2 temp1 = polygonRegion.vertices[faceIndex];
		//increase index by one, loop around if necessary
		faceIndex = faceIndex + 1;
		if (faceIndex == polygonRegion.numVertices) {
			faceIndex = 0;
		}
		Vec2 temp2 = polygonRegion.vertices[faceIndex];
		Face intersectedFace = new Face( temp1, temp2 );
		// get this jount in some global coordinates
		intersectedFace.p1 = toGlobalCoords( polygonRegion, intersectedFace.p1 );
		intersectedFace.p2 = toGlobalCoords( polygonRegion, intersectedFace.p2 );
		return intersectedFace;
	}

	private Vec2 toGlobalAxes( Entity entity, Vec2 point )
	{
		Vec2 transformed = entity.matrix.mul( point );
		return transformed;
	}
	private Vec2 toGlobalCoords( Entity entity, Vec2 point )
	{
		Vec2 transformed = toGlobalAxes( entity, point );
		transformed.addi( entity.position );
		return transformed;
	}

	private Vec2 toLocalCoords( Entity entity, Vec2 point )
	{
		Vec2 transformed = toLocalAxes ( entity, point );
		transformed.subi( toLocalAxes( entity, entity.position ) );
		return transformed;
	}
}




