
function union( a, b ) {
	return ["union", a, b]
}

function difference( a, b ) {
	return ["difference", a, b]
}

function intersect( a, b ) {
	return ["intersect", a, b]
}

function translate( x, y, z ) {
	return function( n ) {
		return ["translate", x, y, z, n];
	}
}

function box( x, y, z ) {
	return ["box", x, y, z];
}