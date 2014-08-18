package com.danwink.traceprint.ui;

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
import com.danwink.traceprint.raytrace.Light;
import com.danwink.traceprint.raytrace.Scene;
import com.phyloa.dlib.math.Geom;
import com.phyloa.dlib.math.Trianglef;
import com.phyloa.dlib.util.DGraphics;
import com.phyloa.dlib.util.DMath;


public class QuickViewPanel implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener
{
	public GLJPanel gljpanel;
	
	public float lookSpeed = .01f;
	
	public float xa = 0;
	public float za = 0;
	public float dist = 100;
	public float[] lookat = { 0, -100, 100, 0, 0, 0, 0, 0, 1 };
	
	Scene<ArrayList<Geom>> scene;
	ArrayList<Trianglef> geom = new ArrayList<Trianglef>();
	
	int oldx, oldy;

	public QuickViewPanel()
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
		//GL2 gl2 = glautodrawable.getGL().getGL2();
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
    	
    	gl2.glPointSize( 3 );

        if( scene != null )
        {
        	
	    	gl2.glBegin( GL.GL_TRIANGLES );
	        for( Trianglef t : geom ) {
	        	int c = t.color;
	        	gl2.glColor3f( DGraphics.getRed( c )/255.f, DGraphics.getGreen( c )/255.f, DGraphics.getBlue( c )/255.f );
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
	        
	        gl2.glColor3f( 1, 0, 0 );
	        gl2.glBegin( GL.GL_POINTS );
	        for( Light l : scene.lights )
	        {
	        	gl2.glVertex3f( l.pos.x, l.pos.y, l.pos.z );
	        }
	        gl2.glEnd();
        }
    }

	public void renderCSG( Scene<ArrayList<Geom>> scene )
	{
		this.scene = scene;
		geom.clear();
		for( Geom g : scene.g )
		{
			geom.add( (Trianglef)g );
		}
		gljpanel.repaint();
	}
	
	public void computeLookat()
	{
		lookat[0] = DMath.cosf( xa ) * DMath.cosf( za ) * dist;
		lookat[1] = DMath.sinf( xa ) * DMath.cosf( za ) * dist;
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
