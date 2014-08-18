package com.danwink.traceprint.raytrace;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.phyloa.dlib.math.Geom;
import com.phyloa.dlib.math.Intersection;
import com.phyloa.dlib.math.Rayf;
import com.phyloa.dlib.util.DGraphics;

public class RayTracer
{
	public BufferedImage im;
	Point3f[][] values;
	int width;
	int height;
	
	Scene<ArrayList<Geom>> scene;
	
	Point3f cameraLoc = new Point3f( 0, 0, 0 );
	Vector3f cameraLook = new Vector3f();
	Vector3f cameraUp = new Vector3f();
	Matrix3f camera = new Matrix3f();
	float viewAngleX;
	float viewAngleY;
	float lift;
	float breadth;
	
	public RayTracer( Scene<ArrayList<Geom>> scene, int width, int height )
	{
		this.scene = scene;
		im = DGraphics.createBufferedImage( width, height );
		values = new Point3f[width][height];
		this.width = width;
		this.height = height;
		
		viewAngleX = 50.f;
		viewAngleY = (viewAngleX / (float)width) * height;
		lift = (float) Math.tan( Math.toRadians( viewAngleY ) );
		breadth = (float) Math.tan( Math.toRadians( viewAngleX ) );
	}
	
	
	public void camera( float[] arr )
	{
		camera( arr[0],
				arr[1],
				arr[2],
				arr[3],
				arr[4],
				arr[5],
				arr[6],
				arr[7],
				arr[8]
		);
	}
	
	public void camera( float cx, float cy, float cz, float lx, float ly, float lz, float ux, float uy, float uz )
	{
		cameraLoc.x = cx;
		cameraLoc.y = cy;
		cameraLoc.z = cz;
		cameraLook.x = lx;
		cameraLook.y = ly;
		cameraLook.z = lz;
		cameraLook.sub( cameraLoc );
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
	
	long lastUpdate;
	public void render()
	{
		lastUpdate = System.currentTimeMillis();
		System.out.println( "RENDER START" );
		for( int y = 0; y < height; y++ ) 
		{
			if( System.currentTimeMillis() - lastUpdate > 1000 ) 
			{ 
				lastUpdate = System.currentTimeMillis();
				System.out.println( "RENDER LINE: " + y ); 
			}
			for( int x = 0; x < width; x++ ) 
			{
				Rayf ray = new Rayf( cameraLoc, getLookVector( x, y ) );
				values[x][y] = trace( ray );
			}
		}
		System.out.println( "RENDER: Filling Image Buffer" );
		for( int y = 0; y < height; y++ ) 
		{
			for( int x = 0; x < width; x++ ) 
			{
				Point3f p = values[x][y];
				im.setRGB( x, y, DGraphics.rgb( (int)p.x, (int)p.y, (int)p.z ) );
			}
		}
		im.flush();
		System.out.println( "RENDER FINISH" );
	}
	
	public Point3f trace( Rayf ray )
	{
		Intersection intersect = getIntersection( ray );
		if( intersect != null )
		{
			int c = intersect.getGeom().getColor( 0, 0 );
			for( Light light : scene.lights )
			{
				Vector3f lv = new Vector3f( light.pos );
				lv.sub( intersect.getLoc() );
				Rayf lr = new Rayf( intersect.getLoc(), lv );
				Intersection li = getIntersection( lr );
				if( li == null )
				{
					c = DGraphics.brighten( c );
				}
				else
				{
					c = DGraphics.darken( c );
				}
			}
			return new Point3f( DGraphics.getRed( c ), DGraphics.getGreen( c ), DGraphics.getBlue( c ) );
		}
		return new Point3f( 0, 0, 0 );
	}
	
	public Intersection getIntersection( Rayf ray )
	{
		Intersection point = null;
		
		for( int j = 0; j < scene.g.size(); j++ )
		{
			Intersection temp = scene.g.get( j ).intersects( ray );
			if( point != null && temp != null )
			{
				if( temp.getDist() < point.getDist() )
					point = temp;
			}
			else if( point == null )
			{
				point = temp;
			}
		}
		
		return point;
	}
	
	public Vector3f getLookVector( int x, int y )
	{
		float xNorm = (float)x / (float)width;
		float yNorm = (float)y / (float)height;
		Vector3f vec = new Vector3f( (breadth * xNorm) - (breadth/2), 1, (lift * yNorm) - (lift/2) );
		camera.transform( vec );
		return vec;
	}

	public void light( float x, float y, float z )
	{
		//lights.add( new Point3f( x, y, z ) );
	}
}
