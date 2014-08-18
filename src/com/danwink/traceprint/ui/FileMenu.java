package com.danwink.traceprint.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

@SuppressWarnings( "serial" )
public class FileMenu extends JMenu implements ActionListener
{	
	TracePrint tp;
	File f;
	
	public FileMenu( TracePrint tp )
	{
		super( "File" );
		this.setMnemonic( 'F' );
		
		JMenuItem newMenu = new JMenuItem( "New", 'N' );
		newMenu.setAccelerator( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK ) );
		newMenu.setActionCommand( "new" );
		newMenu.addActionListener( this );
		this.add( newMenu );

		JMenuItem openMenu = new JMenuItem( "Open", 'O' );
		openMenu.setAccelerator( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK ) );
		openMenu.setActionCommand( "open" );
		openMenu.addActionListener( this );
		this.add( openMenu );

		JMenuItem saveMenu = new JMenuItem( "Save", 'S' );
		saveMenu.setAccelerator( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK ) );
		saveMenu.setActionCommand( "save" );
		saveMenu.addActionListener( this );
		this.add( saveMenu );

		JMenuItem saveAsMenu = new JMenuItem( "Save As...", 'A' );
		saveAsMenu.setActionCommand( "saveas" );
		saveAsMenu.addActionListener( this );
		this.add( saveAsMenu );
		
		this.add( new JSeparator( JSeparator.HORIZONTAL ) );
		
		JMenuItem exportStlMenu = new JMenuItem( "Export as STL...", 'E' );
		exportStlMenu.setActionCommand( "exportstl" );
		exportStlMenu.addActionListener( this );
		this.add( exportStlMenu );
		
		this.tp = tp;
	}

	public void actionPerformed( ActionEvent e )
	{
		switch( e.getActionCommand() )
		{
		case "new":
			tp.clear();
			f = null;
			break;
		case "open":
		{
			JFileChooser ofc = new JFileChooser();
			ofc.setCurrentDirectory( new File( System.getProperty("user.dir") ) );
			int returnVal = ofc.showOpenDialog( tp.container );
			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				tp.load( ofc.getSelectedFile() );
				f = ofc.getSelectedFile();
				tp.prefs.put( "file", f.getAbsolutePath() );
			}
			break;
		}
		case "save":
		{
			if( f != null )
			{
				saveFile( f );
				break;
			}
			//If f IS null, roll over to saveas handler
		}
		case "saveas":
		{
			JFileChooser sfc = new JFileChooser();
			sfc.setCurrentDirectory( new File( System.getProperty("user.dir") ) );
			int returnVal = sfc.showSaveDialog( tp.container );
			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				saveFile( sfc.getSelectedFile() );
			}
			break;
		}
		case "exportstl":
		{
			JFileChooser sfc = new JFileChooser();
			sfc.setCurrentDirectory( f != null ? f.getParentFile() : new File( System.getProperty("user.dir") ) );
			int returnVal = sfc.showSaveDialog( tp.container );
			if( returnVal == JFileChooser.APPROVE_OPTION ) {
				tp.exportSTL( sfc.getSelectedFile() );
			}
			break;
		}
		}
	}
	
	public void saveFile( File file )
	{
		tp.save( file );
		f = file;
	}
}