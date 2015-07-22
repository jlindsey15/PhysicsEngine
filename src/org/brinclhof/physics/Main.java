package org.brinclhof.physics;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.brinclhof.physics.Disk;
import org.brinclhof.physics.Entity;
import org.brinclhof.physics.Collision;
import org.brinclhof.physics.Force;
import org.brinclhof.physics.OmniGravityForce;
import org.brinclhof.physics.PolygonRegion;
import org.brinclhof.physics.SpringForce;
import org.brinclhof.physics.World;
import org.magnos.MathUtils.Vec2;





public class Main
{

	public static void main( String[] args ) {
	       JFrame frame = new JFrame();
	       frame.setSize(1000, 1000); 
	        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        JPanel panel = new javax.swing.JPanel();
	        Demo sketch = new Demo( );
	        
	        panel.add(sketch);
	        frame.add(panel);
	        sketch.init(); 
	        frame.setVisible(true);
	       
	}
}




	
