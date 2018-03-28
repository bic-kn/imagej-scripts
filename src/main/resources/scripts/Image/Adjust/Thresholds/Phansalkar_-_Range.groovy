#@ ModuleService modules
#@ DatasetService datasetService
#@ Dataset input
#@ Integer(label="Minimum Radius", value=5, min=1, max=10) radiusMin
#@ Integer(label="Maximum Radius", value=5, min=1, max=10) radiusMax
#@ Double(label="Minimum k", value=0.25, min=0.1, max=1.0, stepSize=0.1) kMin
#@ Double(label="Maximum k", value=0.25, min=0.1, max=1.0, stepSize=0.1) kMax
#@ Double(label="Minimum r", value=0.5, min=0.1, max=1.0, stepSize=0.1) rMin
#@ Double(label="Maximum r", value=0.5, min=0.1, max=1.0, stepSize=0.1) rMax
#@OUTPUT Dataset output

import net.imglib2.view.Views;

outs = [];
(radiusMin..radiusMax).each { radius ->
	(kMin*10..kMax*10).each { k ->
		k = k/10;
		(rMin*10..rMax*10).each { r ->
			r = r/10;
			// Run LoG script from menu
			moduleInfo = modules.getModuleById("script:Image/Adjust/Thresholds/Phansalkar_.groovy");
			module = modules.createModule(moduleInfo);
			// Mark the output as resolved after creation s.t. it is not shown
			module.resolveOutput("output");
			future = modules.run(module, true, ["input": input.getImgPlus(), "radius": radius as int, "k": k as double, "r": r as double]);
			future.get();
			outs.add(module.getOutput("output"));
		}
	}
}

// Create a stack from the individual invocations
outputTemp = Views.stack(outs);
output = datasetService.create(outputTemp);
output.setName("Phansalkar of "+input.getName());
