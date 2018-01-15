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
//positionToLoad = parsePositionFromTitle(getTitle()) - 1; // index shift
//Ext.setSeries(positionToLoad);

Ext.getImageCount(imageCount);
print("Plane count: " + imageCount);

Ext.getZCTCoords(0, z, c, t)
print("z: "+z+" c:"+c+" t:"+t)

creationDate = "";
Ext.getImageCreationDate(creationDate);
print("Creation date: " + creationDate);

deltaT = newArray(imageCount);
print("Plane deltas (seconds since experiment began):");
currentT=0;
for (no=0; no<imageCount; no++) {
  Ext.getZCTCoords(no, z, c, t)
  if (t > currentT) {
  	Ext.getPlaneTimingDeltaT(deltaT[no], no);
  	if (deltaT[no] == deltaT[no]) { // not NaN
      Stack.setFrame(currentT);
      s = "\t" + (no + 1) + ": " + deltaT[no] + " s";
      Overlay.drawString(deltaT[no] + " s", 5, 20);
      Overlay.add;
	}
	print(s);
	currentT = t;
  }
}
print("Complete.");

function convertDuration(duration, timeUnit) {
	if (timeUnit == "min") {
		return duration / 60;
	} else if (timeUnit == "h") {
		return duration / 3600;
	}

	return duration;
}

function makeTimestamp(start, interval, timeUnit) {
	run("Label...", "format=00:00 starting=0 interval=1 x=5 y=20 font=18 text=s range=3-3 use");
//	run("Time Stamper", "starting=&start interval=&interval decimal=&decimalPoints anti-aliased or=&timeUnit");
}

function parsePositionFromTitle(title) {
	idx = lastIndexOf(title, "#");
	position = substring(title, idx+1, lengthOf(title));

	return parseInt(position);
}