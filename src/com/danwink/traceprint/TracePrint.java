package com.danwink.traceprint;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.phyloa.dlib.util.DFile;
import com.phyloa.dlib.util.DProperties;
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
	
	public StatusBar sb;
	
	public DProperties prefs;
	
	CSG g;
	FileWatcher fw;
	
	TreeCompiler tc = new TreeCompiler();
	
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
		
		sb = new StatusBar();
		container.getContentPane().add( sb, BorderLayout.SOUTH );
		
		container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		container.setSize( 640, 480 );
		container.setVisible( true );
		
		container.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent we ) 
			{
				try
				{
					prefs.save();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		});
		
		try
		{
			prefs = new DProperties( "data.properties" );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			prefs = new DProperties();
		}
		
		String lastOpenedFile = prefs.get( "file", String.class );
		if( lastOpenedFile != null )
		{
			filemenu.f = new File( lastOpenedFile );
			load( filemenu.f );
		}
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
			sb.fileName.setText( f.getAbsolutePath() );
			String text = DFile.loadText( f );
			tc.runParseThread( text, c -> { g = c; rp.renderCSG( g ); } );
			sb.startProgressThread( () -> tc.getProgress(), () -> tc.getMax() );
			cp.updateCode( text );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}
}
