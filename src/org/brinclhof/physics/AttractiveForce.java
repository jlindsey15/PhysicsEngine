package org.brinclhof.physics;
import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;



public class AttractiveForce extends Force
{
	Entity firstEntity;
	Entity secondEntity;
	double attractiveConstant;

	public AttractiveForce( Entity theFirst, Entity theSecond, double theConstant) {
		this.firstEntity = theFirst;
		this.secondEntity = theSecond;
		this.attractiveConstant = theConstant;
	}


	public void exert() {

		Vec2 vectorBetween = new Vec2( firstEntity.position.x - secondEntity.position.x,
			firstEntity.position.y - secondEntity.position.y );

		double force = attractiveConstant * firstEntity.mass * secondEntity.mass / vectorBetween.lengthSq();

		Vec2 forceBetween = new Vec2( vectorBetween );
	
		forceBetween.normalize();
		forceBetween.muli(force);
		
		
		
		firstEntity.netForce.addi( forceBetween.neg() );

		secondEntity.netForce.addi( forceBetween );
	}


	@Override
	public void draw( PApplet applet )
	{
		// TODO Auto-generated method stub
		
	}



}
