/*
 * A simple 3D cube panner.
 *
 * Outputs are: LeftFrontTop, RightFrontTop, LeftBackTop, RightBackTop,
 * LeftFrontBottom, RightFrontBottom, LeftBackBottom, RightBackBottom
 *
 * signal: the signal to be panned
 * panX: x-axis panning (-1 to 1 = left to right)
 * panY: y-axis panning (-1 to 1 = back to front)
 * panZ: z-axis panning (-1 to 1 = bottom to top)
 * amp: ampliude scalar
 */
CubePan
{
	*ar
	{|signal,panX=0,panY=0,panZ=0,amp=1|
		
		panZ = panZ.lincurve(-1,1,0,1,-4);	// make it equal power
		
		^[
			Pan4.ar(signal,panX,panY,panZ * amp),		// top four
			Pan4.ar(signal,panX,panY,(1-panZ) * amp )	// bottom four
		].flat;
	}
	
	*kr
	{|signal,panX=0,panY=0,panZ=0,amp=1|
		
		panZ = panZ.lincurve(-1,1,0,1,-4);	// make it equal power
		
		^[
			Pan4.kr(signal,panX,panY,panZ * amp),		// top four
			Pan4.kr(signal,panX,panY,(1-panZ) * amp )	// bottom four
		].flat;
	}
}