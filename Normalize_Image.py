# @Dataset data
# @OpService ops
# @OUTPUT Img normalized

# Create normalized image to the [0, 1] range.
#
# Stefan Helfrich (University of Konstanz), 03/10/2016

from net.imglib2.type.numeric.real import FloatType
from net.imglib2.type.numeric.integer import ByteType
from net.imagej.ops import Ops

normalized = ops.create().imgPlus(data.getImgPlus(), data.getImgPlus());
normalized.setName("normalized");
normalizeOp = ops.op(Ops.Image.Normalize, normalized, data.getImgPlus(), None, None, FloatType(0.0), FloatType(1.0));

ops.slicewise(normalized, data.getImgPlus(), normalizeOp, [0,1], False);
