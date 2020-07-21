import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import com.opencsv.CSVWriter;

import pnfoa.evals.*;
import pnfoa.util.*;

public class PlayoffTextRunner {
	public static final String DIRECTORY = "C:\\Users\\bwort\\OneDrive\\PNFOA Board\\2017-18 - Evaluations\\2019 Playoff Meeting";
	
	private static final LocalDateTime PREV_WEEK6 = LocalDateTime.of(2018, 10, 4, 0, 0, 0);
	private static final LocalDateTime PREV_WEEK10 = LocalDateTime.of(2018, 11, 4, 0, 0, 0);
	private static final boolean V1_EVALS_ONLY = false;

	public static final double PART_POINTS_MAX = 58;
	public static final double TRAIN_POINTS_MAX = 6;
	public static final double EVAL_MAX = 9;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TRAIN_POINTS_WEIGHT = 0.1;
	public static final double PREV_EVAL_WEIGHT = 5.0 / 12.0;
	public static final double CUR_EVAL_WEIGHT = 7.0 / 12.0;
	public static final double EVAL_WEIGHT = 0.8;	
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Directory? ");
		String directoryName = kb.nextLine();
//		String directoryName = DIRECTORY;
		
//		System.out.print("Officials file? ");
//		String offFileName = kb.nextLine();
		Map<String, Official> officials = Official.readOfficials(directoryName + "\\Officials.csv");
		
//		System.out.print("Assignments file? ");
//		String assFileName = kb.nextLine();
		Map<Integer, Game> oldGames = Game.readGames(directoryName + "\\Prev Assignments.csv", officials);
		Map<Integer, Game> games = Game.readGames(directoryName + "\\Assignments.csv", officials);
		
//		System.out.print("Evaluations file? ");
//		String evalFileName = kb.nextLine();
		EvaluationList oldEvals = EvaluationList.readEvals(directoryName + "\\Prev Evaluations.csv", officials, oldGames);
		EvaluationList evals = EvaluationList.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		readPartPoints(directoryName + "\\Participation.csv", officials);
		readTestScores(directoryName + "\\Test.csv", officials);
		readTrainingPoints(directoryName + "\\Training.csv", officials);
		
		oldGames.entrySet().removeIf((Map.Entry<Integer, Game> e) -> e.getValue().getDate().isBefore(PREV_WEEK6));
		oldEvals.removeIf((Evaluation e) -> e.getGame().getDate().isBefore(PREV_WEEK6));
		oldGames.entrySet().removeIf((Map.Entry<Integer, Game> e) -> e.getValue().getDate().isAfter(PREV_WEEK10));
		oldEvals.removeIf((Evaluation e) -> e.getGame().getDate().isAfter(PREV_WEEK10));
		
		if (V1_EVALS_ONLY) {
			oldEvals.removeIf((Evaluation e) -> e.getEvaluator().getTier() != Tier.V1);
			evals.removeIf((Evaluation e) -> e.getEvaluator().getTier() != Tier.V1);
		}
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("  Test score: " + brett.getTestScore());
		System.out.println("  Participation points: " + brett.getParticipationPoints());
		System.out.println("  Training points: " + brett.getTrainingPoints());
		System.out.println("  Current Adjustment: " + evals.getAdjustmentFor(brett));
		System.out.println("  Prev Adjustment: " + oldEvals.getAdjustmentFor(brett));
		System.out.println("  Current Positional Scores: ");
		System.out.printf("    R:  %f (%d games)\n", evals.getAverageReceivedBy(brett, Position.Referee, true), getNumVarsityGamesFor(brett, Position.Referee, games.values()));
		System.out.printf("    U:  %f (%d games)\n", evals.getAverageReceivedBy(brett, Position.Umpire, true), getNumVarsityGamesFor(brett, Position.Umpire, games.values()));
		System.out.printf("    HL: %f (%d games)\n", evals.getAverageReceivedBy(brett, Position.HeadLinesman, true), getNumVarsityGamesFor(brett, Position.HeadLinesman, games.values()));
		System.out.printf("    LJ: %f (%d games)\n", evals.getAverageReceivedBy(brett, Position.LineJudge, true), getNumVarsityGamesFor(brett, Position.LineJudge, games.values()));
		System.out.printf("    BJ: %f (%d games)\n", evals.getAverageReceivedBy(brett, Position.BackJudge, true), getNumVarsityGamesFor(brett, Position.BackJudge, games.values()));
		System.out.println("  Prev Positional Scores: ");
		System.out.printf("    R:  %f (%d games)\n", oldEvals.getAverageReceivedBy(brett, Position.Referee, true), getNumVarsityGamesFor(brett, Position.Referee, oldGames.values()));
		System.out.printf("    U:  %f (%d games)\n", oldEvals.getAverageReceivedBy(brett, Position.Umpire, true), getNumVarsityGamesFor(brett, Position.Umpire, oldGames.values()));
		System.out.printf("    HL: %f (%d games)\n", oldEvals.getAverageReceivedBy(brett, Position.HeadLinesman, true), getNumVarsityGamesFor(brett, Position.HeadLinesman, oldGames.values()));
		System.out.printf("    LJ: %f (%d games)\n", oldEvals.getAverageReceivedBy(brett, Position.LineJudge, true), getNumVarsityGamesFor(brett, Position.LineJudge, oldGames.values()));
		System.out.printf("    BJ: %f (%d games)\n", oldEvals.getAverageReceivedBy(brett, Position.BackJudge, true), getNumVarsityGamesFor(brett, Position.BackJudge, oldGames.values()));
		System.out.println("  COMPOSITE SCORES: ");
		System.out.printf("    R: %f (%d games)\n", getCompositeScore(brett, Position.Referee, true, evals, oldEvals), getNumVarsityGamesFor(brett, Position.Referee, games.values()));
		System.out.printf("    U: %f (%d games)\n", getCompositeScore(brett, Position.Umpire, true, evals, oldEvals), getNumVarsityGamesFor(brett, Position.Umpire, games.values()));
		System.out.printf("    HL: %f (%d games)\n", getCompositeScore(brett, Position.HeadLinesman, true, evals, oldEvals), getNumVarsityGamesFor(brett, Position.HeadLinesman, games.values()));
		System.out.printf("    LJ: %f (%d games)\n", getCompositeScore(brett, Position.LineJudge, true, evals, oldEvals), getNumVarsityGamesFor(brett, Position.LineJudge, games.values()));
		System.out.printf("    BJ: %f (%d games)\n", getCompositeScore(brett, Position.BackJudge, true, evals, oldEvals), getNumVarsityGamesFor(brett, Position.BackJudge, games.values()));

		System.out.println();
		
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
				
				writer = new CSVWriter(new FileWriter(DIRECTORY + "\\PrevCompositeEvals.csv"));
				writer.writeNext(getEvalsCsvHeaders());
				for (Evaluation e : oldEvals) {
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
				CSVWriter overallWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_Overall" + (V1_EVALS_ONLY ? "_V1" : "") + ".csv"));
				CSVWriter refWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_Referee" + (V1_EVALS_ONLY ? "_V1" : "") + ".csv"));
				CSVWriter umpWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_Umpire" + (V1_EVALS_ONLY ? "_V1" : "") + ".csv"));
				CSVWriter hlWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_HeadLines" + (V1_EVALS_ONLY ? "_V1" : "")  + ".csv"));
				CSVWriter ljWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_LineJudge" + (V1_EVALS_ONLY ? "_V1" : "")  + ".csv"));
				CSVWriter bjWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\Rankings_BackJudge" + (V1_EVALS_ONLY ? "_V1" : "")  + ".csv"));
				overallWriter.writeNext(getCsvHeaders());
				refWriter.writeNext(getCsvHeaders());
				umpWriter.writeNext(getCsvHeaders());
				hlWriter.writeNext(getCsvHeaders());
				ljWriter.writeNext(getCsvHeaders());
				bjWriter.writeNext(getCsvHeaders());
				for (Official o : officials.values()) {
					overallWriter.writeNext(getCsvOutput(o, null, games.values(), oldEvals, evals));
					refWriter.writeNext(getCsvOutput(o, Position.Referee, games.values(), oldEvals, evals));
					umpWriter.writeNext(getCsvOutput(o, Position.Umpire, games.values(), oldEvals, evals));
					hlWriter.writeNext(getCsvOutput(o, Position.HeadLinesman, games.values(), oldEvals, evals));
					ljWriter.writeNext(getCsvOutput(o, Position.LineJudge, games.values(), oldEvals, evals));
					bjWriter.writeNext(getCsvOutput(o, Position.BackJudge, games.values(), oldEvals, evals));
				}

				overallWriter.close();
				refWriter.close();
				umpWriter.close();
				hlWriter.close();
				ljWriter.close();
				bjWriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		

		System.out.println("Done!");
		kb.close();
    }
	
	private static int getNumVarsityGamesFor(Official o, Position p, Collection<Game> games) {
		if (p == null) {
			return getNumVarsityGamesFor(o, games);
		}
		return (int)(games.stream().filter((Game g) -> g.getPositionOf(o) == p && g.getLevel() == Level.Varsity).count());
	}
	
	private static int getNumVarsityGamesFor(Official o, Collection<Game> games) {
		return (int)(games.stream().filter((Game g) -> g.getPositionOf(o) != null && g.getLevel() == Level.Varsity).count());
	}	
	
	private static double getCompositeScore(Official o, Position p, boolean adjusted, EvaluationList currEvals, EvaluationList oldEvals) {
		double prevScore = oldEvals.getAverageReceivedBy(o, p, adjusted);
		double currScore = currEvals.getAverageReceivedBy(o, p, adjusted);
		
		if (currScore == 0) return 0;
		if (prevScore == 0) prevScore = currScore;
		
		double partPoints = o.getParticipationPoints();
		double trainPoints = o.getTrainingPoints();
		
		double compEval = prevScore * PREV_EVAL_WEIGHT + currScore * CUR_EVAL_WEIGHT;
		double partScore = Math.min(1.0, partPoints / PART_POINTS_MAX);
		double trainScore = Math.min(1.0,  trainPoints / TRAIN_POINTS_MAX);
		
		return EVAL_WEIGHT * (compEval / EVAL_MAX) + PART_POINTS_WEIGHT * partScore + TRAIN_POINTS_WEIGHT * trainScore;
	}
	
	private static String[] getCsvHeaders() {
		String[] headers = {"Name", "Tier", "Test Score", "Varsity Games", "Games @ Pos", "Part. Points", "Train. Points", "Raw Prev. Eval. Avg.", "Adj. Prev. Eval. Avg.", "Raw Curr. Eval. Avg.", "Adj. Curr. Eval. Avg.", "Composite Score"}; 
		return headers;
	}	
	
	private static String[] getCsvOutput(Official official, Position p, Collection<Game> games, EvaluationList oldEvals, EvaluationList evals) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getTier());
		values.add("" + official.getTestScore());
		values.add("" + getNumVarsityGamesFor(official, games));
		values.add("" + getNumVarsityGamesFor(official, p, games));
		values.add("" + official.getParticipationPoints());
		values.add("" + official.getTrainingPoints());
		values.add("" + oldEvals.getAverageReceivedBy(official, p, false));
		values.add("" + oldEvals.getAverageReceivedBy(official, p, true));
		values.add("" + evals.getAverageReceivedBy(official, p, false));
		values.add("" + evals.getAverageReceivedBy(official, p, true));
		values.add("" + getCompositeScore(official, p, true, evals, oldEvals));
		
		return values.toArray(new String[0]);
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
				
				Official official = officials.get(record.get("Official Name").trim());
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
	
	private static void readTrainingPoints(String fileName, Map<String, Official> officials) {
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official official = officials.get(record.get("Last") + ", " + record.get("First"));
				if (official == null) continue;
				
				String summer = record.get("Summer");
				int summerScore = (summer == null || summer.isEmpty() ? 0 : Integer.parseInt(summer));
				if (summerScore > 0) {
					// training point for first summer meeting, participation points for each other
					official.addSummerTraining();
					official.addPartPoints(5 * (summerScore - 1));
				}
								
				String rto = record.get("RTO");
				int rtoScore = (rto == null || rto.isEmpty() ? 0 : Integer.parseInt(rto));
				if (rtoScore > 0) {
					official.addRTO();
				}
				
				for (int meet = 1; meet <= 6; meet++) {
					String meeting = record.get("Meeting" + meet);
					if (!meeting.trim().isEmpty()) {
						official.addMeeting();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
