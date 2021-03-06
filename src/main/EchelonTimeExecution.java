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
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;

import java.util.AbstractMap.SimpleEntry;


/*
 * Echelon Algorithm Extension for Test Case Prioritization.
 * isabellysantos@copin.ufcg.edu.br
 */
public class EchelonTimeExecution {

	private List<String> blockAffected;
	private List<Double> timeExecution;
	private List<String[]> coverageMatrix;
	private String coverageFile;
	private final String SEP = File.separator;

	public EchelonTimeExecution(String Directory, String matrixFile) {
		this.coverageFile = Directory + SEP + matrixFile;

		// ex return {["classeA.1"], ["classeB.3", "classeB.5", "classeB.45"]}
		getCoverageMatrix(this.coverageFile);
	}

	/**
	 * For tests
	 */
	public EchelonTimeExecution(List<String[]> coverageMatrix) {
		this.coverageMatrix = coverageMatrix;
	}

	public List<Integer> prioritize() {

		List<Integer> finalList = new ArrayList<Integer>(); // final list with prioritized tests

		// calcula a primeira vez a lista de pesos e o numero de testes que cobrem algo
		List<Entry<Integer, Integer>> originalWeightList = getTotalModifiedCoverage(null, blockAffected);

		while (getQtdTestsWithCov(originalWeightList)) {
			// Fazendo uma copia dos blocos afetados
			List<String> currBlkAffected = new ArrayList<>(blockAffected);

			// calcula a primeira vez a lista de pesos e o numero de testes que cobrem algo
			// da currBlkAffected
			List<Entry<Integer, Integer>> copyWeightList = getTotalModifiedCoverage(originalWeightList,
					currBlkAffected);

			while (getQtdTestsWithCov(copyWeightList)) {

				// selecionando o teste com maior peso
				Entry<Integer, Integer> biggerTest = null;
				List<Entry<Integer, Integer>> listWithBiggerWeight = getBiggerWeightList(copyWeightList);

				if (listWithBiggerWeight.size() == 1)
					biggerTest = listWithBiggerWeight.get(0);
				else
					biggerTest = getTestWithLessTime(listWithBiggerWeight);

				// add o maior na lista final e depois removendo da lista de testes
				finalList.add(biggerTest.getKey());
				copyWeightList.remove(biggerTest);

				// removendo da lista original - eh necessario fazer isso dessa forma pois o
				// elemento nao eh mais o mesmo depois que a cobertura for atualizada, dai tem
				// que procurar pela key para remover
				for (Entry<Integer, Integer> entry : originalWeightList) {
					if (entry.getKey().equals(biggerTest.getKey())) {
						originalWeightList.remove(entry);
						break;
					}
				}

				// por fim removendo o que foi coberto por ele da lista de impactados
				removeImpactedCoverage(biggerTest, currBlkAffected);

				// atualizando a lista de pesos
				copyWeightList = getTotalModifiedCoverage(copyWeightList, currBlkAffected);
			}
		}

		// adicionando os testes que nao cobrem nenhuma parte alterada
		if (!originalWeightList.isEmpty()) {
			originalWeightList.stream().forEach(element -> finalList.add(element.getKey()));
		}

		return finalList;
	}

	/**
	 * Recebe um file com as informacoes de cobertura e gera uma matriz com esse
	 * formato: {["classeA.1"], ["classeB.3", "classeB.5", "classeB.45"]}
	 */
	private void getCoverageMatrix(String coverageFile) {
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(coverageFile));
			ArrayList<String> tempAl = new ArrayList<String>();
			String line;

			// Read all the rows from the Coverage Matrix and store then in an
			// ArrayList for further process.
			while ((line = bReader.readLine()) != null) {
				tempAl.add(line);
			}

			this.coverageMatrix = new ArrayList<>(); // Initialize the Coverage Matrix.

			// ["classeA.1", "classeB.3,classeB.5,classeB.45"] =>
			// {["classeA.1"], ["classeB.3", "classeB.5", "classeB.45"]}
			for (int i = 0; i < tempAl.size(); i++) {
				coverageMatrix.add(tempAl.get(i).split(","));
			}

			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Entry<Integer, Integer>> getTotalModifiedCoverage(List<Entry<Integer, Integer>> weightList,
			List<String> blkAffected) {
		List<Entry<Integer, Integer>> auxList = new ArrayList<>();

		if (Objects.isNull(weightList)) {
			// calcula o peso de todos
			for (int test = 0; test < coverageMatrix.size(); test++) {
				String[] testOriginalCoverage = coverageMatrix.get(test);

				int value = getWeight(testOriginalCoverage, blkAffected);
				auxList.add(new SimpleEntry<Integer, Integer>(test, value));
			}
			return auxList;
		}

		// calcula o peso apenas da lista dada
		for (Entry<Integer, Integer> test : weightList) {
			String[] testOriginalCoverage = coverageMatrix.get(test.getKey());

			int value = getWeight(testOriginalCoverage, blkAffected);
			auxList.add(new SimpleEntry<Integer, Integer>(test.getKey(), value));
		}

		return auxList;
	}

	private int getWeight(String[] testCoverage, List<String> blkAffected) {
		int count = 0;

		for (String classInfo : testCoverage) {
			if (blkAffected.contains(classInfo))
				count++;
		}
		return count;
	}

	private boolean getQtdTestsWithCov(List<Entry<Integer, Integer>> weightList) {
		for (Entry<Integer, Integer> entry : weightList) {
			if (entry.getValue() != 0)
				return true;
		}
		return false;
	}

	private List<Entry<Integer, Integer>> getBiggerWeightList(List<Entry<Integer, Integer>> tests) {
		List<Entry<Integer, Integer>> sameWeight = new ArrayList<Entry<Integer, Integer>>();
		int bigger = -1;

		for (Entry<Integer, Integer> test : tests) {
			if (test.getValue() > bigger) {
				bigger = test.getValue();
				sameWeight.clear();
				sameWeight.add(test);
			} else if (test.getValue() == bigger)
				sameWeight.add(test);
		}
		return sameWeight;
	}

	/**
	 * Seleciona o teste com menor tempo de execução
	 * 
	 * @param tests
	 * @return
	 */
	private Entry<Integer, Integer> getTestWithLessTime(List<Entry<Integer, Integer>> testList) {
		Entry<Integer, Integer> chosenTest = null;
		double minValue = Double.MAX_VALUE;

		for (Entry<Integer, Integer> test : testList) {

			double time = timeExecution.get(test.getKey());

			if (time < minValue) {
				minValue = time;
				chosenTest = test;
			}
		}
		return chosenTest;
	}

	private void removeImpactedCoverage(Entry<Integer, Integer> biggerTest, List<String> currBlkAffected) {
		String[] testCoverage = coverageMatrix.get(biggerTest.getKey());

		for (String classInfo : testCoverage) {
			if (currBlkAffected.contains(classInfo))
				currBlkAffected.remove(classInfo);
		}
	}

	public List<String> getBlockAffected() {
		return this.blockAffected;
	}

	public void setBlockAffected(List<String> blockAffected) {
		this.blockAffected = blockAffected;
	}

	public void setTimeExecution(String Directory, String matrixFile) {
		String executionFile = Directory + SEP + matrixFile;

		try {
			BufferedReader bReader = new BufferedReader(new FileReader(executionFile));
			ArrayList<String> tempAl = new ArrayList<String>();
			String line;

			while ((line = bReader.readLine()) != null) {
				tempAl.add(line);
			}

			this.timeExecution = new ArrayList<>();

			for (int i = 0; i < tempAl.size(); i++) {
				timeExecution.add(Double.parseDouble(tempAl.get(i)));
			}

			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * For tests
	 */
	public void setCoverage(List<String[]> coverageMatrix) {
		this.coverageMatrix = coverageMatrix;
	}

	public void print(List<Integer> tests) {
		System.out.println("------int[] Start-------Len: " + tests.size());

		tests.stream().forEach(element -> System.out.print(element + ", "));
		System.out.println("\n------int[] End------");
	}
	
	public void extractToFile(List<Integer> a, LocalDateTime initial, String post) {
		try {
			String fileName = String.format("dados/output/outputEchelonTime-%s.txt", post);
			FileWriter fw = new FileWriter(fileName);
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
			fw.write(dtf.format(initial));
			fw.write(String.format("\n------int[] Start------Len: %d \n", a.size()));
			fw.write(Arrays.toString(a.toArray()));
			fw.write("\n------int[] End------\n");
			LocalDateTime now = LocalDateTime.now();
			fw.write(dtf.format(now));
			fw.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
