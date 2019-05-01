import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainCoverage {

	private static String fileCoverageSimple = "coverage-v5.txt";
	private static String fileCoverageEchalon = "coverage-ech-exp1.txt";
	private static String base = "src/dados";

	public static void main(String[] args) {
		// execGreedyTotal();

		// execGreedyAdd();

		execEchalonTotal();

	}

	protected static void execGreedyTotal() {
		GreedyTotal gt = new GreedyTotal(base, fileCoverageSimple);
		gt.Print(gt.getSelectedTestSequence());
	}

	protected static void execGreedyAdd() {
		GreedyAdditional ga = new GreedyAdditional(base, fileCoverageSimple);
		ga.Print(ga.getSelectedTestSequence());
	}

	private static void execEchalonTotal() {
		EchalonTotal et = new EchalonTotal(base, fileCoverageEchalon);
		et.setBlockAffected(getBlockAffected(base + File.separator + "blockAffected-exp1.txt"));
		et.print(et.prioritize());
	}

	/**
	 * Class aux for Echalon's execution 
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
