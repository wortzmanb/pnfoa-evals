package pnfoa.evals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Evaluation {
	private int id;
	private Game game;
	private Official evaluator;
	private Official official;
	private Date date;
	private Map<String, Integer> scores;
	private Map<String, String> comments;

	public Evaluation(int id, Game game, Official evaluator, Official official, String date) throws ParseException {
		this.id = id;
		this.game = game;
		this.evaluator = evaluator;
		this.official = official;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm");
		this.date = df.parse(date);
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
	
	public int getId() { return id; }
	public Game getGame() { return game; }
	public Official getEvaluator() { return evaluator; }
	public Official getOfficial() { return official; }
	public Date getDate() { return date; }
	public Map<String, Integer> getScores() { return scores; }
	public Map<String, String> getComments() { return comments; }
	
	public String toString() {
		return String.format("%s from %s on game #%d: %s", official, evaluator, game.getId(), scores);
	}
}
