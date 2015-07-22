
package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;

public class Disk extends Entity {
	public double radius;


	public Disk(Vec2 pos, Vec2 vel, Vec2 theForce, double angVel, double torq, double ori,
		double statFr, double kinFr, double rest, double theRadius) {
		super( pos, vel, theForce, angVel, torq, ori, statFr, kinFr, rest);
		radius = theRadius;
		mass = Math.PI * radius * radius * DEFAULT_DENSITY;
		inertia = 0.5 * mass * radius * radius;
	}

	public Disk( Vec2 pos, double theRadius ) {
		this(pos, new Vec2( 0, 0 ), new Vec2(0, 0), 0, 0, 
			(double)(( 2*Math.PI ) * Math.random() + -Math.PI ), World.staticFriction, World.kineticFriction, World.restitution, theRadius);
	}

	public void draw( PApplet applet ) {



		applet.fill(153);
		applet.ellipse((float)(position.x), (float)(position.y), (float)radius * 2, (float)radius * 2 );
		double radiusX = (double)Math.cos( orientation ) * radius;
		double radiusY = (double)Math.sin( orientation ) * radius;
		applet.line( (float)( position.x + radiusX ), (float)( position.y + radiusY ), (float)( position.x - radiusX ), (float)(position.y - radiusY ) );
		//applet.line( (float)( position.x - radiusY ), (float)( position.y + radiusX ), (float)( position.x + radiusY ), (float)(position.y - radiusX ) );

		applet.noFill();
	}


	public Disk ( Vec2 pos ) {
		this( new Vec2( pos.x , pos.y ), ( 40 * Math.random() + 10 ) );

	}





}
