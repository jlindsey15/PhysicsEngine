package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;


public class Circle extends Entity {
	public double radius;

	
	public Circle(Vec2 pos, Vec2 vel, Vec2 theForce, double angVel, double torq, double ori,
		double statFr, double kinFr, double rest, double theRadius) {
		super( pos, vel, theForce, angVel, torq, ori, statFr, kinFr, rest);
		radius = theRadius;
		mass = 2 * Math.PI * radius * DEFAULT_DENSITY;
		inertia = mass * radius * radius;
	}
	
	public Circle( Vec2 pos, double theRadius ) {
		this(pos, new Vec2( 0, 0 ), new Vec2(0, 0), 0, 0, 
			(double)(( 2*Math.PI ) * Math.random() + -Math.PI ), 0.4, 0.3, 0.5, theRadius);
	}
	
	public void draw( PApplet applet ) {
		
	}

}
