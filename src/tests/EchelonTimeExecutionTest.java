package tests;

import main.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EchelonTimeExecutionTest {
	private EchelonTimeExecution ech;

	@BeforeEach
	public void setUp() throws Exception {
		ech = new EchelonTimeExecution(getCoverage());

		ech.setBlockAffected(getBlock());
		ech.setTimeExecution("src/tests", "times.txt");
	}

	@Test
	public void test() {
		List<Integer> exp = new ArrayList<>();
		Collections.addAll(exp, 5, 0, 8, 3, 1, 4, 6, 9, 2, 7);

		assertEquals(exp, ech.prioritize());
	}
	
	@Test
	public void test2() {
		List<String[]> newCoverage = getCoverage();
		newCoverage.add(new String[] {"/test/isa/classeA.4", "/test/isa/classeB.2", "/test/isa/classeB.4", "/test/isa/classeB.11"}); // test11 - 3, 5, 6 
		newCoverage.add(new String[] {"/test/isa/classeA.2"}); // test12 - 1
		newCoverage.add(new String[] {"/test/isa/classeE.13"}); // test13 - nada
		ech.setCoverage(newCoverage);
		ech.setTimeExecution("src/tests", "times2.txt");
		
		List<Integer> exp = new ArrayList<>();
		Collections.addAll(exp, 10, 0, 5, 6, 1, 11, 8, 3, 4, 9, 2, 7, 12);
		
		assertEquals(exp, ech.prioritize());
	}

	private List<String[]> getCoverage() {
		List<String[]> coverageMatrix = new ArrayList<>();

		coverageMatrix.add(new String[] {"/test/isa/classeA.2", "/test/isa/classeA.11", "/test/isa/classeA.12",
				"/test/isa/classeB.2", "/test/isa/classeB.13", "/test/isa/classeD.5"}); // test1 - 1, 5 e 7
		coverageMatrix.add(new String[] {"/test/isa/classeA.10", "/test/isa/classeA.24", "/test/isa/classeB.4"}); // test2 - 4 e 6
		coverageMatrix.add(new String[] {"/test/isa/classeA.31", "/test/isa/classeA.32", "/test/isa/classeA.33",
				"/test/isa/classeB.34", "/test/isa/classeB.36", "/test/isa/classeD.39"}); // test3 - nada
		coverageMatrix.add(new String[] {"/test/isa/classeA.42", "/test/isa/classeB.2", "/test/isa/classeB.41",
				"/test/isa/classeB.42", "/test/isa/classeD.5"}); // test4 - 5 e 7
		coverageMatrix.add(new String[] {"/test/isa/classeA.52", "/test/isa/classeA.4", "/test/isa/classeA.54",
				"/test/isa/classeB.2"}); // test5 - 3 e 5
		coverageMatrix.add(new String[] {"/test/isa/classeA.3", "/test/isa/classeA.23", "/test/isa/classeA.24",
				"/test/isa/classeA.10", "/test/isa/classeB.4", "/test/isa/classeD.2"}); // test6 - 2, 4 e 6
		coverageMatrix.add(new String[] {"/test/isa/classeA.25", "/test/isa/classeB.2", "/test/isa/classeD.5",
				"/test/isa/classeE.2"}); // test7 - 5 e 7
		coverageMatrix.add(new String[] {"/test/isa/classeE.82", "/test/isa/classeE.84", "/test/isa/classeE.83",
				"/test/isa/classeE.87", "/test/isa/classeE.86", "/test/isa/classeE.89"}); // test8 - nada
		coverageMatrix.add(new String[] {"/test/isa/classeA.4", "/test/isa/classeA.9", "/test/isa/classeA.96",
				"/test/isa/classeB.92", "/test/isa/classeB.93", "/test/isa/classeD.95"}); // test9 - 3
		coverageMatrix.add(new String[] {"/test/isa/classeA.11", "/test/isa/classeA.12", "/test/isa/classeA.15",
				"/test/isa/classeB.13", "/test/isa/classeB.18", "/test/isa/classeD.5"}); // test10 - 7

		return coverageMatrix;
	}

	private List<String> getBlock() {
		List<String> blockAffected = new ArrayList<>();

		blockAffected.add("/test/isa/classeA.2"); // 1
		blockAffected.add("/test/isa/classeA.3"); // 2
		blockAffected.add("/test/isa/classeA.4"); // 3
		blockAffected.add("/test/isa/classeA.10"); // 4
		blockAffected.add("/test/isa/classeB.2"); // 5
		blockAffected.add("/test/isa/classeB.4"); // 6
		blockAffected.add("/test/isa/classeD.5"); // 7
		return blockAffected;
	}
}
