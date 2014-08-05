package com.danwink.traceprint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import com.phyloa.dlib.math.Trianglef;
import com.phyloa.dlib.util.DMath;

import eu.mihosoft.vrl.v3d.CSG;


public class RenderPanel implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener
{
	public GLJPanel gljpanel;
	
	public float lookSpeed = .01f;
	
	public float xa = 0;
	public float za = 0;
	public float dist = 100;
	public float[] lookat = { 0, -100, 100, 0, 0, 0, 0, 0, 1 };
	
	ArrayList<Trianglef> geom;
	
	int oldx, oldy;
	
	public RenderPanel()
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		gljpanel = new GLJPanel( glcapabilities );
		
		gljpanel.addGLEventListener( this );
		gljpanel.addMouseListener( this );
		gljpanel.addMouseWheelListener( this );
		gljpanel.addMouseMotionListener( this );
		
		computeLookat();
	}
	
	public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height )
    {  
		GL2 gl2 = glautodrawable.getGL().getGL2();
    }

    public void init( GLAutoDrawable glautodrawable )
    {
    }
    
    public void dispose( GLAutoDrawable glautodrawable )
    {
    }
    
    public void display( GLAutoDrawable glautodrawable )
    {
    	GL2 gl2 = glautodrawable.getGL().getGL2();
    	GLU glu = GLU.createGLU( gl2 );
    	
    	int width = glautodrawable.getWidth();
    	int height = glautodrawable.getHeight();
    	
    	gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();
        glu.gluPerspective( 50, (float)width/(float)height, 1, 1000 );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();
        glu.gluLookAt( lookat[0], lookat[1], lookat[2], lookat[3], lookat[4], lookat[5], lookat[6], lookat[7], lookat[8] );
        
        gl2.glEnable( GL2.GL_LINE_SMOOTH );
        gl2.glEnable( GL2.GL_POLYGON_SMOOTH );
        gl2.glHint( GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST );
        gl2.glHint( GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST );
        
        gl2.glEnable( GL2.GL_DEPTH_TEST );
    	gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );

        if( geom != null )
        {
        	gl2.glColor3f( 1, 1, 1 );
	    	gl2.glBegin( GL.GL_TRIANGLES );
	        for( Trianglef t : geom ) {
	        	gl2.glVertex3f( t.p1.x, t.p1.y, t.p1.z );
	        	gl2.glVertex3f( t.p2.x, t.p2.y, t.p2.z );
	        	gl2.glVertex3f( t.p3.x, t.p3.y, t.p3.z );
	        }
	        gl2.glEnd();
	        
	        gl2.glColor3f( 0, 1, 0 );
	        for( Trianglef t : geom ) {
	        	gl2.glBegin( GL.GL_LINE_LOOP );
	        	gl2.glVertex3f( t.p1.x, t.p1.y, t.p1.z );
	        	gl2.glVertex3f( t.p2.x, t.p2.y, t.p2.z );
	        	gl2.glVertex3f( t.p3.x, t.p3.y, t.p3.z );
	        	gl2.glEnd();
	        }
        }
    }

	public void renderCSG( CSG g )
	{
		geom = CSGParser.parse( g );
		gljpanel.repaint();
	}
	
	public void computeLookat()
	{
		lookat[0] = DMath.cosf( xa ) * dist;
		lookat[1] = DMath.sinf( xa ) * dist;
		lookat[2] = DMath.sinf( za ) * dist;
	}

	public void mouseWheelMoved( MouseWheelEvent e )
	{
		dist += e.getPreciseWheelRotation() * 10;
		computeLookat();
		gljpanel.display();
	}

	public void mouseClicked( MouseEvent e )
	{
		
	}

	public void mousePressed( MouseEvent e )
	{
		oldx = e.getX();
		oldy = e.getY();
	}

	public void mouseReleased( MouseEvent e )
	{
		
	}

	public void mouseEntered( MouseEvent e )
	{
		
	}

	public void mouseExited( MouseEvent e )
	{
		
	}

	@Override
	public void mouseDragged( MouseEvent e )
	{
		int nx = e.getX();
		int ny = e.getY();
		xa += -(nx - oldx) * lookSpeed;
		za = DMath.bound( za + ((ny - oldy) * lookSpeed), -DMath.PIF/2, DMath.PIF/2 );
		
		oldx = nx;
		oldy = ny;
		
		computeLookat();
		
		gljpanel.display();
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		// TODO Auto-generated method stub
		
	}
}
