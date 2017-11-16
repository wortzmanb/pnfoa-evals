package pnfoa.evals;

import java.util.*;

public class Official {
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
	
	public String toString() {
		return String.format("%s, %s (%s)", lastName, firstName, tier);
	}
}
