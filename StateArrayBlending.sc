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