import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCoverage {

	private static String coverageFileSimple = "coverage-vG.txt";
	private static String coverageFileEchalon = "coverage-vE.txt";
	private static String base = "src/dados";

	public static void main(String[] args) {
//		execGreedyTotal();
//		execGreedyAdd();
		execEchalonTotal();
	}

	protected static void execGreedyTotal() {
		GreedyTotal gt = new GreedyTotal(base, coverageFileSimple);
		gt.Print(gt.getSelectedTestSequence());
	}

	protected static void execGreedyAdd() {
		GreedyAdditional ga = new GreedyAdditional(base, coverageFileSimple);
		ga.Print(ga.getSelectedTestSequence());
	}

	/**
	 * Call execution's echalon total technique. Require the coverageFile and
	 * blockAffectedFile.
	 */
	protected static void execEchalonTotal() {
		Echelon et = new Echelon(base, coverageFileEchalon);
		et.setBlockAffected(getBlockAffected(base + File.separator + "blockAffected-exp2.txt"));
		et.print(et.prioritize());
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
