package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainCoverage {

	private static String coverageFileSimple = "coverage-vG-%s.txt";
	private static String coverageFileEchelon = "coverage-vE-%s.txt";
	private static String base = "src/dados";
	private static String blockAffectedFile = "blockAffected-exp-%s.txt";

	public static void main(String[] args) {

		String tecnica = args[0];
		String post = args[1];

		switch (tecnica) {
		case "greedyTotal":
			execGreedyTotal(post);
			break;

		case "greedyAdd":
			execGreedyAdd(post);
			break;

		case "echelon":
			execEchelon(post);
			break;

		case "echelonTime":
			execEchelonTimeExecution(post);
			break;

		case "random":
			execARTMaxMin(post);
			break;

		case "genetic":
			execGenetic(post);
			break;
		}
	}

	private static void execGreedyTotal(String post) {
		String coverageFile = String.format(coverageFileSimple, post);
		GreedyTotal gt = new GreedyTotal(base, coverageFile);

		LocalDateTime now = LocalDateTime.now();
		gt.extractToFile(gt.getSelectedTestSequence(), now, post);
	}

	private static void execGreedyAdd(String post) {
		String coverageFile = String.format(coverageFileSimple, post);
		GreedyAdditional ga = new GreedyAdditional(base, coverageFile);

		LocalDateTime now = LocalDateTime.now();
		ga.extractToFile(ga.getSelectedTestSequence(), now, post);
	}

	private static void execEchelon(String post) {
		String coverageFile = String.format(coverageFileEchelon, post);
		String blockFile = String.format(blockAffectedFile, post);
		Echelon et = new Echelon(base, coverageFile);

		et.setBlockAffected(getBlockAffected(base + File.separator + blockFile));
		LocalDateTime now = LocalDateTime.now();
		et.extractToFile(et.prioritize(), now, post);
	}

	private static void execEchelonTimeExecution(String post) {
		String coverageFile = String.format(coverageFileEchelon, post);
		String blockFile = String.format(blockAffectedFile, post);
		String timesFile = String.format("times-%.txt", post);
		EchelonTimeExecution et = new EchelonTimeExecution(base, coverageFile);

		et.setBlockAffected(getBlockAffected(base + File.separator + blockFile));
		et.setTimeExecution(base, timesFile);
		LocalDateTime now = LocalDateTime.now();
		et.extractToFile(et.prioritize(), now, post);
	}

	private static void execARTMaxMin(String post) {
		String coverageFile = String.format(coverageFileSimple, post);
		ARTMaxMin random = new ARTMaxMin(base, coverageFile);

		LocalDateTime now = LocalDateTime.now();
		random.extractToFile(random.getSelectedTestSequence(), now, post);
	}

	private static void execGenetic(String post) {
		String coverageFile = String.format(coverageFileSimple, post);
		Genetic tc = new Genetic(base, coverageFile);

		LocalDateTime now = LocalDateTime.now();
		tc.extractToFile(tc.startGeneration(), now, post);
	}

	/**
	 * Function aux for Echalon's execution. Get from file the modified classes and
	 * return them.
	 * 
	 * @param blockAffectedFile
	 * @return
	 */
	private static List<String> getBlockAffected(String blockAffectedFile) {
		List<String> blockAffected = new ArrayList<>();
		String line;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(blockAffectedFile));

			while ((line = bReader.readLine()) != null) {
				blockAffected.add(line);
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("ERROR: \n" + e.getMessage());
			System.exit(1);
		}
		return blockAffected;
	}
}
