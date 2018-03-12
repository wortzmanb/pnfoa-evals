package pnfoa.evals;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import pnfoa.util.*;

public class Official implements Comparable<Official> {
	private String firstName;
	private String lastName;
	private String email;
	private Tier tier;
	private Map<Game, Position> gamesWorked;
	private Collection<Evaluation> evalsGiven;
	private Collection<Evaluation> evalsReceived;
	private int partPoints;
	private double testScore;
	private int rank;
	
	public static final double PART_POINTS_MAX = 100;
	public static final double EVAL_MAX = 9;
	public static final double TEST_MAX = 100;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TEST_WEIGHT = 0.2;
	public static final double EVAL_WEIGHT = 0.7;
	
	private static List<Official> allOfficials;
	
	static {
		allOfficials = new ArrayList<>();
	}
	
	public Official(String firstName, String lastName, String email, String tier) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.tier = Tier.parse(tier);
		this.rank = -1;
		allOfficials.add(this);
	}
	
	public Official(String firstName, String lastName) {
		this(firstName, lastName, null, null);
	}
	
	public void addGame(Game g, Position p) {
		if (gamesWorked == null) {
			gamesWorked = new HashMap<>();
		}
		
		gamesWorked.put(g, p);
		addPartPoints(g.getPartPointsFor(getTier()));
	}
	
	public void addEvalGiven(Evaluation e) {
		if (evalsGiven == null) {
			evalsGiven = new HashSet<>();
		}
		evalsGiven.add(e);
	}
	
	public void addEvalReceived(Evaluation e) {
		if (evalsReceived == null) {
			evalsReceived = new ArrayList<>();
		}
		evalsReceived.add(e);
	}
	
	public void addPartPoints(int points) {
		partPoints += points;
		partPoints = Math.min(partPoints, 100);
	}
	
	public void setTestScore(double score) {
		testScore = score;
	}

	public double getAverageScoreGiven(boolean adjusted) {
		return getAverage(evalsGiven, adjusted);
	}

	public double getAverageScoreReceived(boolean adjusted) {
		return getAverage(evalsReceived, adjusted);
	}
	
	public double getAverageScoreReceived(Position pos, boolean adjusted) {
		if (getNumGamesWorked(pos) == 0) return Double.NEGATIVE_INFINITY; 
				
		return getAverage(evalsReceived
				.stream()
				.filter(e -> e.getGame().getPositionOf(e.getOfficial()) == pos)
				.collect(Collectors.toList()), adjusted);
	}
	
	private static double getAverage(Collection<Evaluation> evals, boolean adjusted) {
		if (evals == null || evals.size() == 0) return Double.NEGATIVE_INFINITY;
		
		double total = 0;
		for (Evaluation eval : evals) {
			total += (eval.getCompositeScore() + (adjusted ? eval.getEvaluator().getAdjustment() : 0));
		}
		return (total / evals.size());
	}

	public double getEvalPenalty() {
		if (getGamesWorked() == null) return 0.0;
		
		Set<Game> missed = new HashSet<>();
		for (Game g : getGamesWorked()) {
			if (g.getLevel() == Level.Varsity) {
				boolean ok = false;
				if (getEvalsGiven() != null) {
					for (Evaluation e : getEvalsGiven()) {
						if (e.getGame().equals(g) && !e.isLate()) {
							ok = true;
						}
					}
				}
				if (!ok) {
					missed.add(g);
				}
			}
		}
		
		switch (missed.size()) {
			case 0:
			case 1: return 0.00;
			case 2: return 0.25;
			case 3: return 0.50;
			default: return 1.00;
		}
	}
	
	public double getAdjustment() {
		return Evaluation.getGlobalAverage() - getAverageScoreGiven(false);
	}
	
	public double getCompositeScore(boolean adjusted) {
		return getCompositeScore(this.getParticipationPoints(), this.getTestScore(), this.getAverageScoreReceived(adjusted));
	}

	
	public double getCompositeScore(Position pos, boolean adjusted) { 
		return getCompositeScore(this.getParticipationPoints(), this.getTestScore(), this.getAverageScoreReceived(pos, adjusted));
	}
	
	private double getCompositeScore(double part, double test, double evals) {
		return (part / PART_POINTS_MAX * PART_POINTS_WEIGHT) + 
			   (test / TEST_MAX * TEST_WEIGHT) +
			   ((evals - getEvalPenalty()) / EVAL_MAX * EVAL_WEIGHT);
	}
	
	public static void calculateRanks(boolean adjusted) {
		allOfficials.sort((Official o1, Official o2) -> Double.compare(o2.getCompositeScore(adjusted), o1.getCompositeScore(adjusted)));
		for (int i = 0; i < allOfficials.size(); i++) {
			allOfficials.get(i).rank = (i + 1);
		}
	}

	public int getTierRank(boolean adjusted) {
		if (this.rank < 0) calculateRanks(adjusted);
		int tierCount = 0;
		for (int i = 0; i < this.rank; i++) {
			if (allOfficials.get(i).getTier() == this.getTier()) {
				tierCount++;
			}
		}
		return tierCount;
	}
	
	public Collection<Game> getGamesWorked(Position pos) { 
		return this.gamesWorked == null ? null : 
			   this.gamesWorked.entrySet().stream()
				.filter(ent -> ent.getValue() == pos)
				.map(ent -> ent.getKey())
				.collect(Collectors.toList()); 
	}
	
	public Collection<Game> getGamesWorked(Level l) { 
		return this.gamesWorked == null ? null :
			   this.gamesWorked.entrySet().stream()
				.filter(ent -> ent.getKey().getLevel() == l)
				.map(ent -> ent.getKey())
				.collect(Collectors.toList()); 
	}

	public Collection<Game> getGamesWorked(Level l, Position pos) { 
		return this.gamesWorked == null ? null :
			   this.gamesWorked.entrySet().stream()
				.filter(ent -> ent.getKey().getLevel() == l)
				.filter(ent -> ent.getValue() == pos)
				.map(ent -> ent.getKey())
				.collect(Collectors.toList()); 
	}

	public int getNumGamesWorked() { 
		return this.gamesWorked == null ? 0 : this.gamesWorked.size(); 
	}
	
	public int getNumGamesWorked(Position pos) { 
		return this.gamesWorked == null ? 0 : getGamesWorked(pos).size(); 
	}

	public int getNumGamesWorked(Level level) { 
		return this.gamesWorked == null ? 0 : getGamesWorked(level).size(); 
	}	
	
	public String getName() { return this.lastName + ", " + this.firstName; }
	public String getEmail() { return this.email; }
	public Tier getTier() { return this.tier; }
	public Collection<Game> getGamesWorked() { return this.gamesWorked == null ? null : this.gamesWorked.keySet(); }
	public Map<Game, Position> getAssignments() { return this.gamesWorked; }
	public Collection<Evaluation> getEvalsGiven() { return this.evalsGiven; }
	public int getNumEvalsGiven() { return this.evalsGiven == null ? 0 : this.evalsGiven.size(); }
	public int getNumEvalsLate() { return this.evalsGiven == null ? 0 : (int)this.evalsGiven.stream().filter(e -> e.isLate()).count(); }
	public Collection<Evaluation> getEvalsReceived() { return this.evalsReceived; }
	public int getNumEvalsReceived() { return this.evalsReceived == null ? 0 : this.evalsReceived.size(); }
	public int getParticipationPoints() { return this.partPoints; }
	public double getTestScore() { return this.testScore; }
	public int getRank(boolean adjusted) { if (this.rank < 0) calculateRanks(adjusted); return this.rank; }
	
	@Override
	public String toString() {
		return String.format("%s, %s (%s)", lastName, firstName, tier);
	}

	@Override
	public int compareTo(Official other) {
		return this.getName().compareToIgnoreCase(other.getName());
	}
	
	public boolean equals(Official other) {
		return this.getName().equalsIgnoreCase(other.getName());
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	public static Map<String, Official> readOfficials(String fileName) {
		Map<String, Official> officials = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				String firstName = record.get("FName");
				String lastName = record.get("LName");
				String fullName = lastName + ", " + firstName;
				
				if (!officials.containsKey(fullName)) {
					officials.put(fullName, 
								  new Official(firstName, lastName, record.get("Email1"), record.get("MiscFieldValue1")));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(officials.size() + " officials read");
		return officials;
	}
}
