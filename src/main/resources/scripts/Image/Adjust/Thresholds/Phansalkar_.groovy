#@ Img input
#@ OpService ops
#@ UIService uis
#@ Integer radius
#@ Double(value=0.25) k
#@ Double(value=0.5) r
#@OUTPUT Img output

import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.type.logic.BitType;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;

normalizedImg = ops.create().img(input, new FloatType())
ops.image().normalize(normalizedImg, input, new FloatType(0), null, new FloatType(0.0), new FloatType(1.0));

output = ops.create().img(input, new BitType());

// ops.threshold().localPhansalkarThreshold(output, input, new RectangleShape(3, false), new OutOfBoundsConstantValueFactory(new ShortType(0 as short)));
ops.threshold().localPhansalkarThreshold(output, normalizedImg, new RectangleShape(radius, false), new OutOfBoundsBorderFactory(), k, r);
