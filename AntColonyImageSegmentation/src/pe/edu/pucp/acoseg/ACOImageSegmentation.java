package pe.edu.pucp.acoseg;

import java.util.Date;

import pe.edu.pucp.acoseg.ant.AntColony;
import pe.edu.pucp.acoseg.ant.Environment;
import pe.edu.pucp.acoseg.cluster.KmeansClassifier;
import pe.edu.pucp.acoseg.image.ImageUtilities;

public class ACOImageSegmentation {

	private Environment environment;
	private AntColony antColony;

	public ACOImageSegmentation(Environment environment) {
		this.environment = environment;
		this.antColony = new AntColony(environment,
				ProblemConfiguration.NUMBER_OF_STEPS);
	}

	private void solveProblem() {
		this.environment.initializePheromoneMatrix();
		int iteration = 0;
		System.out.println("STARTING ITERATIONS");
		System.out.println("Number of iterations: "
				+ ProblemConfiguration.MAX_ITERATIONS);
		while (iteration < ProblemConfiguration.MAX_ITERATIONS) {
			System.out.println("Current iteration: " + iteration);
			this.antColony.clearAntSolutions();
			this.antColony.buildSolutions();
			System.out.println("UPDATING PHEROMONE TRAILS");
			System.out.println("Depositing pheromone");
			this.antColony.depositPheromone();
			this.environment.performEvaporation();
			iteration++;
		}
		System.out.println("EXECUTION FINISHED");
		// TODO(cgavidia): We're not storing best tour or it's lenght
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ACO FOR IMAGE SEGMENTATION");
		System.out.println("=============================");

		try {
			String imageFile = ProblemConfiguration.IMAGE_FILE;
			System.out.println("Data file: " + imageFile);

			int[][] imageGraph = ImageUtilities
					.getProblemGraphFromFile(imageFile);

			System.out.println("Generating original image from matrix");
			ImageUtilities.generateImageFromArray(imageGraph,
					ProblemConfiguration.ORIGINAL_IMAGE_FILE);

			Environment environment = new Environment(imageGraph);
			ACOImageSegmentation acoImageSegmentation = new ACOImageSegmentation(
					environment);
			System.out.println("Starting computation at: " + new Date());
			long startTime = System.nanoTime();

			acoImageSegmentation.solveProblem();

			System.out.println("Starting K-means clustering");

			KmeansClassifier classifier = new KmeansClassifier(environment,
					ProblemConfiguration.NUMBER_OF_CLUSTERS);
			int[][] segmentedImageAsMatrix = classifier
					.generateSegmentedImage();

			System.out.println("Generating segmented image");
			ImageUtilities.generateImageFromArray(segmentedImageAsMatrix,
					ProblemConfiguration.OUTPUT_IMAGE_FILE);

			long endTime = System.nanoTime();
			System.out.println("Finishing computation at: " + new Date());
			System.out.println("Duration (in seconds): "
					+ ((double) (endTime - startTime) / 1000000000.0));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}