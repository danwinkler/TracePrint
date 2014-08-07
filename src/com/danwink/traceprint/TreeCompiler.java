package com.danwink.traceprint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Transform;
import eu.mihosoft.vrl.v3d.Vector3d;

public class TreeCompiler
{
	static 
	{
		CSG.setDefaultOptType( CSG.OptType.POLYGON_BOUND );
	}
	
	int numNodes;
	int onNode;
	
	public interface ParseCallback {
	    void finished( CSG g );
	}
	
	public void runParseThread( String s, ParseCallback cb )
	{
		numNodes = 0;
		onNode = 0;
		Thread t = new Thread( new Runnable() {
			public void run()
			{
				try
				{
					Node n = createTree( s );
					CSG g = n.toCSG();
					cb.finished( g );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public Node createTree( String s )
	{
		return createTree( (JSONArray)JSONValue.parse( s ) );
	}
	
	public Node createTree( JSONArray a )
	{
		numNodes++;
		String type = (String)a.get( 0 );
		switch( type ) {
		//Transforms
		case "union": {
			Union u = new Union();
			for( int i = 1; i < a.size(); i++ )
			{
				u.children.add( createTree( (JSONArray)a.get( i ) ) );
			}
			return u;
		}
		case "difference": {
			Node c1 = createTree( (JSONArray)a.get( 1 ) );
			Node c2 = createTree( (JSONArray)a.get( 2 ) );
			return new Difference( c1, c2 );
		}
		case "intersection": {
			Intersection in = new Intersection();
			for( int i = 1; i < a.size(); i++ )
			{
				in.children.add( createTree( (JSONArray)a.get( i ) ) );
			}
			return in;
		}
		case "translate": {
			Translate t = new Translate( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
			t.children.add( createTree( (JSONArray)a.get( 4 ) ) );
			return t;
		}
		//Primitives
		case "box": {
			return new Box( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
		}
		case "sphere": {
			return new Sphere( (double)a.get( 1 ), (int)(long)a.get( 2 ), (int)(long)a.get( 3 ) );
		}
		case "cylinder": {
			return new Cylinder( (double)a.get( 1 ), (double)a.get( 2 ), (int)a.get( 3 ) );
		}
		case "stl": {
			return new STL( (String)a.get( 1 ) );
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
			return new Polyhedron( points, faces );
		}
		//Modifiers
		case "color": {
			Color color = new Color( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
			color.children.add( createTree( (JSONArray)a.get( 4 ) ) );
			return color;
		}
		}
		return null;
	}
	
	public abstract class Node
	{
		ArrayList<Node> children = new ArrayList<Node>();
		
		public CSG toCSG() throws IOException
		{
			onNode++;
			return _impltoCSG();
		}
		
		protected abstract CSG _impltoCSG() throws IOException;
	}
	
	public class Union extends Node
	{
		public CSG _impltoCSG() throws IOException
		{
			CSG c = children.get( 0 ).toCSG();
			for( int i = 1; i < children.size(); i++ )
			{
				c = c.union( children.get( i ).toCSG() );
			}
			return c;
		}
	}
	
	public class Difference extends Node
	{
		public Difference()
		{
			
		}
		
		public Difference( Node c1, Node c2 )
		{
			children.add( c1 );
			children.add( c2 );
		}

		protected CSG _impltoCSG() throws IOException
		{
			CSG c1 = children.get( 0 ).toCSG();
			CSG c2 = children.get( 1 ).toCSG();
			CSG ret;
			try {
				ret = c1.difference( c2 );
			}
			catch( Exception ex )
			{
				ret = c1;
			}
			return ret;
		}
	}
	
	public class Intersection extends Node
	{
		protected CSG _impltoCSG() throws IOException
		{
			CSG c = children.get( 0 ).toCSG();
			for( int i = 1; i < children.size(); i++ )
			{
				c = c.intersect( children.get( i ).toCSG() );
			}
			return c;
		}
	}
	
	public class Translate extends Node
	{
		double x, y, z;
		
		public Translate()
		{
			
		}
		
		public Translate( double x, double y, double z )
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		protected CSG _impltoCSG() throws IOException
		{
			Transform t = Transform.unity().translate( x, y, z );
			return children.get( 0 ).toCSG().transformed( t );
		}
	}
	
	public class Box extends Node
	{
		double x, y, z;
		
		public Box( double x, double y, double z )
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		protected CSG _impltoCSG()
		{
			return new eu.mihosoft.vrl.v3d.Cube( x, y, z ).toCSG();
		}
	}
	
	public class Sphere extends Node
	{
		double r;
		int slices, stacks;
		
		public Sphere( double r, int slices, int stacks )
		{
			this.r = r;
			this.slices = slices;
			this.stacks = stacks;
		}

		protected CSG _impltoCSG()
		{
			return new eu.mihosoft.vrl.v3d.Sphere( r, slices, stacks ).toCSG();
		}
	}
	
	public class Cylinder extends Node
	{
		double r, height;
		int slices;
		
		public Cylinder( double r, double height, int slices )
		{
			this.r = r;
			this.height = height;
			this.slices = slices;
		}

		protected CSG _impltoCSG()
		{
			return new eu.mihosoft.vrl.v3d.Cylinder( r, height, slices ).toCSG();
		}
	}
	
	public class STL extends Node
	{
		String filename;
		
		public STL( String filename )
		{
			this.filename = filename;
		}

		protected CSG _impltoCSG() throws IOException
		{
			return eu.mihosoft.vrl.v3d.STL.file( java.nio.file.Paths.get( filename ) );
		}

	}
	
	public class Polyhedron extends Node
	{
		List<Vector3d> points = new ArrayList<Vector3d>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		
		public Polyhedron( List<Vector3d> points, List<List<Integer>> faces )
		{
			this.points = points;
			this.faces = faces;
		}

		protected CSG _impltoCSG() throws IOException
		{
			return new eu.mihosoft.vrl.v3d.Polyhedron( points, faces ).toCSG();
		}
	}
	
	public class Color extends Node
	{
		double r, g, b;
		public Color( double r, double g, double b )
		{
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		protected CSG _impltoCSG() throws IOException
		{
			return children.get( 0 ).toCSG().color( javafx.scene.paint.Color.color( r, g, b ) );
		}
	}
}
