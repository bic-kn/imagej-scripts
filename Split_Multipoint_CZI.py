# @File(label="Input file") input
# @File(label="Output folder") output

# Splits multi-point CZI files into multiple TIFFs using Bio-Formats.
#
# Stefan Helfrich (University of Konstaz), 05/09/2016

from ij import IJ
from loci.plugins import BF
from loci.plugins.in import ImporterOptions
import os

srcPath = input.getAbsolutePath()

# using LOCI BioFormats  
settings = ImporterOptions()
settings.setId(srcPath)
settings.setOpenAllSeries(True)
settings.setVirtual(True)
settings.setWindowless(True)

imps = BF.openImagePlus(settings)

for i in range(0, len(imps)):
	currentImp = imps[i]
	filename = os.path.split(srcPath)[1]
	filenameWithoutExtension = os.path.splitext(filename)[0]
	IJ.saveAs(currentImp, "TIFF", output.getAbsolutePath() + "/" + filenameWithoutExtension + "-" + str(i) + ".tif")
