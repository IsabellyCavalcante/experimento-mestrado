import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class EchalonTotal {

	private List<String> blockAffected;
	private String[][] coverageMatrix;
	private String coverageFile;	
	private final String SEP = File.separator;

	public EchalonTotal(String Directory, String matrixFile) {
		this.coverageFile = Directory + SEP + matrixFile;
	}

	public List<Integer> prioritize() {

		//ex return [["classeA.1:1", "classeB.3:0", "classeB.5:0", "classeB.45:1"]]
		getCoverageMatrix(this.coverageFile);
		
		List<Integer> suiteList = new ArrayList<>(); // final list with prioritized tests
		
		List<Map.Entry<Integer, Double>> weightList = new ArrayList<>();  // list with tests that cover some modified part
		
		List<Integer> notWeighted = new ArrayList<>(); // list with tests that don't cover modify parts
		
		for (int test = 0; test < coverageMatrix.length; test++) {
			String[] testCoverage = coverageMatrix[test];
			
			double value = getWeight(testCoverage);
			if (value != 0.0)
				weightList.add(new SimpleEntry<Integer, Double>(test, value));
			else
				notWeighted.add(test);
		}
		
		// ordenando a lista de testes com cobertura do maior para o menor
		// e adicionando na lista final
		weightList.stream().sorted((t1, t2) -> {
			return t2.getValue().compareTo(t1.getValue());
		}).forEach(element -> suiteList.add(element.getKey()));
		
		Collections.shuffle(notWeighted);
		
		// adicionando na lista final os testes que nao cobrem nada
		notWeighted.stream().forEach(element -> suiteList.add(element));

		return suiteList;
	}

	/**
	 * Recebe um file com as informacoes de cobertura e gera uma matriz com esse formato: 
	 * [["classeA.1:1", "classeB.3:0", "classeB.5:0", "classeB.45:1"]]
	 * 
	 * @param coverageFile
	 */
	public void getCoverageMatrix(String coverageFile) {
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(coverageFile));
			ArrayList<String> tempAl = new ArrayList<String>();
			int columnNum = 0;
			String line;

			// Read all the rows from the Coverage Matrix and store then in an
			// ArrayList for further process.

			// TODO verificar se vai ficar com todas as classes mesmo. Se ficar so as que
			// sao cobertas esse if aqui nao vale
			while ((line = bReader.readLine()) != null) {
				if (columnNum == 0) {
					columnNum = line.length();
				} else if (columnNum != line.length()) {
					System.out.println("ERROR: The line from Coverage Matrix File is WORNG.\n" + line);
					System.exit(1);
				}
				tempAl.add(line);
			}

			this.coverageMatrix = new String[tempAl.size()][columnNum]; // Initialize the Coverage Matrix.

			// Store the information in the ArrayList to the Array.
			// ["classeA.1:1, classeB.3:0, classeB.5:0, classeB.45:1"] =>
			// [["classeA.1:1", "classeB.3:0", "classeB.5:0", "classeB.45:1"]]
			for (int i = 0; i < tempAl.size(); i++) {
				coverageMatrix[i] = tempAl.get(i).split(", ");
			}

			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getPercentage(double value) {
		int sizeBlock = getBlockAffected().size();
		if (sizeBlock == 0)
			return 0;
		return value / sizeBlock;
	}

	/**
	 * Recupera a porcentagem de stmts alterados cobertos.
	 * 
	 * @param objectList
	 * @return
	 */
	public double getWeight(String[] testCoverage) {
		int count = 0;
		
		for (String cov : testCoverage) {
			String[] classInfo = cov.split(":");
			String classWithLine = classInfo[0];
			boolean isCov = classInfo[1].equals("1");
			
			if (containsBlock(classWithLine) 
					&& isCov) {
				count++;
			}
		}
		return getPercentage(count);
	}
	

	public List<String> getBlockAffected() {
		return blockAffected;
	}

	public void setBlockAffected(List<String> blockAffected) {
		this.blockAffected = blockAffected;
	}

	public boolean containsBlock(String value) {
		return blockAffected.contains(value);
	}

	
	public void print(List<Integer> tests) {
		System.out.println("------int[] Start-------Len: "+ tests.size());
		
		tests.stream().forEach(element -> System.out.print(element + ","));
		System.out.println("\n------int[] End------");
	}
}
