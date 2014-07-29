import json

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
		return ["translate", float(x), float(y), float(z), union( args )]
	return func

#Primitives
def box( x, y, z ):
	return ["box", float(x), float(y), float(z)]

#Modifiers
def color( r, g, b ):
	def func( *args ):
		return ["color", float(x), float(y), float(z), union( args )]
	return func

#Helpers	
def write_out( filename, object ):
	with open( filename, "w" ) as f:
		f.write( json.dumps( object ) )