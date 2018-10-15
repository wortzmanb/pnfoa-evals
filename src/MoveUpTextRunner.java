import java.io.*;
import java.util.*;


import pnfoa.evals.*;
import pnfoa.util.CSVParser;
import com.opencsv.*;

public class MoveUpTextRunner {
	public static final String DIRECTORY = "D:\\OneDrive\\PNFOA Board\\2017-18 - Evaluations\\Evals App\\Move-Up";
	
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
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("   " + brett.getNumGamesWorked() + " games worked");
		System.out.println("       " + brett.getGamesWorked());
		System.out.println("   " + brett.getNumEvalsReceived() + " evals received (average = " + brett.getAverageScoreReceived(false) + ")");
		System.out.println("       " + brett.getEvalsReceived());
		System.out.println("   " + brett.getNumEvalsGiven() + " evals given (average = " + brett.getAverageScoreGiven(false) + ")");
		System.out.println("       " + brett.getEvalsGiven());
		System.out.println("       " + brett.getNumEvalsLate() + " late");
		System.out.println("         " + Arrays.toString(brett.getEvalsGiven().stream().filter(e -> e.isLate()).toArray(Evaluation[]::new)));
		System.out.println("       Global Average: " + Evaluation.getGlobalAverage());
		System.out.println("       Adjustment: " + brett.getAdjustment());
		System.out.println("  Test score: " + brett.getTestScore());
		System.out.println("  Participation points: " + brett.getParticipationPoints());
		System.out.println("  Eval average: " + brett.getAverageScoreReceived(true));
		System.out.println("  Late penalty: " + brett.getEvalPenalty());
		System.out.println("  Unadj. COMPOSITE SCORE: " + brett.getCompositeScore(true));
		System.out.println();
		System.out.println("  RANKINGS:");
		System.out.println("    Overall: " + brett.getRank(true) + "/" + Official.getNumRanked(true));
		System.out.println("    Referee: " + brett.getRank(Position.Referee, true) + "/" + Official.getNumRanked(Position.Referee, true));
		System.out.println("    Umpire: " + brett.getRank(Position.Umpire, true) + "/" + Official.getNumRanked(Position.Umpire, true));
		System.out.println("    Head Linesman: " + brett.getRank(Position.HeadLinesman, true) + "/" + Official.getNumRanked(Position.HeadLinesman, true));
		System.out.println("    Line Judge: " + brett.getRank(Position.LineJudge, true) + "/" + Official.getNumRanked(Position.LineJudge, true));
		System.out.println("    Back Judge: " + brett.getRank(Position.BackJudge, true) + "/" + Official.getNumRanked(Position.BackJudge, true));
		System.out.println("    HL/LJ: " + brett.getRank(Position.HL_LJ, true) + "/" + Official.getNumRanked(Position.HL_LJ, true));
		
		// Export composite evals
		System.out.print("Export composite evaluations? ");
		if (kb.next().toLowerCase().startsWith("y")) {
			try {
				CSVWriter writer = new CSVWriter(new FileWriter(DIRECTORY + "\\CompositeEvals.csv"));
				writer.writeNext(getEvalsCsvHeaders());
				for (Evaluation e : evals.values()) {
					writer.writeNext(getEvalsCsvOutput(e));
				}

				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		// Export full rankings
		System.out.print("Export full rankings? ");
		if (kb.next().toLowerCase().startsWith("y")) {
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
		
		// Export mail merge data
		System.out.print("Export mail merge data? ");
		if (kb.next().toLowerCase().startsWith("y")) {		
			try {
				CSVWriter writer = new CSVWriter(new FileWriter(DIRECTORY + "\\MailMergeData.csv"));
				writer.writeNext(getMailMergeHeaders());
				for (Official o : officials.values()) {
					writer.writeNext(getMailMergeOutput(o));
				}

				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		kb.close();
    }
	
	private static String[] getEvalsCsvOutput(Evaluation eval) {
		List<String> values = new ArrayList<>();
		values.add("" + eval.getId());
		values.add("" + eval.getDate());
		values.add("" + eval.getGame());
		values.add("" + eval.getEvaluator());
		values.add("" + eval.getOfficial());
		values.add("" + eval.getCompositeScore());
		for (String crit : Evaluation.critWeights.keySet()) {
			values.add("" + eval.getScores().get(crit));
			values.add(eval.getComments().get(crit));
		}
		values.add(eval.getComments().get("Summary"));		
		return values.toArray(new String[0]);
	}
	
	private static String[] getEvalsCsvHeaders() {
		List<String> headers = new ArrayList<>();
		headers.addAll(Arrays.asList(new String[]{"Id", "Date", "Game", "Evaluator", "Official", "Composite Score"}));
		for (String crit : Evaluation.critWeights.keySet()) {
			headers.add(crit + " Score");
			headers.add(crit + " Comment");
		}
		headers.add("Summary Comment");
		return headers.toArray(new String[0]);
	}	
	
	private static String[] getCsvOutput(Official official) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getTier());
		values.add("" + official.getRank(true));
		values.add("" + official.getTierRank(true));
		values.add("" + official.getNumGamesWorked());
		values.add("" + official.getNumGamesWorked(Level.Varsity));
		values.add("" + official.getCompositeScore(true));
		values.add("" + official.getParticipationPoints());
		values.add("" + official.getTestScore());
		values.add("" + official.getAverageScoreReceived(true));
		values.add("" + official.getEvalPenalty());
		
		return values.toArray(new String[0]);
	}
	
	private static String[] getCsvHeaders() {
		String[] headers = {"Name", "Tier", "Rank", "Tier Rank", "Games Worked", "Varsity Games Worked", "Composite", "Part. Points", "Test Score", "Eval. Avg.", "Penalty"}; 
		return headers;
	}
	
	private static String[] getMailMergeOutput(Official official) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getEmail());
		values.add("" + official.getTier());
		values.add("" + official.getRank(true));
		values.add("" + official.getRank(Position.Referee, true));
		values.add("" + official.getRank(Position.Umpire, true));
		values.add("" + official.getRank(Position.HeadLinesman, true));
		values.add("" + official.getRank(Position.LineJudge, true));
		values.add("" + official.getRank(Position.HL_LJ, true));
		values.add("" + official.getRank(Position.BackJudge, true));
		
		return values.toArray(new String[0]);
	}
	
	private static String[] getMailMergeHeaders() {
		String[] headers = {"Name", "E-mail", "Tier", "Rank", "R Rank", "U Rank", "HL Rank", "LJ Rank", "HL/LJ Rank", "BJ Rank"}; 
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
