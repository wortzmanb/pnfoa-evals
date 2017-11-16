package pnfoa.evals;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import pnfoa.util.CSVParser;

public class Evaluation implements Comparable<Evaluation> {
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
	
	public static Map<Integer, Evaluation> readEvals(String fileName, Map<String, Official> officials, Map<Integer, Game> games) {
		Map<Integer, Evaluation> evals = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				Official evaluator = officials.get(record.get("Evaluator Name"));
				Official official = officials.get(record.get("Official Name"));
				Game game = games.get(Integer.parseInt(record.get("Game ID")));
				
				int id = Integer.parseInt(record.get("Evaluation ID"));
				if (!evals.containsKey(id)) {
					evals.put(id, new Evaluation(id,
												 game,
												 evaluator,
												 official,
												 record.get("Date Submitted")));
				}
				Evaluation eval = evals.get(id);
				eval.addScore(record.get("Evaluation Criteria Name"), Integer.parseInt(record.get("Criteria Value")), record.get("Criteria Comments"));
				eval.addSummaryComment(record.get("Summary Comments"));
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
	
	public int getId() { return id; }
	public Game getGame() { return game; }
	public Official getEvaluator() { return evaluator; }
	public Official getOfficial() { return official; }
	public Date getDate() { return date; }
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
}
