package com.danwink.traceprint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import eu.mihosoft.vrl.v3d.CSG;
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
	
	public int getProgress()
	{
		return onNode;
	}
	
	public int getMax()
	{
		return numNodes;
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
					ArrayList<ModuleContainer> modules = getModules( s );
					boolean complete = false;
					while( !complete )
					{
						complete = true;
						for( int i = 0; i < modules.size(); i++ )
						{
							ModuleContainer m = modules.get( i );
							if( m.csg == null )
							{
								if( m.satisfied( modules ) )
								{
									m.buildCSG( modules );
								}
								else
								{
									complete = false;
								}
							}
						}
					}
					for( ModuleContainer mc : modules )
					{
						if( mc.name.equals( "main" ) )
						{
							cb.finished( mc.csg );
						}
					}
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public ArrayList<ModuleContainer> getModules( String s )
	{
		return getModules( (JSONObject)JSONValue.parse( s ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public ArrayList<ModuleContainer> getModules( JSONObject o )
	{
		ArrayList<ModuleContainer> modules = new ArrayList<ModuleContainer>();
		JSONObject geometry = (JSONObject)o.get( "geometry" );
		geometry.forEach( (k, v) -> {
			ModuleContainer m = new ModuleContainer();
			m.name = (String)k;
			m.node = parseGeometryJSON( (JSONObject)v );
			m.depends = m.node.getDependencies();
			modules.add( m );
		});
		return modules;
	}
	
	public Node parseGeometryJSON( String s )
	{
		return parseGeometryJSON( (JSONObject)JSONValue.parse( s ) );
	}
	
	@SuppressWarnings( "unchecked" )
	public Node parseGeometryJSON( JSONObject a )
	{
		numNodes++;
		String type = (String)a.get( "type" );
		switch( type ) {
		//Transforms
		case "union": {
			Union u = new Union();
			JSONArray children = (JSONArray)a.get( "children" );
			children.forEach( c -> { 
				u.children.add( parseGeometryJSON( (JSONObject)c ) );
			});
			return u;
		}
		case "difference": {
			Node c1 = parseGeometryJSON( (JSONObject)a.get( "a" ) );
			Node c2 = parseGeometryJSON( (JSONObject)a.get( "b" ) );
			return new Difference( c1, c2 );
		}
		case "intersection": {
			Intersection in = new Intersection();
			JSONArray children = (JSONArray)a.get( "children" );
			children.forEach( c -> { 
				in.children.add( parseGeometryJSON( (JSONObject)c ) );
			});
			return in;
		}
		case "translate": {
			Translate t = new Translate( (double)a.get( "x" ), (double)a.get( "y" ), (double)a.get( "z" ) );
			t.children.add( parseGeometryJSON( (JSONObject)a.get( "children" ) ) );
			return t;
		}
		//Primitives
		case "box": {
			return new Box( (double)a.get( "x" ), (double)a.get( "y" ), (double)a.get( "z" ) );
		}
		case "sphere": {
			return new Sphere( (double)a.get( "r" ), (int)(long)a.get( "slices" ), (int)(long)a.get( "stacks" ) );
		}
		case "cylinder": {
			return new Cylinder( (double)a.get( "r" ), (double)a.get( "height" ), (int)a.get( "slices" ) );
		}
		case "stl": {
			return new STL( (String)a.get( "path" ) );
		}
		case "polyhedron": {
			List<Vector3d> points = new ArrayList<Vector3d>();
			List<List<Integer>> faces = new ArrayList<List<Integer>>();
			JSONArray parr = (JSONArray)a.get( "points" );
			for( int i = 0; i < parr.size(); i++ )
			{
				JSONArray parr2 = (JSONArray)parr.get( i );
				points.add( new Vector3d( (double)parr2.get( 0 ), (double)parr2.get( 1 ), (double)parr2.get( 2 ) ) );
			}
			
			JSONArray farr = (JSONArray)a.get( "faces" );
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
			Color color = new Color( (double)a.get( "r" ), (double)a.get( "g" ), (double)a.get( "b" ) );
			color.children.add( parseGeometryJSON( (JSONObject)a.get( "children" ) ) );
			return color;
		}
		}
		return null;
	}
	
	public class ModuleContainer
	{
		String name;
		Node node;
		CSG csg;
		ArrayList<String> depends;
		
		public boolean satisfied( ArrayList<ModuleContainer> n )
		{
			for( int i = 0; i < depends.size(); i++ )
			{
				String depend = depends.get( i );
				for( int j = 0; j < n.size(); j++ )
				{
					ModuleContainer mc = n.get( j );
					if( depend.equals( mc.name ) && mc.csg == null )
					{
						return false;
					}
				}
			}
			return true;
		}
		
		public void buildCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			csg = node.toCSG( modules );
		}
	}
	
	public abstract class Node
	{
		ArrayList<Node> children = new ArrayList<Node>();
		
		public CSG toCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			onNode++;
			return _impltoCSG( modules );
		}
		
		public ArrayList<String> getDependencies()
		{
			if( children.size() == 0 )
			{
				return new ArrayList<String>();
			}
			else
			{
				ArrayList<String> arr = new ArrayList<String>();
				children.forEach( c -> arr.addAll( c.getDependencies() ) );
				return arr;
			}
		}
		
		protected abstract CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException;
	}
	
	public class Union extends Node
	{
		public CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			CSG c = children.get( 0 ).toCSG( modules );
			for( int i = 1; i < children.size(); i++ )
			{
				c = c.union( children.get( i ).toCSG( modules ) );
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			CSG c1 = children.get( 0 ).toCSG( modules );
			CSG c2 = children.get( 1 ).toCSG( modules );
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
		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			CSG c = children.get( 0 ).toCSG( modules );
			for( int i = 1; i < children.size(); i++ )
			{
				c = c.intersect( children.get( i ).toCSG( modules ) );
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			Transform t = Transform.unity().translate( x, y, z );
			return children.get( 0 ).toCSG( modules ).transformed( t );
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules )
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules )
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules )
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
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

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
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
		
		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			return children.get( 0 ).toCSG( modules ).color( javafx.scene.paint.Color.color( r, g, b ) );
		}
	}
	
	public class Module extends Node
	{
		String name;

		protected CSG _impltoCSG( ArrayList<ModuleContainer> modules ) throws IOException
		{
			for( int i = 0; i < modules.size(); i++ )
			{
				ModuleContainer m = modules.get( i );
				if( name.equals( m.name ) )
				{
					return m.csg;
				}
			}
			return null;
		}
	}
}
