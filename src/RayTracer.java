import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.phyloa.dlib.math.Geom;
import com.phyloa.dlib.math.Intersection;
import com.phyloa.dlib.math.Rayf;
import com.phyloa.dlib.math.Trianglef;
import com.phyloa.dlib.util.DGraphics;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.Vertex;
import eu.mihosoft.vrl.v3d.ext.org.poly2tri.PolygonUtil;


public class RayTracer
{
	BufferedImage im;
	Point3f[][] values;
	int width;
	int height;
	
	CSG c;
	ArrayList<Geom> geom = new ArrayList<Geom>();
	
	Point3f cameraLoc = new Point3f( 0, 0, 0 );
	Vector3f cameraLook = new Vector3f();
	Vector3f cameraUp = new Vector3f();
	Matrix3f camera = new Matrix3f();
	float viewAngleX;
	float viewAngleY;
	float lift;
	float breadth;
	
	public RayTracer( CSG c, int width, int height )
	{
		im = DGraphics.createBufferedImage( width, height );
		values = new Point3f[width][height];
		this.width = width;
		this.height = height;
		this.c = c;
		
		viewAngleX = 50.f;
		viewAngleY = (viewAngleX / (float)width) * height;
		lift = (float) Math.tan( Math.toRadians( viewAngleY ) );
		breadth = (float) Math.tan( Math.toRadians( viewAngleX ) );
	}
	
	public void camera( float cx, float cy, float cz, float lx, float ly, float lz, float ux, float uy, float uz )
	{
		cameraLoc.x = cx;
		cameraLoc.y = cy;
		cameraLoc.z = cz;
		cameraLook.x = lx;
		cameraLook.y = ly;
		cameraLook.z = lz;
		cameraUp.x = ux;
		cameraUp.y = uy;
		cameraUp.z = uz;
		cameraUp.scale( -1 );
		Vector3f cameraRight = new Vector3f();
		cameraRight.cross( cameraLook, cameraUp );
		
		cameraUp.normalize();
		cameraLook.normalize();
		cameraRight.normalize();
		camera.setIdentity();
		camera.m00 = cameraRight.x;
		camera.m10 = cameraRight.y;
		camera.m20 = cameraRight.z;
		camera.m01 = cameraLook.x;
		camera.m11 = cameraLook.y;
		camera.m21 = cameraLook.z;
		camera.m02 = cameraUp.x;
		camera.m12 = cameraUp.y;
		camera.m22 = cameraUp.z;
	}
	
	public void setup()
	{
		List<Polygon> polys = c.getPolygons();
		for( Polygon p : polys )
		{
			for( int i = 0; i < p.vertices.size() - 2; i++ ) 
			{
				Vertex p0 = p.vertices.get( i );
				Vertex p1 = p.vertices.get( i+1 );
				Vertex p2 = p.vertices.get( i+2 );
				Trianglef t = new Trianglef( 
						new Point3f( (float)p0.pos.x, (float)p0.pos.y, (float)p0.pos.z ),
						new Point3f( (float)p1.pos.x, (float)p1.pos.y, (float)p1.pos.z ),
						new Point3f( (float)p2.pos.x, (float)p2.pos.y, (float)p2.pos.z )
				);
				t.color = DGraphics.rgb( 255, 0, 0 );
				geom.add( t );
			}
		}
	}
	
	public void render()
	{
		for( int y = 0; y < height; y++ ) 
		{
			for( int x = 0; x < width; x++ ) 
			{
				Rayf ray = new Rayf( cameraLoc, getLookVector( x, y ) );
				values[x][y] = trace( ray );
			}
		}
		
		for( int y = 0; y < height; y++ ) 
		{
			for( int x = 0; x < width; x++ ) 
			{
				Point3f p = values[x][y];
				im.setRGB( x, y, DGraphics.rgb( (int)p.x, (int)p.y, (int)p.z ) );
			}
		}
		im.flush();
	}
	
	public Point3f trace( Rayf ray )
	{
		for( int i = 0; i < geom.size(); i++ )
		{
			Geom g = geom.get( i );
			Intersection intersect = g.intersects( ray );
			if( intersect != null )
			{
				int c = g.getColor( 0, 0 );
				return new Point3f( DGraphics.getRed( c ), DGraphics.getGreen( c ), DGraphics.getBlue( c ) );
			}
		}
		return new Point3f( 0, 0, 0 );
	}
	
	public Vector3f getLookVector( int x, int y )
	{
		float xNorm = (float)x / (float)width;
		float yNorm = (float)y / (float)height;
		Vector3f vec = new Vector3f( (breadth * xNorm) - (breadth/2), 1, (lift * yNorm) - (lift/2) );
		camera.transform( vec );
		return vec;
	}
}
