package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;


public class NormalForce extends Force
{

	Entity firstEntity;
	Entity secondEntity;
	Collision collision;
	boolean colliding = true;
	double impulseMag;
	public NormalForce( Entity theFirst, Entity theSecond, Collision theCollision) {
		collision = theCollision;
		firstEntity = theFirst;
		secondEntity = theSecond;
	}
	public void exert() {
		for (int i = 0; i < collision.numIntersectionPoints; i++) {
			// find relative velocity, TAKING INTO ACCOUNT ROTATION
			Vec2 relativeVelocity = collision.getRelativeVelocity( firstEntity, secondEntity, i );

			// a bunch of math - gets us a number that decreases as total mass/inertia stuff decreases
			//Equation 6 of Gaul's tutorial: 
			//http://gamedevelopment.tutsplus.com/tutorials/custom-2d-physics-engine-oriented-rigid-bodies--gamedev-8032
			Vec2 radius1 = collision.intersectionPoints[i].sub( firstEntity.position );
			Vec2 radius2 = collision.intersectionPoints[i].sub( secondEntity.position );
			// find the relative velocity along the normal vector
			double contactVel = Vec2.dot( relativeVelocity, collision.normalVector );

			// don't do the collision if the objects are moving away
			// this prevents sticking and objects eating each other
			if (contactVel > 0) {
				colliding = false;
				return;
			}

			//Huge equation!!!
			double firstMassRecip, firstInertiaRecip, secondMassRecip, secondInertiaRecip;
			if ( firstEntity.fixed ) {
				firstMassRecip = 0;
				firstInertiaRecip = 0;
			}
			else {
				firstMassRecip = 1 / firstEntity.mass;
				firstInertiaRecip = 1 / firstEntity.inertia;
			}
			if ( secondEntity.fixed ) {
				secondMassRecip = 0;
				secondInertiaRecip = 0;
			}
			else {
				secondMassRecip = 1 / secondEntity.mass;
				secondInertiaRecip = 1 / secondEntity.inertia;
			}
			double totalMassInertiaRecip = firstMassRecip + secondMassRecip + ( Vec2.cross( radius1, collision.normalVector ) * Vec2.cross( radius1, collision.normalVector ) * firstInertiaRecip ) + ( Vec2.cross( radius2, collision.normalVector ) * Vec2.cross( radius2, collision.normalVector ) * secondInertiaRecip  );

			//calculate the impulse vector
			impulseMag = -(1.0f + collision.restitution) * contactVel / (totalMassInertiaRecip * collision.numIntersectionPoints);
			Vec2 impulse = collision.normalVector.mul( impulseMag );

			//actually apply the impulses

			if ( !firstEntity.fixed ) {
				firstEntity.torque +=  Vec2.cross( radius1, impulse.neg() ) / World.TIME_STEP;
				firstEntity.netForce.addsi( impulse.neg(), 1 / World.TIME_STEP);


			}
			if ( !secondEntity.fixed ) {
				secondEntity.torque +=  Vec2.cross( radius2, impulse ) / World.TIME_STEP;
				secondEntity.netForce.addsi( impulse, 1 / World.TIME_STEP);


			}



		}
	}
	@Override
	public void draw( PApplet applet )
	{
		// TODO Auto-generated method stub

	}

}
