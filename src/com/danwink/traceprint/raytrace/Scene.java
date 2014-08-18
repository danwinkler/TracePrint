package com.danwink.traceprint.raytrace;

import java.util.ArrayList;

public class Scene<Geometry>
{
	public Geometry g;
	public ArrayList<Light> lights;
	
	public Scene( Geometry g, ArrayList<Light> lights )
	{
		this.g = g;
		this.lights = lights;
	}
}
