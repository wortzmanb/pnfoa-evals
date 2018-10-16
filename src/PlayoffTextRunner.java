import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import com.opencsv.CSVWriter;

import pnfoa.evals.*;
import pnfoa.util.*;

public class PlayoffTextRunner {
	public static final String DIRECTORY = "D:\\OneDrive\\PNFOA Board\\2017-18 - Evaluations\\2018 Playoff Meeting";
	
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
		Map<Integer, Game> oldGames = Game.readGames(directoryName + "\\Prev Assignments.csv", officials);
		Map<Integer, Game> games = Game.readGames(directoryName + "\\Assignments.csv", officials);
		
//		System.out.print("Evaluations file? ");
//		String evalFileName = kb.nextLine();
		EvaluationList oldEvals = EvaluationList.readEvals(directoryName + "\\Prev Evaluations.csv", officials, oldGames);
		EvaluationList evals = EvaluationList.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		readPartPoints(directoryName + "\\Participation.csv", officials);
//		readTestScores(directoryName + "\\Test.csv", officials);
		
		oldGames.entrySet().removeIf((Map.Entry<Integer, Game> e) -> e.getValue().getDate().isBefore(LocalDateTime.of(2017, 10, 19, 0, 0, 0)));
		oldEvals.removeIf((Evaluation e) -> e.getGame().getDate().isBefore(LocalDateTime.of(2017,  10, 19, 0, 0, 0)));
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("  Test score: " + brett.getTestScore());
		System.out.println("  Participation points: " + brett.getParticipationPoints());
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
		
		// Export full rankings
		System.out.print("Export full rankings? ");
		if (kb.next().toLowerCase().startsWith("y")) {
			try {
				CSVWriter refWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\RefereeRankings.csv"));
				CSVWriter umpWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\UmpireRankings.csv"));
				CSVWriter hlWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\HeadLinesRankings.csv"));
				CSVWriter ljWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\LineJudgeRankings.csv"));
				CSVWriter bjWriter = new CSVWriter(new FileWriter(DIRECTORY + "\\BackJudgeRankings.csv"));
				refWriter.writeNext(getCsvHeaders());
				umpWriter.writeNext(getCsvHeaders());
				hlWriter.writeNext(getCsvHeaders());
				ljWriter.writeNext(getCsvHeaders());
				bjWriter.writeNext(getCsvHeaders());
				for (Official o : officials.values()) {
					refWriter.writeNext(getCsvOutput(o, Position.Referee, games.values(), oldEvals, evals));
					umpWriter.writeNext(getCsvOutput(o, Position.Umpire, games.values(), oldEvals, evals));
					hlWriter.writeNext(getCsvOutput(o, Position.HeadLinesman, games.values(), oldEvals, evals));
					ljWriter.writeNext(getCsvOutput(o, Position.LineJudge, games.values(), oldEvals, evals));
					bjWriter.writeNext(getCsvOutput(o, Position.BackJudge, games.values(), oldEvals, evals));
				}

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

		kb.close();
    }
	
	private static int getNumVarsityGamesFor(Official o, Position p, Collection<Game> games) {
		return (int)(games.stream().filter((Game g) -> g.getPositionOf(o) == p && g.getLevel() == Level.Varsity).count());
	}
	
	private static double getCompositeScore(Official o, Position p, boolean adjusted, EvaluationList currEvals, EvaluationList oldEvals) {
		double prevScore = oldEvals.getAverageReceivedBy(o, p, adjusted);
		double currScore = currEvals.getAverageReceivedBy(o, p, adjusted);
		
		if (currScore == 0) return 0;
		if (prevScore == 0) prevScore = currScore;
		
		double partPoints = o.getParticipationPoints();
		
		double compEval = (prevScore * 5.0 / 12.0 + currScore * 7.0 / 12.0);
		double partScore = Math.min(1.0, partPoints / 58.0);
		
		return 0.9 * (compEval / 9.0) + 0.1 * partScore;
	}
	
	private static String[] getCsvHeaders() {
		String[] headers = {"Name", "Tier", "Varsity Games Worked", "Part. Points", "2017 Eval. Avg.", "2018 Eval. Avg.", "Composite Score"}; 
		return headers;
	}	
	
	private static String[] getCsvOutput(Official official, Position p, Collection<Game> games, EvaluationList oldEvals, EvaluationList evals) {
		List<String> values = new ArrayList<>();
		values.add("" + official.getName());
		values.add("" + official.getTier());
		values.add("" + getNumVarsityGamesFor(official, p, games));
		values.add("" + official.getParticipationPoints());
		values.add("" + oldEvals.getAverageReceivedBy(official, p, true));
		values.add("" + evals.getAverageReceivedBy(official, p, true));
		values.add("" + getCompositeScore(official, p, true, evals, oldEvals));
		
		return values.toArray(new String[0]);
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
