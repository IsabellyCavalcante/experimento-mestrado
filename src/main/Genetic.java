package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * Genetic Algorithm for Test Case Prioritization.
 * Note: To generate multiple random numbers, we use the Knuth shuffle - A simple algorithm to generate a permutation of n items uniformly at random.
 * Yafeng.Lu@cs.utdallas.edu
 */
public class Genetic {

	private int genesNum = 0; // How Many Test Cases.
	private final int populationSize = 100; // The size of the Population.
	private final float crossoverProbability = 0.8f, mutationProbability = 0.1f; // The Probabilities to Crossover and
																					// Mutation.
	private String directory, matrixFile;
	private String coverageFile; // The statement/method/branch coverage file.
	private char[][] coverageMatrix; // Store the test case coverage information(Matrix).
	private int[][] population; // Store all the Chromosomes in the Population.
	private ArrayList<Integer> testCaseCovered = new ArrayList<Integer>(); // Store the Statement that are covered in
																			// the Coverage file.

	private int[] resultSequence = new int[this.genesNum]; // Store the Result Test Case Sequence.

	private static int Nth = 0; // The Nth Generation.

	public Genetic(String Directory, String matrixFile) {
		this.directory = Directory; // get the directory to Create a output file for Statistic Data.
		this.matrixFile = matrixFile; // Create a new file use the same file prefix for Statistic Data.
		this.coverageFile = Directory + File.separator + matrixFile;
	}

	// Read the Coverage File and Store the value to the APBC, APDC or APSC Matrix.
	private void getCoverageMatrix(String coverageFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(coverageFile));
			ArrayList<String> tempAl = new ArrayList<String>();
			int columnNum = 0;
			String line;

			// Read all the rows from the Coverage Matrix and store then in an ArrayList for
			// further process.
			while ((line = br.readLine()) != null) {
				if (columnNum == 0) {
					columnNum = line.length();
				} else if (columnNum != line.length()) {
					System.out.println("ERROR: The line from Coverage Matrix File is WORNG.\n" + line);
					System.exit(1);
				}
				tempAl.add(line);
			}
			this.coverageMatrix = new char[tempAl.size()][columnNum]; // Initialize the Coverage Matrix.
			this.genesNum = tempAl.size(); // Initialize the GenesNum, which is the Test Case Number.
			this.population = new int[this.populationSize][this.genesNum]; // Initialize the Population.

			// Store the information in the ArrayList to the Array.
			for (int i = 0; i < tempAl.size(); i++) {
				coverageMatrix[i] = tempAl.get(i).toCharArray();
			}

			for (int i = 0; i < columnNum; i++) {
				boolean covered = false;
				for (int j = 0; j < coverageMatrix.length; j++) {
					if (coverageMatrix[j][i] == '1') {
						covered = true;
						break;
					}
				}
				if (covered) {
					testCaseCovered.add(i);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Return the Selected Test Case Sequence.
	public int[] startGeneration() {
		int generations = 300; // The total number of Generations.

		this.getCoverageMatrix(this.coverageFile); // Read the Coverage Matrix.
		this.generateRandomPopulation(); // Generate the Initial Population.

		// Start the Whole Process for the number of Generations.
		for (int g = 0; g <= generations; g++) {
			this.Nth = g; // Store the Nth Generation.

			this.averagePercentageCoverageCheck(g);

			int[] individuals = this.selection(this.populationSize); // Select the next generation.

			int[][] tempPopulation = new int[this.populationSize][this.genesNum];

			for (int i = 0; i < individuals.length; i++) {
				tempPopulation[i] = this.population[individuals[i]];
			}

			this.population = tempPopulation; // reassign the Selected Population to the Current Population.

			// CrossOver 80 Chromosomes and Mutate 10 Chromosomes.
			this.crossOverAndMutate2();

		} // End of the Whole Process.

		return this.resultSequence;
	}

	// Compute the Average Percentage Block/Statement/Branch Coverage
	private float getAveragePercentageCoverage(int[] chromosome) {
		float averagePercentageCoverage = 0;
		int firstCoveredSum = 0; // Sum of the first test case in the order that covers the
									// Block/Statement/Branch i.

		for (int k = 0; k < this.testCaseCovered.size(); k++) {
			for (int i = 0; i < chromosome.length; i++) {
				if (this.coverageMatrix[chromosome[i]][this.testCaseCovered.get(k)] == '1') {
					firstCoveredSum += i;
					break;
				}
			}
		}

		averagePercentageCoverage = 1
				- (float) ((float) firstCoveredSum / (float) (this.genesNum * this.testCaseCovered.size()))
				+ (1.0F / (float) (2 * this.genesNum));
		return averagePercentageCoverage;
	}

	// Get all the Average Percentage Coverage Metric Value in the Population and
	// Compute the Fitness for each Chromosome.
	private float[] getAllAveragePercentageCoverageMetricAndFitness() {
		float metrics[] = new float[this.populationSize];
		float fitness[] = new float[this.populationSize];

		for (int i = 0; i < this.populationSize; i++) {
			metrics[i] = this.getAveragePercentageCoverage(this.population[i]);
		}

		float[] tempMetrics = Arrays.copyOf(metrics, metrics.length); // Copy the original Array to sort it.
		Arrays.sort(tempMetrics); // Sort the temp Metric Value Array.

		int[] positions = new int[metrics.length];
		// Get the Position value for each Chromosome.
		for (int i = 0; i < metrics.length; i++) {
			for (int j = 0; j < tempMetrics.length; j++) {
				if (tempMetrics[j] == metrics[i]) {
					positions[i] = j + 1; // Larger APC will get larger Position Value.
					tempMetrics[j] = -1;
					break;
				}
			}
		}

		// Compute the Fitness for each Chromosome.
		for (int i = 0; i < positions.length; i++) {
			fitness[i] = 2 * ((positions[i] - 1) / (float) this.populationSize);
		}

		return fitness;
	}

	// Use Stochastic Universal Sampling(SUS) for Selection N individuals.
	private int[] selection(int N) {
		float[] fitness = this.getAllAveragePercentageCoverageMetricAndFitness(); // Get the Fitness value for each
																					// Chromosome.

		float[] maxAndMin = this.getMaxAndMin(fitness); // get the Max and Min Fitness value.
		int[] choose = this.SUS(fitness, N);

		return choose;

	}

	// Stochastic Universal Sampling
	private int[] SUS(float[] fitness, int N) {
		double totalFitness = 0; // Sum of fitness.
		double P = 0; // distance between the pointers.

		for (int i = 0; i < fitness.length; i++) {
			totalFitness += fitness[i];
		}

		P = totalFitness / N;
		// Pick random number between 0 and P
		double start = Math.random() * P;

		// Pick n individuals
		int[] individuals = new int[N];
		int index = 0;
		double sum = fitness[index];
		for (int i = 0; i < N; i++) {
			// Determine pointer to a segment in the population
			double pointer = start + i * P;
			// Find segment, which corresponds to the pointer
			if (sum >= pointer) {
				individuals[i] = index;
			} else {
				for (++index; index < fitness.length; index++) {
					sum += fitness[index];
					if (sum >= pointer) {
						individuals[i] = index;
						break;
					}
				}
			}
		}
		// Return the set of indexes, pointing to the chosen individuals
		return individuals;
	}

	// Returns a random integer i <= uniform(i,m) <= m
	private int uniform(int i, int m) {
		return i + (int) (Math.random() * (m - i));
	}

	private int[] permute(int permutation[], int n) {
		int i;
		for (i = 0; i < n; i++) {
			int j = uniform(i, n - 1);
			int swap = permutation[i];
			permutation[i] = permutation[j];
			permutation[j] = swap;
		}
		return permutation;
	}

	// Generate the Random Population
	private void generateRandomPopulation() {
		int[] sample = new int[this.genesNum];

		for (int i = 0; i < sample.length; i++) {
			sample[i] = i;
		}

		for (int j = 0; j < this.populationSize; j++) {
			int[] temp = Arrays.copyOf(sample, sample.length);
			this.population[j] = this.permute(temp, temp.length);
		}
	}

	private float[] getMaxAndMin(float[] a) {
		float[] results = new float[2];
		results[0] = a[0];
		results[1] = a[0];

		for (int i = 0; i < a.length; i++) {
			if (a[i] > results[0]) {
				results[0] = a[i]; // get the Max.
			} else if (a[i] < results[1]) {
				results[1] = a[i]; // get the Min.
			}
		}
		return results;
	}

	// CrossOver the 2 input Chromosome
	private Object[] crossOver(int[] a, int[] b) {
		if (a.length != b.length) {
			System.out.println("Two Array Size is NOT Equal: " + a.length + ", " + b.length);
			System.exit(0);
		}
		int len = a.length;
		int[] p1 = Arrays.copyOf(a, len);
		int[] p2 = Arrays.copyOf(b, len);

		int random = (int) (Math.random() * len);
		int[] o1 = new int[len], o2 = new int[len];
		ArrayList<Integer> a1 = new ArrayList<Integer>();
		ArrayList<Integer> a2 = new ArrayList<Integer>();

		for (int i = 0; i < random; i++) {
			o1[i] = p1[i];
			a1.add(o1[i]);
			o2[i] = p2[i];
			a2.add(o2[i]);
		}

		for (int i = 0; i < len; i++) {
			if (a1.contains(p2[i])) {
				p2[i] = -1;
			}
		}

		for (int i = 0, j = random; i < len && j < len; i++) {
			if (p2[i] != -1) {
				o1[j] = p2[i];
				j++;
			}
		}

		for (int i = 0, j = 0; i < len && j < random; i++) {
			if (a2.contains(p1[i])) {
				p1[i] = -1;
			}
		}

		for (int i = 0, j = random; i < len && j < len; i++) {
			if (p1[i] != -1) {
				o2[j] = p1[i];
				j++;
			}
		}

		return new Object[] { o1, o2 };
	}

	private void mutation(int[] chromosome) { //
		// Randomly select two Genes and exchange their value;
		int pos0 = 0, pos1 = 0;

		while (pos0 == pos1) {
			pos0 = this.uniform(0, this.genesNum);
			pos1 = this.uniform(0, this.genesNum);
		}

		// Exchange the two Genes.
		int temp = chromosome[pos0];
		chromosome[pos0] = chromosome[pos1];
		chromosome[pos1] = temp;
	}

	private void crossOverAndMutate2() {// CrossOver 80 Chromosomes and Mutate 10 Chromosomes.
		int[] originalArray = new int[this.populationSize];
		for (int i = 0; i < this.populationSize; i++) { // Initialize the Original Array.
			originalArray[i] = i;
		}

		int[] crossOverArray = KnuthShuffle(originalArray);

		// After Randomly select 80 Chromosomes, CrossOver them.
		int crossOverNum = 80;

		for (int i = 0; i < crossOverNum; i = i + 2) {
			int parent0 = crossOverArray[i], parent1 = crossOverArray[i + 1];
			Object[] Offsprings = this.crossOver(this.population[parent0], this.population[parent1]); // Crossover the 2
																										// Parents.
			if (Offsprings.length != 2) {
				System.out.println("ERROR: Offersprings len is not 2!");
				System.exit(1);
			}
			this.population[parent0] = (int[]) Offsprings[0];
			this.population[parent1] = (int[]) Offsprings[1];
		}
		// Random Select 10 Chromosomes and Mutate.
		int[] MutationArray = KnuthShuffle(originalArray);
		int MutationNum = 10; // Random Select 10 Chromosomes to Mutate.

		for (int i = 0; i < MutationNum; i++) {
			int select = MutationArray[i];
			this.mutation(this.population[select]);
		}
	}

	private int[] KnuthShuffle(int[] a) {
		// Use Fisher–Yates shuffle(Knuth Shuffle).
		int[] originalArray = Arrays.copyOf(a, a.length);
		Random rnd = new Random();

		for (int i = originalArray.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int tmp = originalArray[index];
			originalArray[index] = originalArray[i];
			originalArray[i] = tmp;
		}
		return originalArray;
	}

	private void averagePercentageCoverageCheck(int g) {
		// Print the Max Average Percentage Coverage.
		float metrics[] = new float[this.populationSize];
		float max = 0, sum = 0;
		ArrayList<Integer> maxAPC = new ArrayList<Integer>(); // Store the Chromosome that has the Largest APC.

		for (int i = 0; i < this.populationSize; i++) {
			metrics[i] = this.getAveragePercentageCoverage(this.population[i]);
			if (metrics[i] > max) {
				max = metrics[i];
			}
			sum += metrics[i];
		}

		for (int i = 0; i < metrics.length; i++) {
			if (metrics[i] == max) {
				maxAPC.add(i);
			}
		}

		if (g == 300) {
			try {
				String outputDataFile = this.directory + File.separator + this.matrixFile + "_output";
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputDataFile));

				// get the Coverage Increase Trend.
				int[] selectedSequence = this.population[maxAPC.get(0)];
				this.resultSequence = selectedSequence; // return the Selected Test Case Sequence.
				int statements = this.coverageMatrix[0].length;
				char[] accumulatedCoverageArray = new char[statements];

				// Output the selected Sequence.
				bw.write("Selected Sequence: \n");
				for (int i = 0; i < selectedSequence.length; i++) {
					bw.write(selectedSequence[i] + " ");
				}

				bw.write("\nCoverages Increasement:\n");
				for (int i = 0; i < selectedSequence.length; i++) {
					this.mergeTwoCoverageArray(this.coverageMatrix[selectedSequence[i]], accumulatedCoverageArray);
					float ratio = this.getNumberOfOneInArray(accumulatedCoverageArray) / (float) statements;
					bw.write(ratio + ",");
				}

				bw.flush();
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Return the Number of '1' in the Test Case Coverage Array
	private int getNumberOfOneInArray(char[] a) {
		int num = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] == '1') {
				num++;
			}
		}
		return num;
	}

	// Merge the New Coverage Array to the Old Array.
	private void mergeTwoCoverageArray(char[] newArray, char[] oldArray) {
		if (newArray.length != oldArray.length) {
			System.out.println("ERROR: MergeTwoCoverageArray(): 2 Arrays length are not equal.");
		}
		for (int i = 0; i < oldArray.length; i++) {
			if (newArray[i] == '1') {
				oldArray[i] = newArray[i];
			}
		}
	}

	public void print(int[] a) {
		System.out.println("------int[] Start------Len: " + a.length);
		System.out.println(Arrays.toString(a));
		System.out.println("------int[] End------");
	}
	
	public void extractToFile(int[] a, LocalDateTime initial) {
		try {
			FileWriter fw = new FileWriter("src/dados/outputGenetic-exp2.txt");
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			fw.write(dtf.format(initial));
			fw.write(String.format("\n------int[] Start------Len: %d \n", a.length));
			fw.write(Arrays.toString(a));
			fw.write("\n------int[] End------\n");
			LocalDateTime now = LocalDateTime.now();
			fw.write(dtf.format(now));
			fw.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
