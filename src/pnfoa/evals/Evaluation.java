package pnfoa.evals;

import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

public class Evaluation implements Comparable<Evaluation> {
	public static final Map<String, Double> critWeights;
	private static final LocalDateTime FIRST_DUE = LocalDateTime.of(2018, 9, 13, 23, 59, 59);
	
	private int id;
	private Game game;
	private Official evaluator;
	private Official official;
	private Position position;
	private LocalDateTime date;
	private Map<String, Integer> scores;
	private Map<String, String> comments;
	
	static {
		critWeights = new HashMap<>();
		critWeights.put("Rules & Mechanics", 0.4);
		critWeights.put("Communication", 0.4);
		critWeights.put("Intangibles", 0.2);
	}

	public Evaluation(int id, Game game, Official evaluator, Official official, Position position, String date) throws ParseException {
		this.id = id;
		this.game = game;
		this.evaluator = evaluator;
		this.official = official;
		this.position = position;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
		this.date = LocalDateTime.parse(date, df);
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
		return dueDateFor(game.getDate());
	}
	
	public static LocalDateTime dueDateFor(LocalDateTime gameDate) {
		LocalDateTime due = gameDate.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
		due = due.withHour(23).withMinute(59).withSecond(59);
		
		while (due.isBefore(FIRST_DUE)) {
			due = due.plusDays(1);
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
	public Position getPosition() { return position; }
	public LocalDateTime getDate() { return date; }
	public Map<String, Integer> getScores() { return scores; }
	public Map<String, String> getComments() { return comments; }
	
	@Override
	public String toString() {
		return String.format("%s (%s) from %s on game #%d: %s", official, position, evaluator, game.getId(), scores);
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
}
