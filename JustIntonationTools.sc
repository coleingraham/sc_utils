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
	*alteredRatio{|ratio, alteration|
		^( (ratio.numerator + (alteration)) /% (ratio.denominator + (alteration)) );
	}
}

////////////////////////////////////////////////////////////////
// extensions that do the same thing as JustIntonationTools   //
////////////////////////////////////////////////////////////////

+ Rational {

	oddLimit {
		^JustIntonationTools.oddLimit(this);
	}

	primeLimit {
		^JustIntonationTools.primeLimit(this);
	}

	alteredRatio{|alteration|
		^JustIntonationTools.alteredRatio(this,alteration);
	}
}

+ Float {

	oddLimit {
		^this.asRational.oddLimit;
	}

	primeLimit {
		^this.asRational.primeLimit;
	}

	alteredRatio{|alteration|
		^this.asRational.alteredRatio(alteration);
	}
}

+ Array {
	differenceToneRatio {
		^JustIntonationTools.differenceToneRatio(this);
	}
}