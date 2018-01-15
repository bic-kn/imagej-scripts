#@ModuleService modules
#@DatasetService datasetService
#@Double(label="Min. sigma", min=1.0, max=20.0, stepsize=1.0, value=3.0, style="slider") minSigma
#@Double(label="Max. sigma", min=1.0, max=20.0, stepsize=1.0, value=3.0, style="slider") maxSigma
#@Dataset input
#@OUTPUT Dataset output

import net.imglib2.view.Views;

/**
 * Computes the LoG with the sigmas defined by the minimum and maxium
 * values. The output is a stack where the first slice corresponds to
 * the minimal sigma and the last one to the defined maximum.
 */

outs = [];
(minSigma..maxSigma).each{
	// Run LoG script from menu
	moduleInfo = modules.getModuleById("script:Process/Filters/Laplacian_of_a_Gaussian_(LoG).groovy");
	module = modules.createModule(moduleInfo);
	// Mark the output as resolved after creation s.t. it is not shown
	module.resolveOutput("output");
	future = modules.run(module, true, ["input": input.getImgPlus(), "sigma": it, "showLabel": false])
	future.get();
	outs.add(module.getOutput("output"));
}

// Create a stack from the individual LoGs of different sigmas
outputTemp = Views.stack(outs);
output = datasetService.create(outputTemp);
output.setName("LoG of "+input.getName());
