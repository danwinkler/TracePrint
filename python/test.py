import json

from traceprint import *

write_out( "test.json", 
	union( 
		box( 1, 1, 1 ), 
		translate( 3, 0, 0 )( 
			box( 1, 1, 2 ) 
		) 
	) 
)