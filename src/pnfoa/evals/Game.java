package pnfoa.evals;

import java.io.FileNotFoundException;
import java.text.*;
import java.util.*;

import pnfoa.util.CSVParser;

public class Game implements Comparable<Game> {
	private int id;
	private String location;
	private String homeTeam;
	private String awayTeam;
	private Date date;
	private String level;
	private Map<String, List<Official>> officials;

	public Game(int id, String loc, String home, String away, String date, String level) throws ParseException {
		this.id = id;
		this.location = loc;
		this.homeTeam = home;
		this.awayTeam = away;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		this.date = df.parse(date);
		this.level = level;
	}
	
	public void addOfficial(Official official, String pos) {
		if (officials == null) {
			officials = new HashMap<String, List<Official>>();
		}
		
		List<Official> list = officials.get(pos);
		
		if (list == null) {
			list = new ArrayList<Official>();
		}
		list.add(official);
		
		officials.put(pos, list);
		official.addGame(this);
	}
	
	public static Map<Integer, Game> readGames(String fileName, Map<String, Official> officials) {
		Map<Integer, Game> games = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				int id = Integer.parseInt(record.get("GameID"));
				
				if (!games.containsKey(id)) {
					games.put(id, new Game(id,
										   record.get("SiteName"),
										   record.get("HomeTeams"),
										   record.get("AwayTeams"),
										   record.get("FromDate"),
										   record.get("LevelName")));
				}
				Game game = games.get(id);
				
				String firstName = record.get("FirstName");
				String lastName = record.get("LastName");
				
				if (!officials.containsKey(lastName + ", " + firstName)) {
					officials.put(lastName + ", " + firstName, new Official(firstName, lastName));
				}
				Official official = officials.get(lastName + ", " + firstName);
				game.addOfficial(official, record.get("PositionName").replaceAll("\\d", ""));
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(games.size() + " games read");
		return games;
	}
	
	public int getId() { return id; }
	public Date getDate() { return date; }
	public String getLocation() { return location; }
	public String getHomeTeam() { return homeTeam; }
	public String getAwayTeam() { return awayTeam; }
	public String getLevel() { return level; }	
	public List<Official> getOfficials(String pos) { return officials.get(pos) == null ? new ArrayList<Official>() : officials.get(pos); }
	public Map<String, List<Official>> getOfficials() { return officials;	}

	@Override
	public String toString() {
		return String.format("%s: %s @ %s (%s) - %s", date, awayTeam, homeTeam, level, officials.values());
	}
	
	@Override
	public int compareTo(Game other) {
		return this.getId() - other.getId();
	}
	
	public boolean equals(Game other) {
		return this.compareTo(other) == 0;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(this.getId());
	}
}
