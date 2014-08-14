package com.danwink.traceprint;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import eu.mihosoft.vrl.v3d.CSG;

@SuppressWarnings( "serial" )
public class RenderFrame extends JFrame
{
	ImagePanel ip;
	ControlPanel cp;
	
	RayTracer rt;
	public CSG g;
	
	public RenderFrame( CSG g )
	{
		super();
		this.setSize( 800, 600 );
		
		ip = new ImagePanel();
		cp = new ControlPanel();
		
		this.add( ip, BorderLayout.CENTER );
		this.add( cp, BorderLayout.SOUTH );
		
		this.g = g;
	}
	
	
	public class ImagePanel extends JPanel
	{
		BufferedImage im;

		public ImagePanel()
		{
			super();
		}
		
		protected void paintComponent( Graphics g ) 
		{
			super.paintComponent(g);
			if( im != null ) g.drawImage( im, 0, 0, null );
		}
	}
	
	public class ControlPanel extends JPanel implements ActionListener
	{
		JButton start;
		JTextField width;
		JTextField height;
		
		public ControlPanel()
		{
			super();
			this.setLayout( new MigLayout() );
			
			start = new JButton( "Start" );
			start.setActionCommand( "start" );
			start.addActionListener( this );
			this.add( start );
			
			this.add( new JLabel( "Width" ), "gap unrelated" );
			width = new JTextField( "800" );
			this.add( width );
			
			this.add( new JLabel( "Height" ), "gap unrelated" );
			height = new JTextField( "600" );
			this.add( height );
		}

		public void actionPerformed( ActionEvent e )
		{
			switch( e.getActionCommand() )
			{
			case "start":
			{
				rt = new RayTracer( g, Integer.parseInt( width.getText() ), Integer.parseInt( height.getText() ) );
				ip.im = rt.im;
				rt.setup();
				new Thread( () -> {
					rt.render();
				}).start();
			}
			}
		}
	}
}
