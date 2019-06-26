package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainCoverage {

	private static String coverageFileSimple = "coverage-vG.txt";
	private static String coverageFileEchelon = "coverage-vE.txt";
	private static String base = "src/dados";
	private static String blockAffectedFile = "blockAffected-exp3.txt";

	public static void main(String[] args) {
//		execGreedyTotal();
//		execGreedyAdd();
//		execEchelon();
//		execEchelonTimeExecution();
		execARTMaxMin();
	}

	private static void execGreedyTotal() {
		GreedyTotal gt = new GreedyTotal(base, coverageFileSimple);
		gt.Print(gt.getSelectedTestSequence());
	}

	private static void execGreedyAdd() {
		GreedyAdditional ga = new GreedyAdditional(base, coverageFileSimple);
		ga.Print(ga.getSelectedTestSequence());
	}

	private static void execEchelon() {
		Echelon et = new Echelon(base, coverageFileEchelon);
		
		et.setBlockAffected(getBlockAffected(base + File.separator + blockAffectedFile));
		et.print(et.prioritize());
	}

	private static void execEchelonTimeExecution() {
		EchelonTimeExecution et = new EchelonTimeExecution(base, coverageFileEchelon);
		et.setBlockAffected(getBlockAffected(base + File.separator + blockAffectedFile));
		et.setTimeExecution(base, "times.txt");
		et.print(et.prioritize());
	}

	private static void execARTMaxMin() {
		ARTMaxMin random = new ARTMaxMin(base, coverageFileSimple);
		LocalDateTime now = LocalDateTime.now();
		random.extractToFile(random.getSelectedTestSequence(), now);
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
