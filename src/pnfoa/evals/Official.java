package pnfoa.evals;

import java.io.FileNotFoundException;
import java.util.*;

import pnfoa.util.*;

public class Official implements Comparable<Official> {
	private String firstName;
	private String lastName;
	private String email;
	private String tier;
	private List<Game> gamesWorked;
	private List<Evaluation> evalsGiven;
	private List<Evaluation> evalsReceived;
	
	public Official(String firstName, String lastName, String email, String tier) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.tier = tier;
	}
	
	public Official(String firstName, String lastName) {
		this(firstName, lastName, null, null);
	}
	
	public void addGame(Game g) {
		if (gamesWorked == null) {
			gamesWorked = new ArrayList<>();
		}
		
		gamesWorked.add(g);
	}
	
	public void addEvalGiven(Evaluation e) {
		if (evalsGiven == null) {
			evalsGiven = new ArrayList<>();
		}
		evalsGiven.add(e);
	}
	
	public void addEvalReceived(Evaluation e) {
		if (evalsReceived == null) {
			evalsReceived = new ArrayList<>();
		}
		evalsReceived.add(e);
	}
	
	public double getAverageScoreGiven() {
		return getAverage(evalsGiven);
	}
	
	public double getAverageScoreReceived() {
		return getAverage(evalsReceived);
	}
	
	private double getAverage(List<Evaluation> evals) {
		double total = 0;
		for (Evaluation eval : evals) {
			total += eval.getCompositeScore();
		}
		return (total / evals.size());
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
	
	public String getName() { return this.lastName + ", " + this.firstName; }
	public String getEmail() { return this.email; }
	public String getTier() { return this.tier; }
	public List<Game> getGamesWorked() { return this.gamesWorked; }
	public int getNumGamesWorked() { return this.gamesWorked == null ? 0 : this.gamesWorked.size(); }
	public List<Evaluation> getEvalsGiven() { return this.evalsGiven; }
	public int getNumEvalsGiven() { return this.evalsGiven == null ? 0 : this.evalsGiven.size(); }
	public List<Evaluation> getEvalsReceived() { return this.evalsReceived; }
	public int getNumEvalsReceived() { return this.evalsReceived == null ? 0 : this.evalsReceived.size(); }
	
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
}
