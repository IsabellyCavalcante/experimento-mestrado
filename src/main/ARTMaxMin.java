package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Use ART-MaxMin Algorithm for Test Case Prioritization.
 * Yafeng.Lu@cs.utdallas.edu
 */
public class ARTMaxMin {

	String Directory;
	String matrixFile;
	String coverageFile;
	char[][] coverageMatrix;
	ArrayList<Integer> coveredZero = new ArrayList<Integer>(); // Store the test case in case it covers 0
																// statements/methods/branches.

	public ARTMaxMin(String Directory, String matrixFile) {
		this.Directory = Directory; // Get the directory to Create a output file for Statistic Data.
		this.matrixFile = matrixFile; // Create a new file use the same file prefix for Statistic Data.
		this.coverageFile = Directory + File.separator + matrixFile;

		this.getCoverageMatrix(this.coverageFile);
	}

	/**
	 * For tests
	 */
	public ARTMaxMin(char[][] coverageMatrix) {
		this.coverageMatrix = coverageMatrix;
	}

	// Read the Coverage File and Store the value to the APBC, APDC or APSC Matrix.
	public void getCoverageMatrix(String coverageFile) {
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

			// Store the information in the ArrayList to the Array.
			for (int i = 0; i < tempAl.size(); i++) {
				coverageMatrix[i] = tempAl.get(i).toCharArray();
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int[] getSelectedTestSequence() {

		int len = this.coverageMatrix.length, columnNum = this.coverageMatrix[0].length;
		int[] selectedTestSequence = new int[len]; // Final list
		ArrayList<Integer> selected = new ArrayList<Integer>(); // Store the current selected test cases.

		// Generate procedure
		int first = (int) (len * Math.random()); // Randomly select the first element.
		selected.add(first);

		while (selected.size() < len) {

			// Generate procedure
			ArrayList<Integer> candidate = new ArrayList<Integer>(); // Store the already selected candidate tests.

			char[] covered = new char[columnNum]; // Record the already covered statements/methods/branches.
			this.clearArray(covered);
			int coveredNum = 0; // Store the number of statements/methods/branches.

			// Randomly select the first candidate.
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for (int i = 0; i < len; i++) {
				if (!selected.contains(i)) {
					tempList.add(i);
				}
			}

			int firstRandom = (int) (Math.random() * tempList.size());
			int firstCadidateIndex = tempList.get(firstRandom); // Get the index of first selected candidate.
			candidate.add(firstCadidateIndex);

			this.mergeIntoCurrentArray(covered, this.coverageMatrix[firstCadidateIndex]);
			coveredNum = this.getCoveredNumber(covered);

			while (true) {

				ArrayList<Integer> leftToChoose = new ArrayList<Integer>(); // The left unselected candidates to
																			// choose.
				for (int i = 0; i < len; i++) {
					if (!selected.contains(i) && !candidate.contains(i)) {
						leftToChoose.add(i);
					}
				}

				if (leftToChoose.size() == 0) {
					// Nothing to choose
					break;
				}

				int selectedRandom = (int) (Math.random() * leftToChoose.size()); // Randomly select the next candidate.
				int newCandiadteIndex = leftToChoose.get(selectedRandom); // Get the index of new selected candidate.

				this.mergeIntoCurrentArray(covered, this.coverageMatrix[newCandiadteIndex]); // Merge the new
																								// statements/methods/branches
																								// coverage into the
																								// covered array.
				int currentCovered = this.getCoveredNumber(covered);
				if (currentCovered > coveredNum) {
					coveredNum = currentCovered;
					candidate.add(newCandiadteIndex); // Add the selected candidate to the candidate arraylist.
				} else {
					break; // If the statements/methods/branches coverage is not increase, then stop.
				}
			}

			double[] maxDistances = new double[candidate.size()]; // Get the maximum distance from the candidate minimum
																	// distances.
			for (int j = 0; j < candidate.size(); j++) {
				int candidateNo = candidate.get(j);
				double[] minDistance = new double[selected.size()]; // Get the minimum distance from the selected
																	// minimum distances.
				for (int i = 0; i < selected.size(); i++) {
					int testCaseNo = selected.get(i);
					minDistance[i] = this.getJaccardDistance(this.coverageMatrix[testCaseNo],
							this.coverageMatrix[candidateNo]);
				}

				int minIndex = this.getMinIndex(minDistance);
				if (minIndex == -1) {
					System.out.println("ERROR: getSelectedTestSequence MinIndex == -1");
					System.exit(1);
				}
				maxDistances[j] = minDistance[minIndex]; // Assign each candidate's minimum distance to the MaxDistances
															// array.
			}

			int maxIndex = this.getMaxIndex(maxDistances);
			if (maxIndex == -1) {
				System.out.println("ERROR: getSelectedTestSequence MaxIndex == -1");
				System.exit(1);
			}

			// Select the candidate to selected arraylist.
			selected.add(candidate.get(maxIndex));
		}

		// Add the elements of selected arraylist to the test case sequence.
		for (int i = 0; i < selected.size(); i++) {
			selectedTestSequence[i] = selected.get(i);
		}

		return selectedTestSequence;
	}

	// Calculate the Jaccard distance between two vector.
	public double getJaccardDistance(char[] a, char[] b) {
		if (a.length != b.length) {
			System.out.println("ERROR: length not equal.");
			System.exit(0);
		}

		int len = a.length;
		double distance = 0;
		int join = 0, combine = 0;
		char[] combinedArray = new char[len]; // Store the combined result of a and b.

		for (int i = 0; i < len; i++) {
			if (a[i] == '1' && b[i] == '1') {
				join++;
			}
			if (a[i] == '1') {
				combinedArray[i] = '1';
			}
			if (b[i] == '1') {
				combinedArray[i] = '1';
			}
		}
		combine = this.getCoveredNumber(combinedArray);

		if (combine == 0) {
			return 0;
		}

		distance = 1.0 - (join / (double) combine);
		return distance;
	}

	// Return the minimum element's index of the double[].
	public int getMinIndex(double[] a) {
		double min = Double.MAX_VALUE;
		int index = -1;

		for (int i = 0; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
				index = i;
			}
		}
		return index;
	}

	// Return the maximum element's index of the double[].
	public int getMaxIndex(double[] a) {
		double max = -Double.MAX_VALUE;
		int index = -1;

		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
				index = i;
			}
		}
		return index;
	}

	// Calculate the number of '1' in the array.
	private int getCoveredNumber(char[] a) {
		int num = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] == '1') {
				num++;
			}
		}
		return num;
	}

	// Set all elements '0' in the array.
	public void clearArray(char[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = '0';
		}
	}

	// Merge all the '1's in the new array into the current array.
	private void mergeIntoCurrentArray(char[] current, char[] newArray) {
		if (current.length != newArray.length) {
			System.out.println("ERROR: mergeIntoCurrentArray: length is not equal.");
			System.exit(1);
		}
		int len = current.length;
		for (int i = 0; i < len; i++) {
			if (newArray[i] == '1') {
				current[i] = newArray[i];
			}
		}
	}

	public void Print(int[] a) {
		System.out.println("------int[] Start------Len: " + a.length);
		System.out.println(Arrays.toString(a));
		System.out.println("------int[] End------");
	}
	
	public void extractToFile(int[] a, LocalDateTime initial) {
		try {
			FileWriter fw = new FileWriter("src/dados/outputARTMaxMin.txt");
			
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