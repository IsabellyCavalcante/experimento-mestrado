
public class MainCoverage {

	public static void main(String[] args) {
		GreedyTotal gt = new GreedyTotal("src/dados", "coverage-v5.txt");
		gt.Print(gt.getSelectedTestSequence());
		
		System.out.println("add");

//		GreedyAdditional ga = new GreedyAdditional("src/dados", "coverage-v5.txt");
//		ga.Print(ga.getSelectedTestSequence());

	}
}
