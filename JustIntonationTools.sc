/**
 * JustIntonationTools
 * by Cole Ingraham (www.coleingraham.com)
 * Various utility methods for working with Just Intonation
 * DEPENDS ON:
 *  -TuningLib quark
 *  -ddwCommon quark
 */
JustIntonationTools {

	/**
	 * return the lowest difference tone from an array of frequencies or ratios
	 */
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

	/**
	 * return the odd limit (highest odd number) in the ratio
	 */
	*oddLimit {|ratio|
		var num = ratio.asRational.numerator.asInteger;
		var den = ratio.asRational.denominator.asInteger;

		if( num.odd ,{
			^num;
			},{
				^den;
		});
	}

	/**
	 * return the prime limit (highest prime number) in the ratio
	 */
	*primeLimit {|ratio|
		var num = ratio.asRational.numerator.asInteger;
		var den = ratio.asRational.denominator.asInteger;

		var numLimit = num.factors.sort.reverse[0];
		var denLimit = den.factors.sort.reverse[0];

		if( numLimit > denLimit ,{
			^numLimit;
			},{
				^denLimit;
		});
	}

	/**
	 * return the ratio altered by a fraction (useful for finding nearby ratios)
	 */
	*alterRatio{|ratio, alteration|
		^( (ratio.numerator + (alteration)) / (ratio.denominator + (alteration)) );
	}
}

//////////////////////////////////////////////
// extensions that wrap JustIntonationTools //
//////////////////////////////////////////////

+ Rational {

	oddLimit {
		^JustIntonationTools.oddLimit(this);
	}

	primeLimit {
		^JustIntonationTools.primeLimit(this);
	}

	alterRatio{|alteration|
		^JustIntonationTools.alterRatio(this,alteration);
	}
}

+ Float {

	oddLimit {
		^this.asRational.oddLimit;
	}

	primeLimit {
		^this.asRational.primeLimit;
	}

	alterRatio{|alteration|
		^this.asRational.alterRatio(alteration);
	}
}

+ Array {
	differenceToneRatio {
		^JustIntonationTools.differenceToneRatio(this);
	}
}

/*
//////////////
// examples //
//////////////

// alter a 3/2 by 1/n where n is the first 4 primes.
// 1/2 gives the next lower "step," smaller alterations move closer to initial ratio
(
(0..3).nthPrime.do{|n|
	(3/2).alterRatio(1/n).asRational.postln;
}
)

// alter a 3/2 by (n-1)/n where n is the first 4 primes.
// 1/2 gives the next lower "step," larger alterations move closer to the further ratio
(
(0..3).nthPrime.do{|n|
	(3/2).alterRatio((n-1)/n).asRational.postln;
}
)

// identical results to (n-1)/n but from the smaller ratio
(
(0..3).nthPrime.do{|n|
	(4/3).alterRatio(-1/n).asRational.postln;
}
)

*/