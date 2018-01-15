#@OpService opService
#@UIService uiService
#@Double(label="Sigma (Radius)", value=3.0) sigma
#@Boolean(label="Show kernel") showLabel
#@Img input
#@OUTPUT Img output

/**
 * Computes the Laplacian of Gaussian (LoG) of an input image where
 * sigma defines the kernel radius.
 */

// Use sigma for all dimensions of the input image
sigmas = new double[input.numDimensions()];
(0..sigmas.length-1).each {
	sigmas[it] = sigma;
}
// Create the kernel
kernel = opService.create().kernelLog(sigmas);

// Convolve the input image with the kernel
output = opService.filter().convolve(input, kernel);

// Show the kernel if the box was ticked
if (showLabel) uiService.show(kernel);
