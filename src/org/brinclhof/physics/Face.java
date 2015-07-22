package org.brinclhof.physics;

import org.magnos.MathUtils.Vec2;


public class Face
{

	public Vec2 p1;
	public Vec2 p2;
	
	public Face ( Vec2 first, Vec2 second ) {
		p1 = first;
		p2 = second;
	}
	public Face()
	{
		// TODO Auto-generated constructor stub
	}
	
	public Vec2 vecBetween() {
		return p2.sub( p1 );
	}

}
