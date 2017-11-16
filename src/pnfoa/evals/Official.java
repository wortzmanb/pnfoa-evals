package pnfoa.evals;

import java.io.FileNotFoundException;
import java.util.*;

import pnfoa.util.CSVParser;

public class Official implements Comparable<Official> {
	private String firstName;
	private String lastName;
	private String email;
	private String tier;
	private List<Game> gamesWorked;
	
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
			gamesWorked = new ArrayList<Game>();
		}
		
		gamesWorked.add(g);
	}
	
	public static Map<String, Official> readOfficials(String fileName) {
		Map<String, Official> officials = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				String firstName = record.get("FName");
				String lastName = record.get("LName");
				
				if (!officials.containsKey(lastName + ", " + firstName)) {
					officials.put(lastName + ", " + firstName, 
								  new Official(firstName, lastName, record.get("Email1"), record.get("Classification")));
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
