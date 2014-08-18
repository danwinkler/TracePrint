package com.danwink.traceprint.ui;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

@SuppressWarnings( "serial" )
public class StatusBar extends JPanel
{
	JLabel fileName;
	JProgressBar pb;
	public StatusBar()
	{
		super();
		this.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
		this.setPreferredSize( new Dimension( 300, 16 ) );
		this.setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		
		fileName = new JLabel( "" );
		this.add( fileName );
		
		pb = new JProgressBar();
		pb.setPreferredSize( new Dimension( 150, 16 ) );
		this.add( pb );
	}
	
	public interface ProgressInterface
	{
		public int getProgress();
	}
	
	public interface MaxInterface
	{
		public int getMax();
	}
	
	public void startProgressThread( ProgressInterface pi, MaxInterface mi  )
	{
		Thread t = new Thread( new Runnable() {
			public void run()
			{
				while( true )
				{
					try
					{
						SwingUtilities.invokeAndWait( new Runnable() {
							public void run()
							{
								pb.setMinimum( 0 );
								pb.setMaximum( mi.getMax() );
								pb.setValue( pi.getProgress() );
							}
						});
					}
					catch( InvocationTargetException | InterruptedException e1 )
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try
					{
						Thread.sleep( 200 );
					}
					catch( InterruptedException e )
					{
						e.printStackTrace();
					}
					
					if( pb.getValue() == pb.getMaximum() && pb.getMaximum() != 0 )
					{
						break;
					}
				}
			}
		});	
		t.start();
	}
}
