// @Integer(label="Seconds between treatment and experiment start") treatmentExperimentDiff
// @String(label="Time unit for conversion", choices={"s", "min", "h"}) timeUnit
// @Integer(label="Shown decimal points of times") decimalPoints

/*
 * This macro reads metainformation about the "deltaT" from the file
 * of the currently open image. It uses Bio-Formats to get the
 * information about the time between the experiment start and the
 * acquisition start.
 * 
 * Stefan Helfrich (University of Konstanz), 07/14/2016
 */

// Enable Bio-Formats macro extensions
run("Bio-Formats Macro Extensions");

// Load file for the currently open image
openImagePath = getInfo("image.directory")+getInfo("image.filename")
Ext.setId(openImagePath);

// Extract position from the title of the open image
positionToLoad = parsePositionFromTitle(getTitle()) - 1; // index shift
Ext.setSeries(positionToLoad);

treatmentExperimentDiff = convertDuration(treatmentExperimentDiff, timeUnit);

// Obtain deltaT from the metadata
deltaT = 0;
Ext.getPlaneTimingDeltaT(deltaT, 0);
deltaT = convertDuration(deltaT, timeUnit);

// Obtain frame interval
frameInterval = Stack.getFrameInterval();
frameInterval = convertDuration(frameInterval, timeUnit);

// Make the timestamp
makeTimestamp(treatmentExperimentDiff+deltaT, frameInterval, timeUnit);

function convertDuration(duration, timeUnit) {
pick d77fca4 RandomAccessible: add definedBounds()
	if (timeUnit == "min") {
		return duration / 60;
	} else if (timeUnit == "h") {
		return duration / 3600;
	}

	return duration;
}

function makeTimestamp(start, interval, timeUnit) {
	run("Time Stamper", "starting=&start interval=&interval decimal=&decimalPoints anti-aliased or=&timeUnit");
}

function parsePositionFromTitle(title) {
	idx = lastIndexOf(title, "#");
	position = substring(title, idx+1, lengthOf(title));

	return parseInt(position);
}
