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
	value wrapped inside a Pseq([],inf). This requires that all values (even solatary ones)
	in the Event be enclosed in an array. Beyond that, all valid SuperCollider code works
	as per usual.
	*/
	*eventToPdef
	{|patternName,event|
		var output = "";
		var pbind = "";

		event.keys.do{|key|
			pbind = pbind ++ key ++ ":Pseq(" ++ event[key].asCompileString ++",inf),";
		};

		output = "Pdef(\\" ++ patternName ++ ",Pbind(*[" ++ pbind ++ "]));";
		output.interpret;
		^output;
	}
}

/*
EXAMPLES

// create out Pdef(\pat) by using the +> operator to assign an Event to it
\pat+>(instrument:["default"],dur:[0.5,0.25,0.25]*0.5,type:[Pwrand([\note,\rest],[0.8,0.2])],freq:[440,550,660,880]);

// play it like any other Pdef
Pdef(\pat).play;

A few things to note:
-all values inside the event MUST be inside an array like

instrument:["default"]

since they are automattically wrapped in a Pseq

-you can do math on the elements just like normal like

dur:[0.5,0.25,0.25]*0.5)

however, if you wanted to say multiply by a Pseq of values, you would need to wrap that inside the outer array as well like

dur:[Pseq([0.5,0.25,0.25])*Pseq([0.5,1])]

-to use Patterns in the Event, wrap them in the array for the value like

type:[Pwrand([\note,\rest],[0.8,0.2])]
*/