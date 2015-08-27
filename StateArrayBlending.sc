/*
Given two arrays, both the same length, containint some data,
creates a 2D array of with each row being a combination of the
two input arrays, beginning with mostly the first and gragually
becoming mostly the second. .keep will generate each step with
the change from the previous step in the same place. .scramble
will generate each step with the changes in a random position.
Look at the output of the examples to get a clearer idea of what
this actually does.
*/
StateArrayBlending
{
	*scramble
	{|a,b|
		^StateArrayBlending.blendArray(a,b,StateArrayBlending.blendInterpScramble(a.size));
	}

	*keep
	{|a,b|
		^StateArrayBlending.blendArray(a,b,StateArrayBlending.blendInterpKeep(a.size));
	}

	*baseInterpArray
	{|slots|
		^(slots+1).collect{|i|
			Array.fill(slots,{|j|if(j<i,{1},{0})});
		};
	}

	*blendInterpScramble
	{|slots|
		^StateArrayBlending.baseInterpArray(slots).collect(_.scramble);
	}

	*blendInterpKeep{|slots|
		^StateArrayBlending.baseInterpArray(slots).flop.scramble.flop;
	}

	*blendArray
	{|a,b,interp|
		var set = [a,b];
		var out = Array.fill2D(interp.size,a.size,0);

		interp.do{|step,i|
			step.do{|which,j|
				out[i][j] = set[which][j];
			};
		};

		^out;
	}
}
/*
// start with [ a, a, a, a, a ]
// in between, each array will have one a replaced by a b
// until it ends with [ b, b, b, b, b ]
StateArrayBlending.keep(\a!5,\b!5).do(_.postln);

// same thing with the location of the changed value randomized
StateArrayBlending.scramble(\a!5,\b!5).do(_.postln);
*/


/*
Shortcut for using StateArrayBlending with patterns
*/
Pstateblend {
	*new
	{|a,b,type=\keep,num=4,repeats=1|
		var blend;
		var n = num.asStream.next;
		var t = true!n;
		var f = false!n;

		switch(type,
			\keep, { blend = StateArrayBlending.keep(t,f) },
			\scramble, { blend = StateArrayBlending.scramble(t,f) },
			{ ^"type must be either \keep or \scramble".error; }
		);

		^Pif(Pseq(blend.flat,repeats),a,b);
	}
}
/*
// continuously alternate back and forth between the two states
// with different blend arrays
(
Pbind(
	\dur,0.2,
	\degree, Pseq([
		Pstateblend(0,4,\scramble,5),
		Pstateblend(4,0,\scramble,6) // take longer to transition back
	],inf)
).play;
)
*/