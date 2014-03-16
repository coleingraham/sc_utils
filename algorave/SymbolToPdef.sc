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
		^output;
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