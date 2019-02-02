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

	private Map<Ranking, Integer> ranks;
	
	public static final int MIN_GAMES_TO_RANK = 3;
	
	private static List<Official> allOfficials;
	private static Map<Ranking, Integer> rankCounts;
	
	static {
		allOfficials = new ArrayList<>();
		rankCounts = new EnumMap<>(Ranking.class);
	}
	
	public Official(String firstName, String lastName, String email, String tier) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.tier = Tier.parse(tier);
		allOfficials.add(this);
		
		this.ranks = new EnumMap<>(Ranking.class);
		breakOrdering();
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
		breakOrdering();
	}
	
	public void addEvalGiven(Evaluation e) {
		if (evalsGiven == null) {
			evalsGiven = new HashSet<>();
		}
		evalsGiven.add(e);
		breakOrdering();
	}
	
	public void addEvalReceived(Evaluation e) {
		if (evalsReceived == null) {
			evalsReceived = new ArrayList<>();
		}
		evalsReceived.add(e);
		breakOrdering();
	}
	
	public void addPartPoints(int points) {
		partPoints += points;
		partPoints = Math.min(partPoints, 100);
		breakOrdering();
	}
	
	public void setTestScore(double score) {
		testScore = score;
		breakOrdering();
	}

	public double getEvalPenalty() {
		if (getGamesWorked() == null) return 0.0;
		
		Set<LocalDateTime> missed = new HashSet<>();
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
					missed.add(Evaluation.dueDateFor(g.getDate()));
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
	
//	public int getRank(boolean adjusted) {
//		return getRank(null, adjusted);
//	}
//
//	public int getRank(Position pos, boolean adjusted) {
//		if (!ranks.containsKey(Ranking.getValue(pos, adjusted))) {
//			sortOfficials(pos, adjusted);
//		}
//		return ranks.get(Ranking.getValue(pos, adjusted));
//	}
//	
//	public int getTierRank(boolean adjusted) {
//		return getTierRank(null, adjusted);
//	}
//	
//	public int getTierRank(Position pos, boolean adjusted) {
//		if (!ranks.containsKey(Ranking.getValue(pos, adjusted))) {
//			sortOfficials(pos, adjusted);
//		}		
//		List<Official> tier = new ArrayList<>(allOfficials);
//		tier.removeIf((Official o) -> o.getTier() != this.getTier());
//		return 1 + tier.indexOf(this);
//	}
//	
//	private void sortOfficials(Position pos, boolean adjusted) {
//		allOfficials.sort((Official o1, Official o2) -> Double.compare(o2.getCompositeScore(pos, adjusted), o1.getCompositeScore(pos, adjusted)));
//		
//		Ranking r = Ranking.getValue(pos, adjusted);
//		int count = 1;
//		for (int i = 0; i < allOfficials.size(); i++) {
//			Official o = allOfficials.get(i);
//			if (o.getCompositeScore(pos, adjusted) == Double.NEGATIVE_INFINITY ||
//				(pos == Position.HL_LJ && (o.getNumGamesWorked(Level.Varsity, Position.HeadLinesman) + o.getNumGamesWorked(Level.Varsity, Position.LineJudge)) < MIN_GAMES_TO_RANK) ||
//				(pos == null && o.getNumGamesWorked(Level.Varsity) < MIN_GAMES_TO_RANK) ||
//				(pos != null && pos != Position.HL_LJ && o.getNumGamesWorked(Level.Varsity, pos) < MIN_GAMES_TO_RANK)) {
//				o.ranks.put(r, Integer.MAX_VALUE);
//			} else {
//				o.ranks.put(r, count);
//				count++;
//			}
//		}
//		rankCounts.put(r, count - 1);
//	}
	
	public Collection<Game> getGamesWorked(Position pos) { 
		return this.gamesWorked == null ? null : 
			   this.gamesWorked.entrySet().stream()
				.filter(ent -> ent.getValue().matches(pos))
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
				.filter(ent -> ent.getValue().matches(pos))
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
	
	public int getNumGamesWorked(Level level, Position pos) {
		return this.gamesWorked == null ? 0 : getGamesWorked(level, pos).size();
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
	
	public static int getNumRanked(boolean adjusted) { return rankCounts.get(Ranking.getValue(null, adjusted)); }
	public static int getNumRanked(Position p, boolean adjusted) { return rankCounts.get(Ranking.getValue(p, adjusted)); }
	
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
	
	private static enum Ranking {
		Overall,
		Referee,
		Umpire,
		HeadLinesman,
		LineJudge,
		BackJudge,
		HL_LJ,	
		AdjustedOverall,
		AdjustedReferee,
		AdjustedUmpire,
		AdjustedHeadLinesman,
		AdjustedLineJudge,
		AdjustedBackJudge,
		AdjustedHL_LJ;
		
		public static Ranking getValue(Position p, boolean adjusted) {
			String pos = p == null ? "Overall" : p.toString();
			String adj = adjusted ? "Adjusted" : "";
			return Ranking.valueOf(adj + pos);
		}
	
	}

	private void breakOrdering() {
		this.ranks.clear();
	}
}
