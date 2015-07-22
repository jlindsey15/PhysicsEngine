package org.brinclhof.physics;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import org.magnos.MathUtils.Vec2;



import processing.core.PApplet;
import processing.core.PFont;


public class Demo extends PApplet
{

	public World world;
	public boolean playing;
	public double accumulator;
	public double millis;
	public double clockStart;
	public  double demo;
	public final int screenX = 1000, screenY = 1000;
	public char changingMode = 'r';
	public boolean constructingPoly = false;
	public boolean constructingCirc = false;
	public ArrayList<Vec2> polyVerts = new ArrayList<Vec2>();
	public Vec2 circleCenter = new Vec2();
	public double circleRadius;
	String demoText = "";
	
	

	public void setup()
	{
	
		
		
		world = new World();
		background(255);
		size( screenX, screenY );
		smooth();
		fill( 0 );
		millis = 0;
		clockStart = System.currentTimeMillis();
		frameRate(600);
		accumulator = 0;
		playing = true;

		demo1();
		
		
		
	}
	
	
	
	
	
	public void demo1() {
		demoText = "Demo 1:\n Vanilla world with downward gravity";
		
		World.setConstants( 0.4, 0.4, 0.3 );

		Vec2[] verts = new Vec2[4];
		verts[0] = new Vec2 ( 400, 100 );
		verts[1] = new Vec2 ( -400, 100 );
		verts[2] = new Vec2 ( 400, -100 );
		verts[3] = new Vec2 ( -400, -100 );

		PolygonRegion p =  new PolygonRegion( new Vec2 ( 240, 600 ), verts );
		p.fixInPlace();
		p.orientation = 0;
		p.matrix.set( p.orientation );

		

		//Force.allTheForces.add( new AttractiveForce(d, c, 150) );
		//Force.allTheForces.add(new SpringForce(d, c, 500, 100, 10) );
		Force.allTheForces.add( new OmniGravityForce() );
	}
	
	public void demo2() {
		demoText = "Demo 2:\nUniversal Gravitation";

		World.setConstants( 0.3, 0.2, 1, 150 );
		Disk d = ( new Disk( new Vec2(300, 200) , 40.0 ) );
		d.velocity.set( new Vec2(0, 20) );



		Disk c =  new Disk(new Vec2(200, 280), 20) ;
		//c.staticFriction = 10000000;
		c.velocity.set(new Vec2(0, -80));

		
		
	}
	
	public void demo3() {
		demoText = "Demo 3:\nElastic collisions";
		World.setConstants( 0, 0, 1 );
		for ( int i = 0; i < 10; i++ ) {
			Vec2 pos = new Vec2( Math.random() * screenX * 0.7, Math.random() * screenY * 0.7 );
			PolygonRegion p = new PolygonRegion( pos );
			p.velocity.set( Math.random() * 500 - 50, Math.random() * 100 - 50);
			//p.angularVelocity = Math.random() * 32 - 4;
			
		}
		Vec2[] verts = new Vec2[4];
		verts[0] = new Vec2 ( screenX * 0.7, 10 );
		verts[1] = new Vec2 ( -screenX, 10 );
		verts[2] = new Vec2 ( screenX * 0.7, -10 );
		verts[3] = new Vec2 ( -screenX, -10 );
		PolygonRegion boxTop = new PolygonRegion( new Vec2( 0, 0 ), verts);
		boxTop.orientation = 0;
		boxTop.matrix.set( boxTop.orientation );
		boxTop.fixInPlace();
		PolygonRegion boxBottom = new PolygonRegion( new Vec2( 0, screenY * 0.7 ), verts);
		boxBottom.fixInPlace();
		boxBottom.orientation = 0;
		boxBottom.matrix.set( boxBottom.orientation );
		boxBottom.fixInPlace();
		verts[0] = new Vec2 ( 10, screenY * 0.7 );
		verts[1] = new Vec2 ( -10, screenY * 0.7 );
		verts[2] = new Vec2 ( 10, -screenY );
		verts[3] = new Vec2 ( -10, -screenY );
		PolygonRegion boxRight = new PolygonRegion( new Vec2( 0, 10), verts);
		boxRight.fixInPlace();
		boxRight.orientation = 0;
		boxRight.matrix.set( boxRight.orientation );
		boxRight.fixInPlace();
		PolygonRegion boxLeft = new PolygonRegion( new Vec2( screenX * 0.7, 10 ), verts);
		boxLeft.fixInPlace();
		boxLeft.orientation = 0;
		boxLeft.matrix.set( boxLeft.orientation );
		boxLeft.fixInPlace();
		
	}
	
	public void demo4() {
		demoText = "Demo 4:\nSimple harmonic motion\n(with no kinetic friction)";
		World.setConstants( 0.7 ,0 , 0.3 );

		Vec2[] verts = new Vec2[4];
		verts[0] = new Vec2 ( 450, 100 );
		verts[1] = new Vec2 ( -600, 100 );
		verts[2] = new Vec2 ( 450, -100 );
		verts[3] = new Vec2 ( -600, -100 );

		PolygonRegion floor =  new PolygonRegion( new Vec2 ( 240, 600 ), verts );
		floor.fixInPlace();
		floor.orientation = 0;
		floor.matrix.set( floor.orientation );
		
		Disk c = new Disk ( new Vec2( 450, 470), 30 );
		
		
		Disk d = new Disk ( new Vec2( 600, 470), 30 );
		d.fixInPlace();
		
		Vec2[] triangle  = new Vec2[3];
		triangle[0] = new Vec2( 0, 0 );
		triangle[1] = new Vec2( 0, -90 );
		triangle[2] = new Vec2( 200, 0 );
		PolygonRegion t =  new PolygonRegion( new Vec2 ( 0, 500 ),  triangle );
		t.orientation = 0;
		t.matrix.set( t.orientation );
		

		t.fixInPlace();
		Disk e = new Disk (new Vec2( 80, 400 ), 30);

		
		Force.allTheForces.add(new SpringForce( d, c, 50000, 0, 150 ) );
		Force.allTheForces.add( new OmniGravityForce( 500 ) );
		
	}
	
	public void demo5() {
		demoText = "Demo 5:\nDamped harmonic motion";
		World.setConstants( 0, 0, 1 );
		Disk d = new Disk( new Vec2( 300, 300 ), 50);
		d.fixInPlace();
		
		PolygonRegion p = new PolygonRegion( new Vec2 (500, 400) );
		p.velocity.set( 0, 0 );
		p.angularVelocity = 0;
		
		Force.allTheForces.add( new SpringForce( d, p, 50000, 2, 300) );
		Force.allTheForces.add( new OmniGravityForce() );
	}
	
	public void demo6() {
		demoText = "Demo 6:\nArtillery Fire";
		World.setConstants( 0.4, 0.4, 0.3 );
		Vec2[] verts = new Vec2[4];
		verts[0] = new Vec2 ( 400, 100 );
		verts[1] = new Vec2 ( -400, 100 );
		verts[2] = new Vec2 ( 400, -100 );
		verts[3] = new Vec2 ( -400, -100 );

		PolygonRegion p =  new PolygonRegion( new Vec2 ( 240, 600 ), verts );
		p.fixInPlace();
		p.orientation = 0;
		p.matrix.set( p.orientation );
		
		for ( int i = 0; i < 10; i++ ) {
			Disk d = new Disk( new Vec2 (400, 450 - 60*i), Math.random() * 20 + 9);
		}
		
		Force.allTheForces.add( new OmniGravityForce( 100 ) );
		
		PolygonRegion swag = new PolygonRegion( new Vec2 (0, 100) );
		swag.velocity = new Vec2( 150, 0 );
		swag.angularVelocity = -4;
		
	}
	
	public static double random( double min, double max )
	{
		return (double)((max - min) * Math.random() + min);
	}

	

	public void keyPressed() {
		if ( key == '1' ) {
			world.reset();
			demo = 1;
			demo1();
		}
		if ( key == '2' ) {
			world.reset();
			demo = 2;
			demo2();
		}
		if ( key == '3' ) {
			world.reset();
			demo = 3;
			demo3();
		}
		if ( key == '4' ) {
			world.reset();
			demo = 4;
			demo4();
		}
		if ( key == '5' ) {
			world.reset();
			demo = 5;
			demo5();
		}
		if ( key == '6' ) {
			world.reset();
			demo = 6;
			demo6();
		}
		else if ( key == 'p' ) {
			constructingPoly = true;
		}
		else if ( key == 'd' ) {
			constructingCirc = true;
		}
		else if ( key == 'r' ) {
			changingMode = 'r';
		}
		else if ( key == 's' ) {
			changingMode = 's';
		}
		else if ( key == 'k' ) {
			changingMode = 'k';
		}
		else if ( key == CODED && keyCode == UP) {
			if ( changingMode == 'r' ) world.addToRestitutions( 0.1 );
			else if ( changingMode == 'k' ) world.addToKineticCoefficients( 0.1 );
			else if ( changingMode == 's' ) world.addToStaticCoefficients( 0.1 );
		}
		else if ( key == CODED && keyCode == DOWN) {
			if ( changingMode == 'r' ) world.addToRestitutions( -0.1 );
			else if ( changingMode == 'k' ) world.addToKineticCoefficients( -0.1 );
			else if ( changingMode == 's' ) world.addToStaticCoefficients( -0.1 );
		}
	}
	
	public void keyReleased() {
		if ( key == 'p' ) {
			if (constructingPoly) {
				if ( polyVerts.size() < 3 ) new PolygonRegion( new Vec2( pmouseX, pmouseY ) );
				else {
					double xAvg = 0;
					double yAvg = 0;
					for ( int i = 0; i < polyVerts.size(); i++ ) {
						xAvg += polyVerts.get( i ).x;
						yAvg += polyVerts.get( i ).y;
					}
					xAvg /= polyVerts.size();
					yAvg /= polyVerts.size();
					Vec2[] verts = new Vec2[polyVerts.size()];
					for ( int i = 0; i < polyVerts.size(); i++ ) {
						verts[i] = polyVerts.get( i ).sub( new Vec2( xAvg, yAvg ) );
					}

					new PolygonRegion( new Vec2( xAvg, yAvg ), verts );
				}
				polyVerts = new ArrayList<Vec2>();
			}
			constructingPoly = false;
		}
		else if ( key == 'd' ) {
			if ( constructingCirc ) {
				if (circleRadius == 0) new Disk( new Vec2( pmouseX, pmouseY ) );
				else {
					new Disk( circleCenter, circleRadius );
					circleCenter = new Vec2();
					circleRadius = 0;
					constructingCirc = false;
				}
			}
		}
	}
	
	public void mousePressed() {
		if ( constructingPoly ) {
			polyVerts.add( new Vec2( pmouseX, pmouseY ) );
		}
		if ( constructingCirc ) {
			circleCenter = new Vec2( pmouseX, pmouseY );
			circleRadius = 0;
		}
	}
	
	public void mouseDragged() {
		if ( constructingCirc ) {
			circleRadius = (new Vec2( pmouseX, pmouseY ) ).sub( circleCenter ).length();
		}
	}
	

	

	public void draw()
	{
		//I learned how to fix the timestep here:
		//gafferongames.com/game-physics/fix-your-timestep/

		double currentTime = System.currentTimeMillis();
		accumulator += ( currentTime - clockStart ) / 1000;
		while ( accumulator >= World.TIME_STEP ) {
			world.doPhysics();
			clockStart = System.currentTimeMillis();
			accumulator -= World.TIME_STEP;
		}
		
		background(255);
		for (Entity entity : world.allTheEntities)
		{
			entity.draw( this );
			
		}
		if (constructingPoly) {
			noFill();
			beginShape();
			// etc;
			for (int i = 0; i < polyVerts.size(); i++)
			{
				Vec2 v = new Vec2( polyVerts.get( i ) );
				vertex( (float)v.x, (float)v.y );
			}
			endShape();
		}
		if (constructingCirc) {
			noFill();
			ellipse((float)(circleCenter.x), (float)(circleCenter.y), (float)circleRadius * 2, (float)circleRadius * 2 );
		}

		
		for (Force force : Force.allTheForces) {
			force.draw( this );
		}
		fill(0);
		text("Instructions:\n\nType a number from 1 to 6\n to switch between demos\n\nPress 'd' to create a\n randomly sized disk\n\nHold 'd' and drag your mouse\n to define your own disk\n\nPress 'p' to create a random polygon\n\nHold 'p' and click at vertex points\n to define your own polygon\n\nPress 'r', 'k', or 's', to enter \n'restitution, 'kinetic friction,' or \n'static friction' mode\n\nUse the arrow keys to change the\n value of the global coefficient\n corresponding to your mode", screenX - 250, 0, screenX - 100, screenY - 100 );
		text((changingMode == 's' ? ">>" : "  ") + "Coefficient of Static Friction: " + String.format((Math.abs( World.staticFriction ) < 0.9995 && Math.abs( World.staticFriction ) > 0.0005 ) ? "%.3g%n" : "%.4g%n", World.staticFriction) + "\n" + (changingMode == 'k' ? ">>" : "  ") + "Cofficient of Kinetic Friction: " + String.format((Math.abs( World.kineticFriction ) < 0.9995 && Math.abs( World.kineticFriction ) > 0.0005 ) ? "%.3g%n" : "%.4g%n", World.kineticFriction) + "\n" + (changingMode == 'r' ? ">>" : "  ") + "Coefficient of Restitution: " + String.format((Math.abs( World.restitution ) < 0.9995 && Math.abs( World.restitution ) > 0.0005) ? "%.3g%n" : "%.4g%n", World.restitution) + "\n\n\n" + demoText, screenX - 250, screenY/2, screenX - 100, screenY);
		
	}

}
