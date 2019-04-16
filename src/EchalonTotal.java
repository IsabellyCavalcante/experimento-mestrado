import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EchalonTotal {

	// TODO REFACT AND FIX
	public int[] getSelectedTestSequence() {

//		List<TestCase> copyList = new ArrayList<TestCase>(tests);
//
//		//List<String> suiteList = new ArrayList<String>();
//		ArrayList<Integer> selected = new ArrayList<Integer>();
//
//		ArrayList<TestCaseComparable> weightList = new ArrayList<TestCaseComparable>();
//
//		ArrayList<TestCase> notWeighted = new ArrayList<TestCase>();
//
//		for (TestCase testCase : copyList) {
//			double value = getWeight(testCase.getStatementsCoverage());
//			if (value != 0.0)
//				weightList.add(new TestCaseComparable(value, testCase));
//			else
//				notWeighted.add(testCase);
//		}
//
//		Collections.sort(weightList);
//		for (TestCaseComparable test : weightList) {
//			System.out.println(test.getTestCase().getName() + " " + test.getValue());
//		}
//
//		List<String> weightListStr = new ArrayList<String>();
//		List<String> notWeightListStr = new ArrayList<String>();
//
//		for (int i = weightList.size() - 1; i >= 0; i--) {
//			TestCaseComparable obj = weightList.get(i);
//			String tcSig = obj.getTestCase().getSignature();
//			suiteList.add(tcSig);
//
//			weightListStr.add(tcSig);
//		}
//		this.weightList = weightListStr;
//
//		Collections.shuffle(notWeighted);
//		for (TestCase test : notWeighted) {
//			String tcSig = test.getSignature();
//			suiteList.add(tcSig);
//
//			notWeightListStr.add(tcSig);
//		}
//		this.notWeightList = notWeightListStr;
//		return suiteList;
		return null;
	}
}
