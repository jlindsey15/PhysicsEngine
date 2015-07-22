package org.brinclhof.physics;

import java.util.ArrayList;

import processing.core.PApplet;


public abstract class Force
{
	public static ArrayList<Force> allTheForces = new ArrayList<Force>();
	public abstract void exert();
	public static void exertAll() {
		ArrayList<Force> toBeRemoved = new ArrayList<Force>();
		for ( Force force : Force.allTheForces ) {
			force.exert();
			if ( force instanceof NormalForce || force instanceof FrictionForce ) {
				
				toBeRemoved.add( force );
			}
		}
		for ( Entity entity : World.allTheEntities ) {
			accelerate( entity );
		}
		for ( Force force : toBeRemoved ) {
			allTheForces.remove( force );
		}
		
	}
	
	public static void accelerate( Entity entity ) {
		if ( entity.fixed ) { 
			return;
		}
		entity.velocity.addi( entity.netForce.mul( World.TIME_STEP / entity.mass ) );
		entity.angularVelocity += entity.torque * (World.TIME_STEP / entity.inertia);
		entity.netForce.set( 0, 0 );
		entity.torque = 0;
	}
	
	public abstract void draw( PApplet applet );
	
}
