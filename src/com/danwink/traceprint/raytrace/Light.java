package com.danwink.traceprint.raytrace;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Light
{
	public Type type;
	public Point3f pos;
	public Vector3f dir;
	
	public static enum Type
	{
		POINT,
		DIRECTIONAL,
		SPOT
	}
}
