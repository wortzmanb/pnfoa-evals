import java.io.FileNotFoundException;
import java.util.*;


import pnfoa.evals.*;
import pnfoa.util.CSVParser;

public class TextRunner {
	public static final String DIRECTORY = "D:\\OneDrive\\PNFOA Board\\2017 - Evaluations\\Evals App\\Move-Up";
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
//		System.out.print("Directory? ");
//		String directoryName = kb.nextLine();
		String directoryName = DIRECTORY;
		
//		System.out.print("Officials file? ");
//		String offFileName = kb.nextLine();
		Map<String, Official> officials = Official.readOfficials(directoryName + "\\Officials.csv");
		
//		System.out.print("Assignments file? ");
//		String assFileName = kb.nextLine();		
		Map<Integer, Game> games = Game.readGames(directoryName + "\\Assignments.csv", officials);
		
//		System.out.print("Evaluations file? ");
//		String evalFileName = kb.nextLine();
		Map<Integer, Evaluation> evals = Evaluation.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		readPartPoints(directoryName + "\\Participation.csv", officials);
		readTestScores(directoryName + "\\Test.csv", officials);
		
		kb.close();
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("   " + brett.getNumGamesWorked() + " games worked");
		System.out.println("       " + brett.getGamesWorked());
		System.out.println("   " + brett.getNumEvalsReceived() + " evals received (average = " + brett.getAverageScoreReceived() + ")");
		System.out.println("       " + brett.getEvalsReceived());
		System.out.println("   " + brett.getNumEvalsGiven() + " evals given (average = " + brett.getAverageScoreGiven() + ")");
		System.out.println("       " + brett.getEvalsGiven());
		System.out.println("       " + brett.getNumEvalsLate() + " late");
		System.out.println("         " + Arrays.toString(brett.getEvalsGiven().stream().filter(e -> e.isLate()).toArray(Evaluation[]::new)));
		System.out.println("       Global Average: " + Evaluation.getGlobalAverage());
		System.out.println("       Adjustment: " + brett.getAdjustment());
		System.out.println("  Test score: " + brett.getTestScore());
		System.out.println("  Participation points: " + brett.getParticipationPoints());
		System.out.println("  Eval average: " + brett.getAverageScoreReceived());
		System.out.println("  Late penalty: " + brett.getEvalPenalty());
		System.out.println("  Unadj. COMPOSITE SCORE: " + brett.getCompositeScore());
    }
	
	private static void readPartPoints(String fileName, Map<String, Official> officials) {
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official official = officials.get(record.get("Official Name"));
				if (official == null) continue;
				
				official.addPartPoints(Integer.parseInt(record.get("Points")));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void readTestScores(String fileName, Map<String, Official> officials) {
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official official = officials.get(record.get("Official Name"));
				if (official == null) continue;
				
				String strScore1 = record.get("Test Score");
				double score1 = strScore1 == null || strScore1.isEmpty() ? 0 : Double.parseDouble(strScore1);
				
				String strScore2 = record.get("Test Score 2");
				double score2 = strScore2 == null || strScore2.isEmpty() ? 0 : Double.parseDouble(strScore2);
				
				official.setTestScore(score1);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
