package com.danwink.traceprint;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel
{
	JLabel fileName;
	public StatusBar()
	{
		super();
		this.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
		this.setPreferredSize( new Dimension( 100000, 16 ) );
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		
		fileName = new JLabel( "" );
		this.add( fileName );
	}
}
