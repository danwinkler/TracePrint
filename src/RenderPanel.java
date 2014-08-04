import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;

import com.phyloa.dlib.math.Trianglef;

import eu.mihosoft.vrl.v3d.CSG;


public class RenderPanel implements GLEventListener
{
	public GLJPanel gljpanel;
	public float[] lookat = { 0, -100, 100, 0, 0, 0, 0, 0, 1 };
	
	ArrayList<Trianglef> geom;
	
	public RenderPanel()
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		gljpanel = new GLJPanel( glcapabilities );
		
		gljpanel.addGLEventListener( this );
	}
	
	public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height )
    {  
    	GL2 gl2 = glautodrawable.getGL().getGL2();
    	GLU glu = GLU.createGLU( gl2 );
    	
    	gl2.glMatrixMode( GL2.GL_PROJECTION );
        gl2.glLoadIdentity();
        glu.gluPerspective( 50, (float)width/(float)height, 1, 1000 );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();
        glu.gluLookAt( lookat[0], lookat[1], lookat[2], lookat[3], lookat[4], lookat[5], lookat[6], lookat[7], lookat[8] );
        
        //gl2.glViewport( 0, 0, width, height );
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
    	gl2.glClear( GL.GL_COLOR_BUFFER_BIT );

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
		gljpanel.display();
	}
}
