/*
Container for serial music like 2D matricies.
There is, however, no restriction on size (e.g.
you can have more than 12 tones/indecies/etc.)
*/
SerialistMatrix {
	var <mat;

	*new {|row|
		^super.new.init(row);
	}

	init {|p0|
		var i0;
		i0 = (p0.size - p0) % p0.size;
		mat = i0.collect{arg n;(n + p0) % p0.size;};
	}

	/*
	Get the size of the matrix
	*/
	size {
		^mat[0].size;
	}

	/*
	Print out the matrix in a nicely formatted way
	*/
	show {
		mat.do{|row|
			row.do{|item|
				if(item.asString.size == 1,{
					(item.asString ++ "  ").post;
					},{
						(item.asString ++ " ").post;
				});
			};
			"".postln;
		};
	}

	/*
	Return the row which begins from n
	*/
	p {|n|
		if(n > mat.size,{ "index is larger than matrix".warn; },{
			mat.do{|row|
				if(row[0] == n,{ ^row; });
			};
		});
	}

	/*
	Return the reverse of the row which begins from n
	*/
	r {|n|
		if(n > mat.size,{ "index is larger than matrix".warn; },{
			^this.p(n).reverse;
		});
	}

	/*
	Return the column which begins from n
	*/
	i {|n|
		if(n > mat.size,{ "index is larger than matrix".warn; },{
			mat.flop.do{|row|
				if(row[0] == n,{ ^row; });
			};
		});
	}

	/*
	Return the reverse of the column which begins from n
	*/
	ri {|n|
		if(n > mat.size,{ "index is larger than matrix".warn; },{
			^this.i(n).reverse;
		});
	}
}