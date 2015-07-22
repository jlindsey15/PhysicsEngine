package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;

import processing.core.PApplet;


public class FrictionForce extends Force
{

	Entity firstEntity;
	Entity secondEntity;
	Collision collision;
	NormalForce normalForce;
	public FrictionForce( NormalForce theNormalForce, Collision theCollision) {
		collision = theCollision;
		firstEntity = theNormalForce.firstEntity;
		secondEntity = theNormalForce.secondEntity;
		normalForce = theNormalForce;
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
					if ( !normalForce.colliding ) {
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
					
					

					//***************************************************************************
					// now for Friction... more math!  More inspiration from Randy Gaul's website:
					//http://gamedevelopment.tutsplus.com/tutorials/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756

					// get tangent vector - direction of friction force
					Vec2 tangentVector = new Vec2( relativeVelocity );
					tangentVector.addi( collision.normalVector.mul( -contactVel )  );
					tangentVector.normalize();

					// get possible friction mag if it was a static friction case
					double possibleFrictionImpulseMag = - Vec2.dot( relativeVelocity, tangentVector )/ (totalMassInertiaRecip * collision.numIntersectionPoints);
					//System.out.println(possibleFrictionImpulseMag);
					// is it static friction or kinetic?
					Vec2 frictionImpulse;
					
					// 
					if (Math.abs( possibleFrictionImpulseMag ) < normalForce.impulseMag * collision.staticFriction)
					{
						//static
						frictionImpulse = tangentVector.mul( possibleFrictionImpulseMag );
					}
					else
					{
						//kinetic
						frictionImpulse = tangentVector.mul( normalForce.impulseMag ).muli( -collision.kineticFriction );
					}
					
			//actually apply the friction impulse
			if (!firstEntity.fixed) {
				firstEntity.torque +=  Vec2.cross( radius1, frictionImpulse.neg() ) / World.TIME_STEP;
				firstEntity.netForce.addsi( frictionImpulse.neg(), 1 / World.TIME_STEP);
			}
			if (!secondEntity.fixed) {
				secondEntity.torque +=  Vec2.cross( radius2, frictionImpulse ) / World.TIME_STEP;
				secondEntity.netForce.addsi( frictionImpulse, 1 / World.TIME_STEP);
				

			}
			
		
			
		}
	}
	@Override
	public void draw( PApplet applet )
	{
		// TODO Auto-generated method stub
		
	}

}
