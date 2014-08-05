import json
import os

#Transform
def union( *args ):
	ret = ["union"]
	for a in args:
		ret.append( a )
	return ret

def difference( a, b ):
	return ["difference", a, b]
	
def intersection( *args ):
	ret = ["intersection"]
	for a in args:
		ret.append( a )
	return ret

def translate( x, y, z ):
	def func( *args ):
		return ["translate", float(x), float(y), float(z), union( *args )]
	return func

#Primitives
def box( x, y, z ):
	return ["box", float(x), float(y), float(z)]

def sphere( r, slices=16, stacks=8 ):
	return ["sphere", float(r), int(slices), int(stacks)]

def cylinder( r, height, slices=8 ):
	return ["cylinder", float(r), float(height), int(slices)]
	
def stl( filepath, abspath=True ):
	return ["stl", os.path.abspath( filepath ) if abspath else filepath]

def polyhedron( points, faces ):
	return ["polyhedron", points, faces]
	
#Modifiers
def color( r, g, b ):
	def func( *args ):
		return ["color", float(r), float(g), float(b), union( *args )]
	return func

#Helpers	
def write_out( filename, object ):
	with open( filename, "w" ) as f:
		f.write( json.dumps( object, indent=3 ) )

def from_solid( obj ):
	