package org.brinclhof.physics;
import org.magnos.MathUtils.*;

import processing.core.PApplet;


public class SpringForce extends Force
{
	public Entity firstEntity;
	public Entity secondEntity;

	double damping;
	double naturalLength;
	double springConstant;


	public SpringForce( Entity firstEnd, Entity secondEnd, double theSpringConstant, double theDamping, double theNaturalLength)
	{
		springConstant = theSpringConstant;
		damping = theDamping;
		naturalLength = theNaturalLength;
		firstEntity = firstEnd;
		secondEntity = secondEnd;
	}



	public final void exert()
	{	

		Vec2 displacement = new Vec2( firstEntity.position.x - secondEntity.position.x, 
			firstEntity.position.y - secondEntity.position.y );
		Vec2 direction = new Vec2( displacement );
		direction.normalize();

		double distance = displacement.length();


		double springForce = springConstant * ( naturalLength - distance ); 



		Vec2 relativeVelocity = new Vec2( firstEntity.velocity.x - secondEntity.velocity.x,
			firstEntity.velocity.y - secondEntity.velocity.y );

		double dampingForce = -damping * Vec2.dot( displacement, relativeVelocity );



		double magnitude = springForce + dampingForce;


		Vec2 totalForce = direction.mul( magnitude );

		firstEntity.netForce.addi( totalForce );

		secondEntity.netForce.addi( totalForce.neg() );

	}



	@Override
	public void draw( PApplet applet ) {

		applet.line((float)firstEntity.position.x, (float)firstEntity.position.y, (float)secondEntity.position.x, (float)secondEntity.position.y );

	}
}