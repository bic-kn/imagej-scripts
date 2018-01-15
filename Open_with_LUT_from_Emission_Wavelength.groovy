#@File(label="Input file") input
#@LogService logService

/*
 * Opens an image as composite using Bio-Formats, where each
 * channel's LUT corresponds to its emission wavenlength. 
 *
 * Stefan Helfrich (University of Konstanz), 07/13/2017
 */
import loci.plugins.in.ImporterOptions;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImagePlusReader;
import java.awt.Color;
import ij.process.LUT;
import ij.CompositeImage;
import ij.IJ;

srcPath = input.getAbsolutePath();

// Bio-Formats  
options = new ImporterOptions();
options.setId(srcPath);
options.setQuiet(true); // To disable warning on too many channels

process = new ImportProcess(options);
process.execute();

imageIndex = 0;
metadata = process.getOMEMetadata();
channelCount = metadata.getChannelCount(imageIndex);

// Compute LUTs from emission wavelengths
logService.debug("Computing LUTs");
luts = new LUT[channelCount];
(0..channelCount-1).each {
	emissionWavelength = metadata.getChannelEmissionWavelength(imageIndex, it);
	logService.debug(""+emissionWavelength);

	color = colorFromWavelength(emissionWavelength.value());

	byte[] reds = new byte[256];
	byte[] greens = new byte[256];
	byte[] blues = new byte[256];
	colorRamp(color, reds, greens, blues);

	lut = new LUT(8, 256, reds, greens, blues);
	luts[it] = lut;
}

// Open image
logService.debug("Opening image");
reader = new ImagePlusReader(process);
imps = reader.openImagePlus();
imp = imps[0];

// Check prerequisites
if (imp.getBitDepth()==24) {
	interp.error("Non-RGB image expected");
}

// Convert image to composite. Otherwise one LUT for whole image.
logService.debug("Converting image to composite");
if (!imp.isComposite()) {
	imp = new CompositeImage(imp);
}

// Set computed LUTs
logService.debug("Setting computed LUTs");
(0..channelCount-1).each {
	imp.setChannelLut(luts[it], it+1);
}

// Show image
imp.show();

/*
 * Adapted from https://github.com/fiji/fiji/blob/9338c1c2c7f07ccbd2c0f8efabda4e649fd4dd7f/plugins/Scripts/Image/Color/Set_Color_By_Wavelength.ijm
 */
Color colorFromWavelength(wavelength) {
  // These values will be between 0 and 1
  red = green = blue = 0;

  if (wavelength < 380 || wavelength > 780) {
    abort("Only wavelengths between 380 and 780 are supported");
  } else if (wavelength <= 440) {
    red = (440 - wavelength) / (440 - 380);
    blue = 1;
  } else if (wavelength <= 490) {
    green = (wavelength - 440) / (490 - 440);
    blue = 1;
  } else if (wavelength <= 510) {
    green = 1;
    blue = (510 - wavelength) / (510 - 490);
  } else if (wavelength <= 580) {
    red = (wavelength - 510) / (580 - 510);
    green = 1;
  } else if (wavelength <= 645) {
    red = 1;
    green = (645 - wavelength) / (645 - 580);
  } else {
  	red = 1;
  }

  intensity = 1;
  if (wavelength > 700) {
    intensity = 0.3 + 0.7 * (780 - wavelength) / (780 - 700);
  } else if (wavelength < 420) {
    intensity = 0.3 + 0.7 * (wavelength - 380) / (420 - 380);
  }

  red *= intensity;
  green *= intensity;
  blue *= intensity;

  return new Color((int)((double)red * 255), (int)((double)green * 255), (int)((double)blue * 255));
}

/*
 * Adapted from IJ1's LUT_Editor.java
 */
void colorRamp(color, reds, greens, blues) {
    initialC = 0;
    finalC = 255;
    difference = finalC - initialC+1;
    start = 0;
    end = (byte)color.getRed()&255;
    redStep = (end-start)/(double)difference;
    for(i=initialC; i<=finalC; i++) {
        reds[i] = (byte)(start+(i-initialC)*redStep);
    }

    start = 0;
    end = (byte)color.getGreen()&255;
    greenStep = (end-start)/difference;
    for(i=initialC; i<=finalC; i++) {
        greens[i] = (byte)(start+(i-initialC)*greenStep);
    }

    start = 0;
    end = (byte)color.getBlue()&255;
    blueStep = (end-start)/difference;
    for(i=initialC; i<=finalC; i++) {
        blues[i] = (byte)(start+(i-initialC)*blueStep);
    }
}
