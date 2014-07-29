import java.io.FileNotFoundException;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JFrame;

import com.phyloa.dlib.util.DFile;

import eu.mihosoft.vrl.v3d.CSG;


public class TracePrint
{
	public JFrame container;
	
	public static void main( String[] args ) throws IOException
	{
		CSG c = TreeCompiler.parse( DFile.loadText( "python/test.json" ) );
		RayTracer rt = new RayTracer( c, 200, 150 );
		rt.camera( 0, -10, 10, 0, 1, -1, 0, 0, 1 );
		rt.setup();
		rt.render();
		DFile.saveImage( "C:/rt/tp" + System.currentTimeMillis() + ".png", "png", rt.im );
		//DFile.saveText( "test.stl", rt.toStlString() );
	}
}
