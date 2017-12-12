import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


import pnfoa.evals.*;
import pnfoa.util.CSVParser;
import com.opencsv.*;

public class TextRunner {
	public static final String DIRECTORY = "C:\\Users\\brettwo\\OneDrive\\PNFOA Board\\2017 - Evaluations\\Evals App\\Move-Up";
	
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
		
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(DIRECTORY + "\\GeneratedRankings.csv"));
			writer.writeNext(getCsvHeaders());
			for (Official o : officials.values()) {
				writer.writeNext(getCsvOutput(o));
			}
			
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    }
	
	
	private static String[] getCsvOutput(Official official) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getTier());
		values.add("" + official.getAdjustedRank());
		values.add("" + official.getAdjustedTierRank());
		values.add("" + official.getNumGamesWorked());
		values.add("" + official.getNumGamesWorked(Level.Varsity));
		values.add("" + official.getAdjustedComposite());
		values.add("" + official.getParticipationPoints());
		values.add("" + official.getTestScore());
		values.add("" + official.getAverageScoreReceived());
		values.add("" + official.getEvalPenalty());
		
		return values.toArray(new String[0]);
	}
	
	private static String[] getCsvHeaders() {
		String[] headers = {"Name", "Tier", "Rank", "Tier Rank", "Games Worked", "Varsity Games Worked", "Composite", "Part. Points", "Test Score", "Eval. Avg.", "Penalty"}; 
		return headers;
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
