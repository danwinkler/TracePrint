package com.danwink.traceprint;
import java.io.File;


public class RenderFileParser
{
	public static void parse( String file, RayTracer rt )
	{
		String[] lines = file.split( "\n" );
		for( String line : lines )
		{
			String[] parts = line.split( " " );
			
			if( parts.length <= 0 ) 
			{
				continue;
			}
			
			switch( parts[0] ) 
			{
			case "camera":
				rt.camera( 
					Float.parseFloat( parts[1] ), 
					Float.parseFloat( parts[2] ), 
					Float.parseFloat( parts[3] ), 
					Float.parseFloat( parts[4] ), 
					Float.parseFloat( parts[5] ), 
					Float.parseFloat( parts[6] ), 
					Float.parseFloat( parts[7] ), 
					Float.parseFloat( parts[8] ), 
					Float.parseFloat( parts[9] ) 
				);
				break;
			case "light":
				rt.light( 
					Float.parseFloat( parts[1] ), 
					Float.parseFloat( parts[2] ), 
					Float.parseFloat( parts[3] )
				);
			}
		}
	}
}
