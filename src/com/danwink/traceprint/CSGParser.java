package com.danwink.traceprint;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.vecmath.Point3f;

import com.phyloa.dlib.math.Trianglef;
import com.phyloa.dlib.util.DGraphics;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.Vertex;


public class CSGParser
{
	public static ArrayList<Trianglef> parse( CSG c )
	{
		ArrayList<Trianglef> geom = new ArrayList<Trianglef>();
		List<Polygon> polys = c.getPolygons();
		for( Polygon p : polys )
		{
			//TODO: Better triangulation
			for( int i = 0; i < p.vertices.size() - 2; i++ ) 
			{
				Vertex p0 = p.vertices.get( 0 );
				Vertex p1 = p.vertices.get( i+1 );
				Vertex p2 = p.vertices.get( i+2 );
				Trianglef t = new Trianglef( 
						new Point3f( (float)p0.pos.x, (float)p0.pos.y, (float)p0.pos.z ),
						new Point3f( (float)p1.pos.x, (float)p1.pos.y, (float)p1.pos.z ),
						new Point3f( (float)p2.pos.x, (float)p2.pos.y, (float)p2.pos.z )
				);
				Optional<String> oCol = p.getStorage().getValue( "material:color" );
				if( oCol.isPresent() )
				{
					String[] s = oCol.get().split( " " );
					t.color = DGraphics.rgb( (int)Float.parseFloat( s[0] )*255, (int)Float.parseFloat( s[1] )*255, (int)Float.parseFloat( s[2] )*255 );
				}
				else
				{
					t.color = DGraphics.rgb( 255, 255, 255 );
				}
				t.color = DGraphics.rgb( 255, 255, 255 );
				geom.add( t );
			}
		}
		return geom;
	}
}
