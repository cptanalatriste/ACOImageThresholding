package pe.edu.pucp.acosthres.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pe.edu.pucp.acothres.ProblemConfiguration;

public class ImageFileHelper {

	public static int[][] getImageArrayFromFile(String imageFile)
			throws IOException {

		BufferedImage image = ImageIO.read(new File(imageFile));
		Raster imageRaster = image.getData();

		int[][] imageAsArray;
		int[] pixel = new int[1];
		int[] buffer = new int[1];

		imageAsArray = new int[imageRaster.getWidth()][imageRaster.getHeight()];

		for (int i = 0; i < imageRaster.getWidth(); i++)
			for (int j = 0; j < imageRaster.getHeight(); j++) {
				pixel = imageRaster.getPixel(i, j, buffer);
				imageAsArray[i][j] = pixel[0];
			}
		return imageAsArray;
	}

	public static void generateImageFromArray(
			int[][] normalizedPheromoneMatrix, String outputImageFile)
			throws IOException {
		System.out.println("Generating output image");
		BufferedImage outputImage = new BufferedImage(
				normalizedPheromoneMatrix.length,
				normalizedPheromoneMatrix[0].length,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = outputImage.getRaster();
		for (int x = 0; x < normalizedPheromoneMatrix.length; x++) {
			for (int y = 0; y < normalizedPheromoneMatrix[x].length; y++) {
				raster.setSample(x, y, 0, normalizedPheromoneMatrix[x][y]);
			}
		}
		File imageFile = new File(outputImageFile);
		ImageIO.write(outputImage, "bmp", imageFile);
		System.out.println("Resulting image stored in: " + outputImageFile);
	}

	public static int[][] removeBackgroundPixels(int[][] imageGraph) {
		int[][] result = new int[imageGraph.length][imageGraph[0].length];
		for (int i = 0; i < imageGraph.length; i++) {
			for (int j = 0; j < imageGraph[0].length; j++) {
				if (Math.abs(imageGraph[i][j]
						- ProblemConfiguration.GRAYSCALE_MIN_RANGE) < ProblemConfiguration.GRAYSCALE_DELTA) {
					result[i][j] = ProblemConfiguration.ABSENT_PIXEL_FLAG;
				} else {
					result[i][j] = imageGraph[i][j];
				}
			}
		}
		return result;
	}
}