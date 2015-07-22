

package org.brinclhof.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;

public class PolygonRegion extends Entity
{

	public static final int MAX_VERTICES = 40;

	public int numVertices;
	public Vec2[] vertices;
	public Vec2[] normals;



	public PolygonRegion( Vec2 pos, Vec2 vel, Vec2 theForce, double angVel, double torq, double ori,
		double statFr, double kinFr, double rest, Vec2 ... verts) {
		super( pos, vel, theForce, angVel, torq, ori, statFr, kinFr, rest);
		vertices = new Vec2[MAX_VERTICES];
		for (int i = 0; i < MAX_VERTICES; i++ ) {
			vertices[i] = new Vec2();
		}
		normals = new Vec2[MAX_VERTICES];
		for (int i = 0; i < MAX_VERTICES; i++ ) {
			normals[i] = new Vec2();
		}
		buildHull( verts );
		double area = 0;
		double inertiaPart1 = 0;
		double inertiaPart1Denom = 0;
		double inertiaPart1Num = 0;
		double comX = 0, comY = 0;
		for ( int n = 0; n < numVertices; n++ ) {
			int nPlus1 = (n+1 < numVertices) ? n+1 : 0;
			inertiaPart1Denom += Math.abs( Vec2.cross( vertices[nPlus1], vertices[n] ) );
			double temp = 0;
			temp += Vec2.dot( vertices[nPlus1], vertices[nPlus1] );
			temp += Vec2.dot( vertices[nPlus1], vertices[n] );
			temp += Vec2.dot( vertices[n], vertices[n] );
			temp *= Math.abs( Vec2.cross( vertices[nPlus1], vertices[n] ) );
			inertiaPart1Num += temp;
			area += vertices[n].x * vertices[nPlus1].y / 2;
			area -= vertices[n].y * vertices[nPlus1].x / 2;
			comX += (vertices[n].x + vertices[nPlus1].x) * (vertices[n].x * vertices[nPlus1].y - vertices[nPlus1].x * vertices[nPlus1].y);
			comX += (vertices[n].y + vertices[nPlus1].y) * (vertices[n].x * vertices[nPlus1].y - vertices[nPlus1].x * vertices[nPlus1].y);

		}


		inertiaPart1 = inertiaPart1Num / inertiaPart1Denom;

		mass = DEFAULT_DENSITY * area;
		double inertiaPart2 = mass / 6.0;
		inertia =  inertiaPart1 * inertiaPart2;
	}

	public PolygonRegion( Vec2 pos, Vec2 ... verts ) {
		this(pos, new Vec2( 0, 0 ), new Vec2(0, 0), 0, 0, 
			0, World.staticFriction, World.kineticFriction, World.restitution, verts);
	}
	
	public PolygonRegion ( Vec2 pos ) {
		

		this( pos, randomVerts() );
		
		orientation = ( 2 * Math.PI * Math.random() - Math.PI );
	}
	
	public static Vec2[] randomVerts( int vertCount ) {
		double size = 40 * Math.random() + 10;

		Vec2[] verts = Vec2.arrayOf( vertCount );
		for (int i = 0; i < vertCount; i++) {
			verts[i].set( 2 * size * Math.random() - size, 2 * size * Math.random() - size );
		}
		return verts;
	}
	
	public static Vec2[] randomVerts( ) {
		return randomVerts( (int)( 20 * Math.random() ) + 3);
	}
	
	public void draw( PApplet applet ) {
		

		applet.fill(0);
		applet.beginShape();
		// etc;
		for (int i = 0; i < numVertices; i++)
		{
			Vec2 v = new Vec2( vertices[i] );
			matrix.muli( v );
			v.addi( position );
			applet.vertex( (float)v.x, (float)v.y );
		}
		applet.endShape();
		applet.noFill();
	}


/*the following code is not my own (I did make a few minor changes in order to integrate it into the program).
	Here's the link to the code, and a copy of the attached license:
	https://github.com/tillnagel/unfolding/blob/master/examples/de/fhpotsdam/unfolding/examples/overviewdetail/connection/ConvexHull.java
	
	Unfolding - Map Library

Copyright (C) 2013 Till Nagel, and contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.*/
	protected void buildHull(Vec2[] swag) {
		List<Vec2> points = Arrays.asList( swag );
		if (points.size() < 3)
			return;

		// Find the point with the least y, then x coordinate
		Vec2 p0 = null;
		for (int i = 0; i < points.size(); ++i) {
			Vec2 curr = points.get(i);
			if (p0 == null || curr.y < p0.y || (curr.y == p0.y && curr.x < p0.x))
				p0 = curr;
		}

		// Sort the points by angle around p0
		class PointAngleComparator implements Comparator<Vec2> {
			private Vec2 p0;

			public PointAngleComparator(Vec2 p0) {
				this.p0 = p0;
			}

			private double angle(Vec2 pt) {
				return Math.atan2(pt.y - p0.y, pt.x - p0.x);
			}

			public int compare(Vec2 p1, Vec2 p2) {
				double a1 = angle(p1), a2 = angle(p2);
				if (a1 > a2)
					return 1;
				if (a1 < a2)
					return -1;
				return Double.compare(Vec2.distance( new Vec2( p0.x, p0.y), new Vec2( p1.x, p1.y) ),
					Vec2.distance( new Vec2( p0.x, p0.y), new Vec2( p2.x, p2.y ) ) );
			}
		}
		Collections.sort(points, new PointAngleComparator(p0));

		// build the hull
		Stack<Vec2> hull = new Stack<Vec2>();
		hull.push(points.get(0));
		hull.push(points.get(1));
		hull.add(points.get(2));
		for (int i = 3; i < points.size(); ++i) {
			Vec2 cur = points.get(i);
			while (hull.size() >= 3) {
				Vec2 snd = hull.get(hull.size() - 2);
				Vec2 top = hull.peek();
				double crossproduct = (top.x - snd.x) * (cur.y - snd.y) - (cur.x - snd.x)
					* (top.y - snd.y);
				if (crossproduct > 0)
					break;
				hull.pop();
			}
			hull.push(cur);
		}

		numVertices = hull.size();
		for ( int i = 0; i < numVertices; i++ ) {
			vertices[i] = hull.get( i );
		}
		for (int i = 0; i < numVertices; i++) {
			Vec2 face = vertices[(i + 1) % numVertices].sub( vertices[i] );

			normals[i].set( face.perpendicularOf() );
			normals[i].normalize();
		}
	}



	Vec2 farthestVertexInDirection( Vec2 direction ) {
		//so that it will definitely be replaced
		double bestProjection = -Double.MAX_VALUE;
		Vec2 bestVertex = new Vec2();

		for(int i = 0; i < numVertices; i++) {

			double projection = Vec2.dot( direction, vertices[i] );

			if(projection > bestProjection) {
				bestVertex = vertices[i];
				bestProjection = projection;
			}
		}

		return bestVertex;
	}

}
