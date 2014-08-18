package com.danwink.traceprint.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

@SuppressWarnings( "serial" )
public class CodePanel extends JPanel implements ActionListener
{
	TracePrint tp;
	
	RSyntaxTextArea textArea;
	RTextScrollPane sp;
	
	public CodePanel( TracePrint tp )
	{
		super();
		this.tp = tp;
		
		this.setLayout( new BorderLayout() );
		
		textArea = new RSyntaxTextArea( 20, 60 );
		textArea.setSyntaxEditingStyle( SyntaxConstants.SYNTAX_STYLE_JSON );
		textArea.setCodeFoldingEnabled( true );
		sp = new RTextScrollPane( textArea );
		
		this.add( sp );
	}

	public void actionPerformed( ActionEvent e )
	{
		
	}

	public void updateCode( String code )
	{
		textArea.setText( code != null ? code : "" );
	}
}
