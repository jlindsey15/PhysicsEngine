# PhysicsEngine

A 2D physics simulation program written in Java.  Supports collisions between convex polygons and circles, remote attractive/repulsive forces (e.g. universal gravitation), "Earth-style" downward gravitation, springs (damped and simple), and friction.  Comes with 6 preconfigured demos of the engine's various capabilities.

Instructions:

Type a number from 1 to 6 to switch between demos.  Press 'd' to create a randomly sized disk.  Hold 'd' and drag your mouse to define your own disk.  Press 'p' to create a random polygon.  Hold 'p' and click at vertex points to define your own polygon.  Press 'r', 'k', or 's', to enter 'restitution, 'kinetic friction,' or 'static friction' mode.  Use the arrow keys to change the value of the global coefficient corresponding to your mode.

Also, I couldn't have done this without Randy Gaul's awesome series of tutorials about physics engines. I based a lot of this on his ideas/techniques, and I used his (very useful) custom math library (ported to Java - see license for details).
