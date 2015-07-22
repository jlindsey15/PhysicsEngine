package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;


public class OmniGravityForce extends Force
{

	public static Vec2 GRAVITY;
	public void exert() {
		for ( Entity entity : World.allTheEntities ) {
			entity.netForce.addi( GRAVITY.mul( entity.mass ) );
		}
	}
	
	public OmniGravityForce( double g ) {
		GRAVITY = new Vec2 ( 0, g );
	}
	public OmniGravityForce( ) {
		GRAVITY = new Vec2 ( 0, 200 );
	}
	@Override
	public void draw( PApplet applet )
	{
		// TODO Auto-generated method stub
		
	}

}
