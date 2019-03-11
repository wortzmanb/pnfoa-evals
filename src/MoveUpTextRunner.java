import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import pnfoa.evals.*;
import pnfoa.util.CSVParser;
import com.opencsv.*;

public class MoveUpTextRunner {
	public static final String DIRECTORY = "D:\\OneDrive\\PNFOA Board\\2017-18 - Evaluations\\2018 Move-up Meeting\\February";

	public static final double PART_POINTS_MAX = 100;
	public static final double EVAL_MAX = 9;
	public static final double TEST_MAX = 100;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TEST_WEIGHT = 0.2;
	public static final double EVAL_WEIGHT = 0.7;
	
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private EvaluationList evals;
	
	private Map<Position, List<Official>> rankings;
	
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
		runner.printOutputFor(brett, evals);
		
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
		return getCompositeScore(o.getParticipationPoints(), o.getTestScore(), evals.getAverageReceivedBy(o, null, adjusted), o.getEvalPenalty());
	}
	
	private double getCompositeScoreFor(Official o, Position pos, boolean adjusted) {
		return getCompositeScore(o.getParticipationPoints(), o.getTestScore(), evals.getAverageReceivedBy(o, pos, adjusted), o.getEvalPenalty());
	}	
	private double getCompositeScore(double part, double test, double evals, double penalty) {
		return (part / PART_POINTS_MAX * PART_POINTS_WEIGHT) + 
			   (test / TEST_MAX * TEST_WEIGHT) +
			   ((evals - penalty) / EVAL_MAX * EVAL_WEIGHT);
	}
	
	private List<Official> getRankings(Position pos, boolean adjusted) {
		if (rankings == null) {
			rankings = new HashMap<>();
		}
		if (!rankings.containsKey(pos)) {
			List<Official> ranks = new ArrayList<>();
			ranks.addAll(this.officials.values());
			
			rankings.put(pos, ranks.stream().filter((Official o) -> o.getNumGamesWorked(Level.Varsity, pos) > 0)
								 .sorted((Official o1, Official o2) -> Double.compare(getCompositeScoreFor(o2, pos, adjusted), getCompositeScoreFor(o1, pos, adjusted)))
								 .collect(Collectors.toList()));
		}
		return rankings.get(pos);
	}
	
	private void printOutputFor(Official o, EvaluationList evals) {
		System.out.println(o + ": ");
		System.out.println("   " + o.getNumGamesWorked() + " games worked");
		System.out.println("       " + o.getGamesWorked());
		System.out.println("   " + o.getNumEvalsReceived() + " evals received (average = " + evals.getAverageReceivedBy(o, null, false));
		System.out.println("       " + o.getEvalsReceived());
		System.out.println("   " + o.getNumEvalsGiven() + " evals given (average = " + evals.getAverageGivenBy(o, false));
		System.out.println("       " + o.getEvalsGiven());
		System.out.println("       " + o.getNumEvalsLate() + " late");
		System.out.println("         " + Arrays.toString(o.getEvalsGiven().stream().filter(e -> e.isLate()).toArray(Evaluation[]::new)));
		System.out.println("       Global Average: " + evals.getAverage(false));
		System.out.println("       Adjustment: " + evals.getAdjustmentFor(o));
		System.out.println("  Test score: " + o.getTestScore());
		System.out.println("  Participation points: " + o.getParticipationPoints());
		System.out.println("  Unadj. Eval average: " + evals.getAverageReceivedBy(o, null, false));
		System.out.println("  Adj. Eval average: " + evals.getAverageReceivedBy(o, null, true));
		System.out.println("  Late penalty: " + o.getEvalPenalty());
		System.out.println("  Adj. COMPOSITE SCORE: " + getCompositeScoreFor(o, true));
		System.out.println();
		
		System.out.println("  POSITION RATINGS:");
		printRatings(o, null);
		printRatings(o, Position.Referee);
		printRatings(o, Position.Umpire);
		printRatings(o, Position.HeadLinesman);
		printRatings(o, Position.LineJudge);
		printRatings(o, Position.BackJudge);
		printRatings(o, Position.HL_LJ);
		
		System.out.println("  RANKINGS:");
		printRankings(o, null);
		printRankings(o, Position.Referee);
		printRankings(o, Position.Umpire);
		printRankings(o, Position.HeadLinesman);
		printRankings(o, Position.LineJudge);
		printRankings(o, Position.BackJudge);
		printRankings(o, Position.HL_LJ);
	}
	
	private void printRatings(Official o, Position pos) {
		System.out.printf("     %s: %f - %f (%d games)\n", pos == null ? "Overall" : pos, evals.getAverageReceivedBy(o, pos, true), getCompositeScoreFor(o, pos, true), o.getNumGamesWorked(Level.Varsity, pos));
	}
	
	private void printRankings(Official o, Position pos) {
		List<Official> ranks = getRankings(pos, true);
		System.out.printf("     %s: %d/%d\n", pos == null ? "Overall" : pos, getRankingFor(o, pos), ranks.size());
	}
	
	private int getRankingFor(Official o, Position pos) {
		List<Official> ranks = getRankings(pos, true);
		int rank = ranks.indexOf(o);
		return (rank < 0 ? Integer.MAX_VALUE : rank + 1);
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
		values.add("" + official.getNumGamesWorked());
		values.add("" + official.getNumGamesWorked(Level.Varsity));
		values.add("" + getCompositeScoreFor(official, true));
		values.add("" + official.getParticipationPoints());
		values.add("" + official.getTestScore());
		values.add("" + evals.getAverageReceivedBy(official, null, true));
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
		values.add("" + getRankingFor(official, null));
		values.add("" + getRankingFor(official, Position.Referee));
		values.add("" + getRankingFor(official, Position.Umpire));
		values.add("" + getRankingFor(official, Position.HeadLinesman));
		values.add("" + getRankingFor(official, Position.LineJudge));
		values.add("" + getRankingFor(official, Position.HL_LJ));
		values.add("" + getRankingFor(official, Position.BackJudge));
		
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
