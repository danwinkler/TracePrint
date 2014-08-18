package com.danwink.traceprint.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings( "serial" )
public class RenderMenu extends JMenu implements ActionListener
{	
	TracePrint tp;
	File f;
	
	public RenderMenu( TracePrint tp )
	{
		super( "Render" );
		this.setMnemonic( 'R' );
		
		JMenuItem currentViewMenu = new JMenuItem( "Current View", 'C' );
		currentViewMenu.setAccelerator( KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK ) );
		currentViewMenu.setActionCommand( "current" );
		currentViewMenu.addActionListener( this );
		this.add( currentViewMenu );
		
		this.tp = tp;
	}

	public void actionPerformed( ActionEvent e )
	{
		switch( e.getActionCommand() )
		{
		case "current":
			tp.renderCurrent();
			break;
		}
	}
}