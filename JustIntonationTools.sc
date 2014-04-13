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
	* return the difference tones from an array of ratios
	*/
	*differenceToneRatios {|ratioArray|
		var dt = List();

		ratioArray.do{|denom,i|
			ratioArray.do{|num,j|
				if(num > denom, { dt.add( num - denom ) });
			};
		};

		dt = dt.sort;

		^dt;
	}

	/**
	* return the octave adjusted difference tones from an array of ratios
	*/
	*adjustedDifferenceToneRatios {|ratioArray|
		^JustIntonationTools.differenceToneRatios(ratioArray).adjustOctave;
	}

	/**
	* return the lowest difference tone from an array of ratios
	*/
	*lowestDifferenceTone {|ratioArray|
		^JustIntonationTools.differenceToneRatios(ratioArray)[0];
	}

	/**
	* return the octave adjusted lowest difference tone from an array of ratios
	*/
	*adjustedLowestDifferenceTone {|ratioArray|
		^JustIntonationTools.adjustedDifferenceToneRatios(ratioArray)[0];
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

	*midinotecents{|midi|
		var cents;

		cents = ((midi - midi.round) * 100).round;

		^(midi.midinote + cents);
	}

	/**
	* return an array of partial numbers from a set of ratios
	*/
	*partialsFromRatios{|array|
		var denoms, base;
		denoms = array.collect{|r| r.asRational.denominator};

		base = denoms.collect{|numB|
			denoms.collect{|numA|
				numA.asInteger.lcm(numB.asInteger);
			};
		}.flat.sort.reverse[0];

		^array*base;
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

	alterRatio {|alteration|
		^this.asRational.alterRatio(alteration);
	}

	midinotecents {
		^JustIntonationTools.midinotecents(this);
	}
}

+ SequenceableCollection {

	differenceToneRatios {
		^JustIntonationTools.differenceToneRatios(this);
	}

	adjustedDifferenceToneRatios {
		^JustIntonationTools.adjustedDifferenceToneRatios(this);
	}

	lowestDifferenceTone {
		^JustIntonationTools.lowestDifferenceTone(this);
	}

	adjustedLowestDifferenceTone {
		^JustIntonationTools.adjustedLowestDifferenceTone(this);
	}

	adjustOctave {
		^this.collect{|r| Lattice.adjustOctave( r ) };
	}

	midinotecents {
		^this.collect{|n| n.midinotecents; };
	}

	partialsFromRatios {
		^JustIntonationTools.partialsFromRatios(this);
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

// using lowestDifferenceTone to play the fundamental of a tetrad
(
{ Mix.new( SinOsc.ar([1,9/8,3/2,7/4] * 440,0,0.4).distort)
+ Saw.ar([1,9/8,3/2,7/4].lowestDifferenceTone * 55, 0.3)
}.play
)

(
{ Mix.new( SinOsc.ar([1,6/5,8/5,9/5] * 440,0,0.4).distort )
+ Saw.ar([1,6/5,8/5,9/5].lowestDifferenceTone * 55, 0.3)
}.play
)

*/