/*
A quick way to create what I normally do for a wobble bass
Assumes that both wubs and shreaks are arrays
*/
Pwub
{
	*new
	{|wubs,shreaks|
		var wubStates = (0..wubs.size-1);
		var stateArray = List();

		// start with any of the wubs
		stateArray.add(wubStates);

		// add all the wubs and make them most likely to go to eachother
		wubs.do({|name|
			stateArray.add(name);
			stateArray.add(wubStates.stutter(3) ++ wubs.size);
		});

		// add the shreaks
		stateArray.add(Prand(shreaks));
		// make the shreaks most likely to go back to the wubs
		stateArray.add(wubStates.stutter(4) ++ wubs.size);

		^Pfsm(stateArray.asArray,inf);
	}

	// a generally useful set of lfo rates
	*lfo
	{
		^Pwrand([0.5,1,2/3,2,3,4,6,8], [1,2,2,3,4,3,1,1].normalizeSum,inf)
	}
}

/*
Convert a string with a Lich style rhythm a Pseq
Stands for P Layout/Lich Rhythm
*/
Plr
{
	*new
	{|str|
		^Pseq(str.asLayout,inf);
	}
}


/*
A more concise way to create Pdefs for live coding.
*/
+ Symbol
{
	/*
	Our magic operator.

	example:
	\test +> (dur:[0.5,1]);

	will interpret as

	Pdef(\test,Pbind(*[dur:Pseq([ 0.5, 1 ],inf),]));
	*/
	+>
	{|event|
		^Symbol.eventToPdef(this,event);
	}

	/*
	Create a Pdef named after our Symbol. The Event is converted into a Pbind with each
	value wrapped inside a Pseq([],inf). Beyond that, all valid SuperCollider code works
	as per usual.
	*/
	*eventToPdef
	{|patternName,event|
		var output = "";
		var pbind = "";

		if(event.isKindOf(Event).not,{
			"% +> did not receive an Event".format(patternName).error;
			^patternName;
		});

		event.keys.do{|key|
			var value = event[key];

			if(value.isKindOf(Array),{
				value = "Pseq(" ++ value.asCompileString ++ ",inf)";
				},{
					value = value.asCompileString;
			});

			// allow inst as a shortcut for instrument
			if(key.asString == "inst",{ key = "instrument" });

			pbind = pbind ++ key ++ ":" ++ value ++",";
		};

		output = "Pdef(\\" ++ patternName ++ ",Pbind(*[" ++ pbind ++ "]));";
		output = output.replace(" ","");
		output.interpret;
		^Pdef(patternName);
	}

	/*
	Quick way to play the resulting Pdef.
	TODO: pass all possible arguments correctly
	*/
	play
	{|argClock, quant|
		if(argClock.notNil && quant.notNil,{ ^Pdef(this).play(argClock, quant:quant); });
		if(argClock.notNil,{ ^Pdef(this).play(argClock); });
		if(quant.notNil,{ ^Pdef(this).play(quant:quant); });
		^Pdef(this).play();
	}

	/*
	Quick way to stop the resulting Pdef.
	*/
	stop
	{
		^Pdef(this).stop;
	}

	/*
	Quick way to call Pdef().player
	*/
	player
	{
		^Pdef(this).player;
	}

	pause
	{
		^Pdef(this).pause;
	}

	resume
	{|argClock, quant|
		if(argClock.notNil && quant.notNil,{ ^Pdef(this).resume(argClock, quant:quant); });
		if(argClock.notNil,{ ^Pdef(this).resume(argClock); });
		if(quant.notNil,{ ^Pdef(this).resume(quant:quant); });
		^Pdef(this).resume();
	}

	reset
	{
		^Pdef(this).reset;
	}

	/*
	Same as Pdef().player.mute
	*/
	mute
	{
		^Pdef(this).player.mute;
	}

	/*
	Same as Pdef().player.unmute
	*/
	unmute
	{
		^Pdef(this).player.unmute;
	}

	/*
	Wrapper for setting one value with Pbindf
	*/
	set
	{|key, value|

		if(value.isKindOf(String),{ value = Plr(value); }); // handle Lich style layout patterns

		^Pbindef(this,key.asSymbol,value);
	}

	/*
	Same as Pdef().quant
	*/
	quant
	{|q|
		^Pdef(this).quant;
	}

	/*
	Same as Pdef().quant_()
	*/
	quant_
	{|q|
		^Pdef(this).quant_(q);
	}

}

+ String
{
	/*
	Pseudo-Lich style layout patterns
	*/
	asLayout
	{|multiplier=1|
		var str,output,dur;
		output = List();
		str = this;
		str = str.split($ );
		str = str.do({|beat|
			beat = beat.stripWhiteSpace;

			dur = (beat.size*2).reciprocal;

			beat.do{|char|
				switch(
					char,
					$_, { output.add(Rest(dur*multiplier)) },
					$x, { output.add(dur*multiplier) }
				);
			};
		});

		^output.flat.asArray;
	}

	asPseq
	{|repeats=1,multiplier=1|
		^Pseq(this.asLayout(multiplier),repeats);
	}
}

/*
EXAMPLES

// create out Pdef(\pat) by using the +> operator to assign an Event to it
\pat+>(instrument:"default",dur:[0.5,0.25,0.25]*0.5,type:[Pwrand([\note,\rest],[0.8,0.2])],freq:[440,550,660,880]);

// play it like any other Pdef
Pdef(\pat).play;

// or play from the Symbol directly
\pat.play;

A few things to note:
-you can do math on the elements just like normal like

dur:[0.5,0.25,0.25]*0.5)

however, if you wanted to say multiply by a Pseq of values, you would need to wrap that inside the outer array as well like

dur:[Pseq([0.5,0.25,0.25])*Pseq([0.5,1])]

-to use Patterns in the Event, wrap them in the array for the value like

type:[Pwrand([\note,\rest],[0.8,0.2])]
*/