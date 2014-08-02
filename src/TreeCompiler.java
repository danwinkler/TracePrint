import java.io.IOException;

import javafx.scene.paint.Color;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Polygon;
import eu.mihosoft.vrl.v3d.STL;
import eu.mihosoft.vrl.v3d.Transform;

public class TreeCompiler
{
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
		case "translate": {
			Transform t = Transform.unity().translate( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) );
			return parse( (JSONArray)a.get( 4 ) ).transformed( t );
		}
		//Primitives
		case "box": {
			return new Cube( (double)a.get( 1 ), (double)a.get( 2 ), (double)a.get( 3 ) ).toCSG();
		}
		case "stl": {
			return STL.file(java.nio.file.Paths.get( (String)a.get( 1 ) ) );
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
