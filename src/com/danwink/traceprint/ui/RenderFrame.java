package com.danwink.traceprint.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.danwink.traceprint.raytrace.RayTracer;
import com.danwink.traceprint.raytrace.Scene;
import com.phyloa.dlib.math.Geom;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings( "serial" )
public class RenderFrame extends JFrame
{
	TracePrint tp;
	
	ImagePanel ip;
	ControlPanel cp;
	
	RayTracer rt;
	public Scene<ArrayList<Geom>> scene;
	
	public RenderFrame( TracePrint tp, Scene<ArrayList<Geom>> scene )
	{
		super();
		this.setSize( 800, 600 );
		
		ip = new ImagePanel();
		cp = new ControlPanel();
		
		this.add( ip, BorderLayout.CENTER );
		this.add( cp, BorderLayout.SOUTH );
		
		this.scene = scene;
		this.tp = tp;
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
				rt = new RayTracer( scene, Integer.parseInt( width.getText() ), Integer.parseInt( height.getText() ) );
				rt.camera( tp.rp.lookat );
				ip.im = rt.im;
				new Thread( () -> {
					rt.render();
					ip.repaint();
				}).start();
			}
			}
		}
	}
}
