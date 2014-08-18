package com.danwink.traceprint.ui;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.danwink.traceprint.csg.CSGHelper;
import com.danwink.traceprint.csg.JSONCompiler;
import com.danwink.traceprint.raytrace.Scene;
import com.phyloa.dlib.math.Geom;
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
	public RenderMenu rendermenu;
	
	public JSplitPane splitPane;
	
	public QuickViewPanel rp;
	public CodePanel cp;
	
	public StatusBar sb;
	
	public DProperties prefs;
	
	Scene<ArrayList<Geom>> scene;
	Scene<CSG> sceneCSG;
	
	FileWatcher fw;
	
	JSONCompiler tc = new JSONCompiler();
	
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
		
		rendermenu = new RenderMenu( this );
		menubar.add( rendermenu );
		
		rp = new QuickViewPanel();
		
		cp = new CodePanel( this );
		
		splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, cp, rp.gljpanel );
		container.getContentPane().add( splitPane, BorderLayout.CENTER );
		
		sb = new StatusBar();
		container.getContentPane().add( sb, BorderLayout.SOUTH );
		
		container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		container.setSize( 800, 600 );
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
	
	@SuppressWarnings( "unused" )
	public static void main( String[] args ) throws IOException
	{	
		TracePrint tp = new TracePrint();
	}

	public void changed( File f )
	{
		try
		{
			sb.fileName.setText( f.getAbsolutePath() );
			String text = DFile.loadText( f );
			tc.runParseThread( text, (s) -> { this.sceneCSG = s; this.scene = CSGHelper.convertSceneCSGtoRT( s ); rp.renderCSG( this.scene ); } );
			sb.startProgressThread( () -> tc.getProgress(), () -> tc.getMax() );
			cp.updateCode( text );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}

	public void exportSTL( File f )
	{
		try
		{
			DFile.saveText( f, sceneCSG.g.toStlString() );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}

	public void renderCurrent()
	{
		RenderFrame renderFrame = new RenderFrame( this, scene );
		renderFrame.setVisible( true );
	}
}
