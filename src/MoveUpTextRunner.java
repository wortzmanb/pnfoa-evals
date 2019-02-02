import java.io.*;
import java.util.*;

import pnfoa.evals.*;
import pnfoa.util.CSVParser;
import com.opencsv.*;

public class MoveUpTextRunner {
	public static final String DIRECTORY = "C:\\Users\\bwort\\OneDrive\\PNFOA Board\\2017-18 - Evaluations\\2018 Move-up Meeting";

	public static final double PART_POINTS_MAX = 100;
	public static final double EVAL_MAX = 9;
	public static final double TEST_MAX = 100;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TEST_WEIGHT = 0.2;
	public static final double EVAL_WEIGHT = 0.7;
	
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private EvaluationList evals;
	
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
		EvaluationList evals = EvaluationList.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		MoveUpTextRunner runner = new MoveUpTextRunner(officials, games, evals);
		
		readPartPoints(directoryName + "\\Participation.csv", officials);
		readTestScores(directoryName + "\\Test.csv", officials);
		
		Official brett = officials.get("Wortzman, Brett");
		runner.printOutputFor(brett);
		
		// Export composite evals
		System.out.print("Export composite evaluations? ");
		if (kb.next().toLowerCase().startsWith("y")) {
			try {
				CSVWriter writer = new CSVWriter(new FileWriter(DIRECTORY + "\\CompositeEvals.csv"));
				writer.writeNext(getEvalsCsvHeaders());
				for (Evaluation e : evals) {
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
					writer.writeNext(runner.getCsvOutput(o));
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
					writer.writeNext(runner.getMailMergeOutput(o));
				}

				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		kb.close();
    }
	
	public MoveUpTextRunner(Map<String, Official> officials, Map<Integer, Game> games, EvaluationList evals) {
		this.officials = officials;
		this.games = games;
		this.evals = evals;
	}
	
	private double getCompositeScoreFor(Official o, boolean adjusted) {
		return getCompositeScore(o.getParticipationPoints(), o.getTestScore(), evals.getAverageReceivedBy(o, adjusted), o.getEvalPenalty());
	}
	
	private double getCompositeScoreFor(Official o, Position pos, boolean adjusted) {
		return getCompositeScore(o.getParticipationPoints(), o.getTestScore(), evals.getAverageReceivedBy(o, pos, adjusted), o.getEvalPenalty());
	}	
	private double getCompositeScore(double part, double test, double evals, double penalty) {
		return (part / PART_POINTS_MAX * PART_POINTS_WEIGHT) + 
			   (test / TEST_MAX * TEST_WEIGHT) +
			   ((evals - penalty) / EVAL_MAX * EVAL_WEIGHT);
	}
	
	private void printOutputFor(Official o) {
		System.out.println(o + ": ");
		System.out.println("   " + o.getNumGamesWorked() + " games worked");
		System.out.println("       " + o.getGamesWorked());
		System.out.println("   " + o.getNumEvalsReceived() + " evals received (average = " + evals.getAverageReceivedBy(o, false));
		System.out.println("       " + o.getEvalsReceived());
		System.out.println("   " + o.getNumEvalsGiven() + " evals given (average = " + evals.getAverageGivenBy(o, false));
		System.out.println("       " + o.getEvalsGiven());
		System.out.println("       " + o.getNumEvalsLate() + " late");
		System.out.println("         " + Arrays.toString(o.getEvalsGiven().stream().filter(e -> e.isLate()).toArray(Evaluation[]::new)));
		System.out.println("       Global Average: " + evals.getAverage(false));
		System.out.println("       Adjustment: " + evals.getAdjustmentFor(o));
		System.out.println("  Test score: " + o.getTestScore());
		System.out.println("  Participation points: " + o.getParticipationPoints());
		System.out.println("  Eval average: " + evals.getAverageReceivedBy(o, false));
		System.out.println("  Late penalty: " + o.getEvalPenalty());
		System.out.println("  Unadj. COMPOSITE SCORE: " + getCompositeScoreFor(o, true));
		System.out.println();
//		System.out.println("  RANKINGS:");
//		System.out.println("    Overall: " + o.getRank(true) + "/" + Official.getNumRanked(true));
//		System.out.println("    Referee: " + o.getRank(Position.Referee, true) + "/" + Official.getNumRanked(Position.Referee, true));
//		System.out.println("    Umpire: " + o.getRank(Position.Umpire, true) + "/" + Official.getNumRanked(Position.Umpire, true));
//		System.out.println("    Head Linesman: " + o.getRank(Position.HeadLinesman, true) + "/" + Official.getNumRanked(Position.HeadLinesman, true));
//		System.out.println("    Line Judge: " + o.getRank(Position.LineJudge, true) + "/" + Official.getNumRanked(Position.LineJudge, true));
//		System.out.println("    Back Judge: " + o.getRank(Position.BackJudge, true) + "/" + Official.getNumRanked(Position.BackJudge, true));
//		System.out.println("    HL/LJ: " + o.getRank(Position.HL_LJ, true) + "/" + Official.getNumRanked(Position.HL_LJ, true));
	}
	
	private static String[] getEvalsCsvOutput(Evaluation eval) {
		List<String> values = new ArrayList<>();
		values.add("" + eval.getId());
		values.add("" + eval.getDate());
		values.add("" + eval.getGame());
		values.add("" + eval.getEvaluator());
		values.add("" + eval.getOfficial());
		values.add("" + eval.getCompositeScore());
		values.add("" + eval.isLate());
		for (String crit : Evaluation.critWeights.keySet()) {
			values.add("" + eval.getScores().get(crit));
			values.add(eval.getComments().get(crit));
		}
		values.add(eval.getComments().get("Summary"));		
		return values.toArray(new String[0]);
	}
	
	private static String[] getEvalsCsvHeaders() {
		List<String> headers = new ArrayList<>();
		headers.addAll(Arrays.asList(new String[]{"Id", "Date", "Game", "Evaluator", "Official", "Composite Score", "Late?"}));
		for (String crit : Evaluation.critWeights.keySet()) {
			headers.add(crit + " Score");
			headers.add(crit + " Comment");
		}
		headers.add("Summary Comment");
		return headers.toArray(new String[0]);
	}	
	
	private String[] getCsvOutput(Official official) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getTier());
//		values.add("" + official.getRank(true));
//		values.add("" + official.getTierRank(true));
		values.add("" + official.getNumGamesWorked());
		values.add("" + official.getNumGamesWorked(Level.Varsity));
		values.add("" + getCompositeScoreFor(official, true));
		values.add("" + official.getParticipationPoints());
		values.add("" + official.getTestScore());
		values.add("" + evals.getAverageReceivedBy(official, true));
		values.add("" + official.getEvalPenalty());
		
		return values.toArray(new String[0]);
	}
	
	private static String[] getCsvHeaders() {
		String[] headers = {"Name", "Tier", "Games Worked", "Varsity Games Worked", "Composite", "Part. Points", "Test Score", "Eval. Avg.", "Penalty"}; 
		return headers;
	}
	
	private String[] getMailMergeOutput(Official official) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getEmail());
		values.add("" + official.getTier());
//		values.add("" + official.getRank(true));
//		values.add("" + official.getRank(Position.Referee, true));
//		values.add("" + official.getRank(Position.Umpire, true));
//		values.add("" + official.getRank(Position.HeadLinesman, true));
//		values.add("" + official.getRank(Position.LineJudge, true));
//		values.add("" + official.getRank(Position.HL_LJ, true));
//		values.add("" + official.getRank(Position.BackJudge, true));
		
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
