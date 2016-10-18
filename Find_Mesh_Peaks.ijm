/*
 * Marks peaks in a regular structure with a multi-point selection.
 *
 * Stefan Helfrich (University of Konstanz), 05/30/2016
 */

// Import file
filePath = File.openDialog("Select input file")
run("Bio-Formats", "open=[&filePath] autoscale color_mode=Grayscale view=Hyperstack stack_order=XYCZT");

// Minimum projection in z-direction
run("Z Project...", "projection=[Min Intensity]");

// Invert image (s.t. Find Maxima works properly)
run("Invert", "stack");

// Find points with maximum value in neighborhood
run("Find Maxima...", "noise=15 output=[Point Selection]");

// Get intensity values and coordinates
run("Measure");
