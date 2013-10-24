/*
* SternBrocotTree
*
* a basic implementation of a Stern-Brocot tree
*/

SternBrocotTree {

	* areAdjacent {|r,s|
		var  value;

		value = (r.asRational.numerator * s.asRational.denominator) - (r.asRational.denominator * s.asRational.numerator);

		if(value.abs == 1,{^true},{^false});
	}

	* left {|mat,steps=1|
		^mat * Matrix.with([[1,0],[steps,1]]);
	}

	* right {|mat,steps=1|
		^mat * Matrix.with([[1,steps],[0,1]]);
	}

	* up {|continuedFraction|
		var output;

		output = continuedFraction.copy;

		if(output.last == 1,{
			^output.keep(output.size-1);
		},{
				output[output.size-1] = output[output.size-1]-1;
				^output;
		});
	}

	* asRational {|mat|
		^Rational( mat.asArray[0].sum / mat.asArray[1].sum );
	}

	// adapted from code by Nick Collins on the sc users list
	* asContinuedFraction {|ratio,n=inf|
		var continuedFraction = List();
		var tmp;
		var now = 0;

		tmp = ratio;

		block{|break|

			n.do{
				now = floor(tmp);
				continuedFraction.add(now.asInteger);
				now = tmp - now;
				//if reached zero
				if(now<0.00000001,{break.()});
				tmp = now.reciprocal;
			};

		};

		continuedFraction[continuedFraction.size-1] = continuedFraction[continuedFraction.size-1]-1;

		if(continuedFraction[continuedFraction.size-1] == 0,{
			continuedFraction.remove(continuedFraction[continuedFraction.size-1])
		});

		if(continuedFraction.size == 0,{continuedFraction.add(0)});

		^continuedFraction;
	}

	* matrixFromRatio {|ratio|
		^SternBrocotTree.fromContinuedFraction(SternBrocotTree.asContinuedFraction(ratio));
	}

	* postPath {|continuedFraction|
		var moveRight = true;

		continuedFraction.do{|steps|
			if(moveRight,{
				"R: %".format(steps).postln;
				},{
					"L: %".format(steps).postln;
			});
			moveRight = moveRight.not;
		};
	}

	* fromContinuedFraction {|continuedFraction|
		var moveRight = true;
		var mat;

		mat = Matrix.newIdentity(2);

		continuedFraction.do{|steps|

			steps.do{
				if(moveRight,{
					mat = SternBrocotTree.right(mat);
					},{
						mat = SternBrocotTree.left(mat);
				});
			};
			moveRight = moveRight.not;
		};

		^mat;
	}

	* fibPath {|continuedFraction,n=1|
		var fib;
		var tmp;

		fib = List();
		tmp = continuedFraction.copy;
		fib.add(continuedFraction.copy);

		n.do{
			tmp.add(1);
			fib.add(tmp.copy);
		}

		^fib;
	}

	// return the ratio on the opposite side of the axis or itself if the axis is incorrect
	* mirrorAround {|ratio,axis|
		var index;
		var target;

		index = axis.size-1;
		target = axis[index];
		ratio[index] = [target,ratio[index]-target];

		^ratio.flat;
	}

	// return the generation (number of steps down from 1:1) of the input
	* generation {|continuedFraction|
		^continuedFraction.sum;
	}

	* complexity {|ratio|
		^(ratio.numerator * ratio.denominator);
	}

	* simplicity {|ratio|
		^SternBrocotTree.complexity(ratio).reciprocal;
	}

	* rightFirst {|continuedFraction|
		^(continuedFraction[0] = 1);
	}

	* leftFirst {|continuedFraction|
		^(continuedFraction[0] = 0);
	}
}

//////////////////////////////////////////
// extensions that wrap SternBrocotTree //
//////////////////////////////////////////

+ Rational {

	asContinuedFraction {|n=inf|
		^SternBrocotTree.asContinuedFraction(this,n);
	}

	matrixFromRatio {
		^SternBrocotTree.matrixFromRatio(this);
	}

	sbGeneration {
		^SternBrocotTree.generation(this.asContinuedFraction);
	}

	sbMirrorAround {|axis|
		^this.asContinuedFraction.sbMirrorAround(axis.asContinuedFraction).fromContinuedFraction.asRational;
	}

	sbUp {
		^this.asContinuedFraction.sbUp.fromContinuedFraction.asRational;
	}

	sbLeft {|steps=1|
		^this.matrixFromRatio.sbLeft(steps).asRational;
	}

	sbRight {|steps=1|
		^this.matrixFromRatio.sbRight(steps).asRational;
	}

	sbLeftFirst {
		^this.asContinuedFraction.sbLeftFirst.fromContinuedFraction.asRational;
	}

	sbRightFirst {
		^this.asContinuedFraction.sbRightFirst.fromContinuedFraction.asRational;
	}
}

+ SimpleNumber {

	asContinuedFraction {|n=inf|
		^SternBrocotTree.asContinuedFraction(this.asFloat,n);
	}

	matrixFromRatio {
		^SternBrocotTree.matrixFromRatio(this);
	}

	sbGeneration {
		^SternBrocotTree.generation(this.asContinuedFraction);
	}

	sbMirrorAround {|axis|
		^this.asRational.sbMirrorAround(axis).asFloat;
	}

	sbUp {
		^this.asRational.sbUp.asFloat;
	}

	sbLeft {|steps=1|
		^this.asRational.sbLeft(steps).asFloat;
	}

	sbRight {|steps=1|
		^this.asRational.sbRight(steps).asFloat;
	}

	sbLeftFirst {
		^this.asRational.sbLeftFirst.asFloat;
	}

	sbRightFirst {
		^this.asRational.sbRightFirst.asFloat;
	}
}

+ SequenceableCollection {

	fromContinuedFraction {
		^SternBrocotTree.fromContinuedFraction(this);
	}

	sbMirrorAround {|axis|
		^SternBrocotTree.mirrorAround(this,axis);
	}

	sbUp {
		^SternBrocotTree.up(this);
	}

	sbLeftFirst {
		^SternBrocotTree.leftFirst(this);
	}

	sbRightFirst {
		^SternBrocotTree.rightFirst(this);
	}
}

+ Matrix {
	asRational {
		^SternBrocotTree.asRational(this);
	}

	sbLeft {|steps=1|
		^SternBrocotTree.left(this,steps);
	}

	sbRight {|steps=1|
		^SternBrocotTree.right(this,steps);
	}
}