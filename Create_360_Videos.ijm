/*
 * The macro creates roation videos of reconstructed SIM data. Input
 * data is read from a directory, AVIs are saved to a defined output
 * directory.
 * 
 * Requires a working 3D Viewer in ImageJ.
 *
 * Stefan Helfrich (University of Konstanz), 04/13/2016
 */

inputDirectory = getDirectory("Choose input directory ");
outputDirectory = getDirectory("Choose output directory ");
inputFiles = getFileList(inputDirectory);

setBatchMode(true);
for (i=0; i<inputFiles.length; i++) {
  showProgress(i+1, inputFiles.length);
  if (!endsWith(inputFiles[i], "SIR.dv")) {
  	continue;
  }

  run("Bio-Formats (Windowless)", "open='"+inputDirectory+inputFiles[i]+"'");
  run("Green");
  title = getTitle();
  create360Video(title);

	selectWindow("Movie");
	run("AVI... ", "compression=JPEG frame=10 save='"+outputDirectory+replace(inputFiles[i],"dv","avi")+"'");
	
	// Close movie
	close();
	
	// Close input image
	selectWindow(title);
	close();
	
	// Close viewer
	call("ij3d.ImageJ3DViewer.close");
}

function create360Video(imageTitle) {
	selectWindow(imageTitle);

	getStatistics(area, mean, min, max);
	//run("Brightness/Contrast...");
	setMinAndMax(1000.0, max);
	run("8-bit");
	//setTool("hand");
	run("3D Viewer");
	call("ij3d.ImageJ3DViewer.setCoordinateSystem", "false");
	call("ij3d.ImageJ3DViewer.add", imageTitle, "Green", imageTitle, "35", "false", "true", "false", "2", "2");
	call("ij3d.ImageJ3DViewer.record360");
}
