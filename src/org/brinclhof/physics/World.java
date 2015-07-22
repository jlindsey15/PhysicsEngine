
package org.brinclhof.physics;

import java.util.ArrayList;

import org.magnos.MathUtils.Vec2;



public class World
{

	public static final double NEGLIGIBLE_AMOUNT = 0.0001;
	public static final double SINKAGE_AVOID_CONSTANT = 0.04;
	public static final double NEGLIGIBLE_INTERSECTION = 0.05;



	public static final double TIME_STEP = 1 / 600.0;
	public static final double NEGLIGIBLE_VELOCITY = 1.0;
	public static ArrayList<Entity> allTheEntities = new ArrayList<Entity>();
	public static double staticFriction = 0;
	public static double kineticFriction = 0;
	public static double restitution = 1;
	public static boolean universalGrav = false;
	public static double gravConst;


	public static void setConstants( double staticFric, double kineticFric, double rest ) {
		staticFriction = staticFric;
		kineticFriction = kineticFric;
		restitution = rest;
		universalGrav = false;
	}
	
	public static void setConstants( double staticFric, double kineticFric, double rest, double univGrav) {
		setConstants( staticFric, kineticFric, rest );
		universalGrav = true;
		gravConst = univGrav;
	}
	public static void reset() {
		allTheEntities.clear();
		Force.allTheForces.clear();
	}

	public static void add( Entity entity ) {
		if (universalGrav) {
			for ( Entity e : allTheEntities ) {
				Force.allTheForces.add( new AttractiveForce( e, entity, gravConst ) );
			}
		}
		allTheEntities.add( entity );

	}

	public static void doPhysics() {
		//Yes, this is THE least efficient way of detecting collisions.  But if you test the system
		//enough for that to matter, what are you doing with your life?  There's much better ways
		//to spend time than stacking virtual polygons on top of each other.
		for ( int i = 0; i < allTheEntities.size(); i++ ) {
			for (int j = 0; j < allTheEntities.size(); j++ ) {
				if ( j <= i ) continue;

				Collision collision = new Collision( allTheEntities.get( i ), allTheEntities.get( j ) );
				collision.detectCollisions();

				collision.respondToCollisions();
				collision.avoidSinkage();

			}
		}

		Force.exertAll();
		for (Entity entity : allTheEntities) {
			// move all the entities based on velocities
			move( entity, TIME_STEP );
		}



	}




	public static void move( Entity entity, double time ) {

		if (!entity.fixed) {

			entity.position.addi( entity.velocity.mul( time ) );
			entity.orientation += entity.angularVelocity * time;
			entity.matrix.set( entity.orientation );
		}

	}

	public static void addToRestitutions( double amount ) {
		for ( Entity entity : allTheEntities ) {
			entity.restitution += amount;
		}
		restitution += amount;
		if ( Math.abs( restitution ) < 0.00001 ) restitution = 0;
	}
	
	public static void addToKineticCoefficients( double amount ) {
		for ( Entity entity : allTheEntities ) {
			entity.kineticFriction += amount;
		}
		kineticFriction += amount;
		if ( Math.abs( kineticFriction ) < 0.00001 ) kineticFriction = 0;

	}
	
	public static void addToStaticCoefficients( double amount ) {
		for ( Entity entity : allTheEntities ) {
			entity.staticFriction += amount;
		}
		staticFriction += amount;
		if ( Math.abs( kineticFriction ) < 0.00001 ) kineticFriction = 0;

	}




}
