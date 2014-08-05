package com.danwink.traceprint;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.phyloa.dlib.util.DFile;


public class CodeParser
{
	ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");
    
    public CodeParser( File f )
    {	
    	try 
    	{
    		Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
    		bindings.put( "api", this );
    		Scanner s = new Scanner( f );
	    	String js = s.useDelimiter("\\Z").next();
	    	s.close();
	    	
	    	engine.eval( DFile.loadText( "js/setup.js" ) );
	    	
			engine.eval( js );
    	} 
    	catch( IOException | ScriptException ex ) 
    	{
    		ex.printStackTrace();
    	} 
    }
}
