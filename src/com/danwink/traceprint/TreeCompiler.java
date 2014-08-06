package com.danwink.traceprint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Cylinder;
import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.Polyhedron;
import eu.mihosoft.vrl.v3d.STL;
import eu.mihosoft.vrl.v3d.Sphere;
import eu.mihosoft.vrl.v3d.Transform;
import eu.mihosoft.vrl.v3d.Vector3d;

public class TreeCompiler
{
	static 
	{
		CSG.setDefaultOptType( CSG.OptType.POLYGON_BOUND );
	}
	
	public static CSG parse( String s ) throws IOException
	{
		return parse( (JSONArray)JSONValue.parse( s ) );
	}
	
	public static CSG parse( JSONArray a ) throws IOException
	{
		String type = (String)a.get( 0 );
		switch( type ) {
		//Transforms
		case "union": {
			CSG c = parse( (JSONArray)a.get( 1 ) );
			for( int i = 2; i < a.size(); i++ )
			{
				c = c.union( parse( (JSONArray)a.get( i ) ) );
			}
			return c;
		}
		case "difference": {
			CSG c1 = parse( (JSONArray)a.get( 1 ) );
			CSG c2 = parse( (JSONArray)a.get( 2 ) );
			CSG ret = null;
			try {
				ret = c1.difference( c2 );
			} catch( Exception ex )
			{
				ret = c1;
			}
			return ret;
		}
		case "intersection": {
			CSG c = parse( (JSONArray)a.get( 1 ) );
			for( int i = 2; i < a.size(); i++ )
			{
				c = c.intersect( parse( (JSONArray)a.get( i ) ) );
			}
			return c;
		}
		case "translate": {
			Transform t = Transform.unity().translate( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
			return parse( (JSONArray)a.get( 4 ) ).transformed( t );
		}
		//Primitives
		case "box": {
			return new Cube( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) ).toCSG();
		}
		case "sphere": {
			return new Sphere( (double)a.get( 1 ), (int)(long)a.get( 2 ), (int)(long)a.get( 3 ) ).toCSG();
		}
		case "cylinder": {
			return new Cylinder( (double)a.get( 1 ), (double)a.get( 2 ), (int)a.get( 3 ) ).toCSG();
		}
		case "stl": {
			return STL.file( java.nio.file.Paths.get( (String)a.get( 1 ) ) );
		}
		case "polyhedron": {
			List<Vector3d> points = new ArrayList<Vector3d>();
			List<List<Integer>> faces = new ArrayList<List<Integer>>();
			JSONArray parr = (JSONArray)a.get( 1 );
			for( int i = 0; i < parr.size(); i++ )
			{
				JSONArray parr2 = (JSONArray)parr.get( i );
				points.add( new Vector3d( (double)parr2.get( 0 ), (double)parr2.get( 1 ), (double)parr2.get( 2 ) ) );
			}
			
			JSONArray farr = (JSONArray)a.get( 2 );
			for( int i = 0; i < farr.size(); i++ )
			{
				JSONArray farr2 = (JSONArray)farr.get( i );
				ArrayList<Integer> fal = new ArrayList<Integer>();
				for( int j = 0; j < farr2.size(); j++ )
				{
					fal.add( (int)farr2.get( j ) );	
				}
				faces.add( fal );
			}
			return new Polyhedron( points, faces ).toCSG();
		}
		//Modifiers
		case "color": {
			Color color = Color.color( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
			return parse( (JSONArray)a.get( 4 ) ).color( color );
		}
		}
		return null;
	}
}
