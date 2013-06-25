JustIntonationTools {

	*differenceToneRatio {|ratioArray|
		var dt = List();

		ratioArray.do{|denom,i|
			ratioArray.do{|num,j|
				if(num > denom, { dt.add( num - denom ) });
			};
		};

		dt = dt.sort;
		^Lattice.adjustOctave( dt[0] );
	}
}