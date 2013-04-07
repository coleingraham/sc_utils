MIDIGuitar
{
	var tuning, <strings;

	*new
	{|tuning|
		^super.newCopyArgs(tuning).init;
	}

	init
	{
		if(tuning.isNil,{
			tuning = Array.fill(12,0);
		});

		// allow setting single tuning or tuning per string
		if(tuning[0].size == 6,{
			strings = Array.fill(6,{|i| MIDIGuitarNote(0,0,0.0,tuning[i]) });
		},{
			strings = Array.fill(6,{ MIDIGuitarNote(0,0,0.0,tuning) });
		});


	}

	setTuning
	{|newTuning|
		tuning = newTuning;

		if(tuning[0].size == 6,{
			strings.do{|string,i| string.tuning = tuning[i]};
		},{
			strings.do{|string| string.tuning = tuning};
		});
	}
}

MIDIGuitarNote
{
	var <>pitch, <>velocity, <>bend, <>tuning;

	*new
	{|pitch=0,velocity=0,bend=0.0,tuning|
		^super.newCopyArgs(pitch,velocity,bend,tuning).init;
	}

	init
	{
		if(tuning.isNil,{
			tuning = Array.fill(12,0);
		});
	}

	tunedPitch
	{
		var note;
		note = pitch + bend;
		^(note + tuning.blendAt(note%12).round(0.01));
	}

	cents
	{
		var note;
		note = pitch + bend;
		^(((bend-bend.floor) + tuning.blendAt(note%12,'wrapAt').round(0.01)));
	}
}