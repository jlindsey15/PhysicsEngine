
package org.brinclhof.physics;

import org.magnos.MathUtils.Mat2;
import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;

public abstract class Entity
{

	public double mass;
	public boolean fixed;
	public double inertia;
	public double staticFriction;
	public double kineticFriction;
	public double restitution;
	public Vec2 position = new Vec2();
	public Vec2 velocity = new Vec2();
	public Vec2 netForce = new Vec2();
	public double angularVelocity;
	public double torque;
	public double orientation;
	public Mat2 matrix = new Mat2();
	public static final double DEFAULT_DENSITY = 1.0;
	

	
	public Entity( Vec2 pos, Vec2 vel, Vec2 theForce, double angVel, double torq, double ori,
		double statFr, double kinFr, double rest) {
		position = pos;
		velocity = vel;
		netForce = theForce;
		angularVelocity = angVel;
		torque = torq;
		orientation = ori;
		staticFriction = statFr;
		kineticFriction = kinFr;
		restitution = rest;
		World.add( this );
	}
	
	

	public void exertForce( Vec2 impulseVector, Vec2 radius ) {
		if (!fixed) {
		angularVelocity +=  Vec2.cross( radius, impulseVector ) / inertia;
		velocity.addsi( impulseVector, 1 / mass);
		
		}
	}
	
	public void fixInPlace() {
		fixed = true;
		
	}
	
	public abstract void draw( PApplet applet );
	
	//public abstract void addRandom();

	


}
