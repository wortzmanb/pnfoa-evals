package pnfoa.evals;

import java.io.*;
import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

import pnfoa.util.*;

public class Evaluation implements Comparable<Evaluation> {
	private static Map<String, Double> critWeights;
	private static final LocalDateTime FIRST_DUE = LocalDateTime.of(2017, 9, 13, 23, 59, 59);
	
	private static Collection<Evaluation> allEvals;
	private static double globalAverage;
	private static boolean isAverageStale;
	
	private int id;
	private Game game;
	private Official evaluator;
	private Official official;
	private LocalDateTime date;
	private Map<String, Integer> scores;
	private Map<String, String> comments;
	
	static {
		allEvals = new HashSet<>();
		critWeights = new HashMap<>();
		critWeights.put("Rules & Mechanics", 0.4);
		critWeights.put("Communication", 0.4);
		critWeights.put("Intangibles", 0.2);
	}

	public Evaluation(int id, Game game, Official evaluator, Official official, String date) throws ParseException {
		this.id = id;
		this.game = game;
		this.evaluator = evaluator;
		this.official = official;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
		this.date = LocalDateTime.parse(date, df);
		allEvals.add(this);
	}
	
	public void addScore(String criterion, int score) {
		addScore(criterion, score, null);
	}
	
	public void addScore(String criterion, int score, String comment) {
		if (scores == null) {
			scores = new TreeMap<>();
		}
		scores.put(criterion, score);
		
		if (comments == null) {
			comments = new TreeMap<>();
		}
		comments.put(criterion, comment);
		isAverageStale = true;
	}

	public void addSummaryComment(String comment) {
		if (comments == null) {
			comments = new TreeMap<>();
		}
		comments.put("Summary", comment);
	}
	
	public double getCompositeScore() {
		double score = 0;
		for (String crit : scores.keySet()) {
			if (critWeights.containsKey(crit)) {
				double critWeight = critWeights.get(crit);
				double critScore = scores.get(crit);
				score += (critWeight * critScore);
			}
		}
		
		return score;
	}
	
	public LocalDateTime dueDate() {
		LocalDateTime due = game.getDate().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
		due = due.withHour(23).withMinute(59).withSecond(59);
		
		while (due.isBefore(FIRST_DUE)) {
			due = due.plusWeeks(1);
		}
		return due;
	}
	
	public boolean isLate() {
		return date.isAfter(dueDate());
	}

	public int getId() { return id; }
	public Game getGame() { return game; }
	public Official getEvaluator() { return evaluator; }
	public Official getOfficial() { return official; }
	public LocalDateTime getDate() { return date; }
	public Map<String, Integer> getScores() { return scores; }
	public Map<String, String> getComments() { return comments; }
	
	@Override
	public String toString() {
		return String.format("%s from %s on game #%d: %s", official, evaluator, game.getId(), scores);
	}
	
	@Override
	public int compareTo(Evaluation other) {
		return this.getId() - other.getId();
	}
	
	public boolean equals(Evaluation other) {
		return this.compareTo(other) == 0;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(this.getId());
	}
	
	public static double getGlobalAverage() {
		if (isAverageStale) {
			globalAverage = allEvals.stream()
									.filter((Evaluation e) -> e.getEvaluator().getTier() != Tier.A1 && e.getEvaluator().getTier() != Tier.A2)
									.mapToDouble(Evaluation::getCompositeScore)
									.average()
									.getAsDouble();
			isAverageStale = false;
		}
		return globalAverage;
	}
	
	public static Map<Integer, Evaluation> readEvals(String fileName, Map<String, Official> officials, Map<Integer, Game> games) {
		Map<Integer, Evaluation> evals = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official evaluator = officials.get(record.get("Evaluator_Name"));
				Official official = officials.get(record.get("Official_Name"));
				Game game = games.get(Integer.parseInt(record.get("GameID")));
				
				int id = Integer.parseInt(record.get("Evaluation_ID"));
				if (!evals.containsKey(id)) {
					evals.put(id, new Evaluation(id,
												 game,
												 evaluator,
												 official,
												 record.get("Date_Submitted")));
				}
				Evaluation eval = evals.get(id);
				eval.addScore(record.get("Evaluation_Criteria_Name"), Integer.parseInt(record.get("Criteria_Value")), record.get("Criteria_Comments"));
				eval.addSummaryComment(record.get("Summary_Comments"));
				
				// don't count evaluations from Apprentices
				if (evaluator.getTier() == Tier.A1 || evaluator.getTier() == Tier.A2) {
					System.out.println("Skipping Apprentice evaluation: " + eval);
					continue;
				}
				
				// don't count evaluations from officials not on the crew
				Position evalPos = game.getPositionOf(evaluator); 
				if (evalPos != null && evalPos != Position.Referee && evalPos != Position.Umpire && 
					evalPos != Position.HeadLinesman && evalPos != Position.LineJudge && evalPos != Position.BackJudge) {
					System.out.println("Skipping non-crew evaluation: " + eval);
					continue;
				}
				
				if (evaluator.getEvalsGiven() == null || !evaluator.getEvalsGiven().contains(eval)) 
					evaluator.addEvalGiven(eval);
				if (official.getEvalsReceived() == null || !official.getEvalsReceived().contains(eval))
					official.addEvalReceived(eval);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(evals.size() + " evaluations read");
		return evals;
	}
}
