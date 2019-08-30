package tests;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import main.ARTMaxMin;

public class ARTMaxMinTest {
	
	private ARTMaxMin random;

	@Before
	public void setUp() throws Exception {
		random = new ARTMaxMin(getCoverageMatrix());
	}

	@Test
	public void test() {
		LocalDateTime now = LocalDateTime.now();
		random.extractToFile(random.getSelectedTestSequence(), now, "teste");
	}
	
	private char[][] getCoverageMatrix() {
		char[][] coverageMatrix = new char[10][5];

		coverageMatrix[0] = new char[] {'0', '1', '0', '1', '0'};
		coverageMatrix[1] = new char[] {'1', '1', '1', '0', '0'};
		coverageMatrix[2] = new char[] {'0', '0', '0', '0', '0'};
		coverageMatrix[3] = new char[] {'1', '1', '0', '1', '0'};
		coverageMatrix[4] = new char[] {'1', '0', '1', '1', '1'};
		coverageMatrix[5] = new char[] {'0', '0', '1', '0', '0'};
		coverageMatrix[6] = new char[] {'1', '0', '0', '0', '0'};
		coverageMatrix[7] = new char[] {'1', '1', '1', '1', '0'};
		coverageMatrix[8] = new char[] {'1', '0', '0', '1', '0'};
		coverageMatrix[9] = new char[] {'1', '1', '0', '0', '0'};

		return coverageMatrix;
	}
}
