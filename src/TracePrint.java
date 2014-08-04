import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.phyloa.dlib.util.DFile;
import com.phyloa.dlib.util.FileWatcher;
import com.phyloa.dlib.util.FileWatcher.FileWatcherListener;

import eu.mihosoft.vrl.v3d.CSG;

public class TracePrint implements FileWatcherListener
{
	public JFrame container;
	
	public JMenuBar menubar;
	public FileMenu filemenu;
	
	public JSplitPane splitPane;
	
	public RenderPanel rp;
	public CodePanel cp;
	
	CSG g;
	FileWatcher fw;
	
	public TracePrint()
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e )
		{
			e.printStackTrace();
		}
		
		container = new JFrame( "TracePrint" );
		
		menubar = new JMenuBar();
		container.setJMenuBar( menubar );
		
		filemenu = new FileMenu( this );
		menubar.add( filemenu );
		
		rp = new RenderPanel();
		
		cp = new CodePanel( this );
		
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, cp, rp.gljpanel );
		container.getContentPane().add( splitPane, BorderLayout.CENTER );
		
		container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		container.setSize( 640, 480 );
		container.setVisible( true );
	}
	
	public void clear()
	{
		
	}
	
	public void load( File f )
	{
		fw = new FileWatcher( f );
		fw.addListener( this );
		fw.start();
		
		changed( f );
	}
	
	public void save( File f )
	{
		
	}
	
	public static void main( String[] args ) throws IOException
	{
		/*
		CSG c = TreeCompiler.parse( DFile.loadText( "python/test.json" ) );
		c = STL.file(java.nio.file.Paths.get("C:\\Users\\Daniel\\Desktop\\stuff\\3dprinting\\3dprintpython\\dan\\project\\ballsculpture\\ballsculpture2.stl"));
		RayTracer rt = new RayTracer( c, 600, 400 );
		RenderFileParser.parse( DFile.loadText( "python/renderfile.txt" ), rt );
		rt.setup();
		rt.render();
		DFile.saveImage( "C:/rt/tp" + System.currentTimeMillis() + ".png", "png", rt.im );
		//DFile.saveText( "test.stl", rt.toStlString() );
		*/
		
		TracePrint tp = new TracePrint();
	}

	public void changed( File f )
	{
		try
		{
			String text = DFile.loadText( f );
			g = TreeCompiler.parse( text );
			cp.updateCode( text );
			rp.renderCSG( g );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
}
