package pnfoa.evals;

import java.io.*;
import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

import pnfoa.util.*;

public class Game implements Comparable<Game> {
	private int id;
	private String location;
	private String homeTeam;
	private String awayTeam;
	private LocalDateTime date;
	private Level level;
	private Map<Position, Collection<Official>> officials;

	public Game(int id, String loc, String home, String away, String date, String level) throws ParseException {
		this.id = id;
		this.location = loc;
		this.homeTeam = home;
		this.awayTeam = away;
		DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
		this.date = LocalDateTime.parse(date, df);
		this.level = Level.parse(level);
	}
	
	public void addOfficial(Official official, String p) {
		if (officials == null) {
			officials = new HashMap<>();
		}
		Position pos = Position.parse(p);
		
		Collection<Official> list = officials.get(pos);
		
		if (list == null) {
			list = new HashSet<Official>();
		}
		list.add(official);
		
		officials.put(pos, list);
		
		if (pos != Position.Chains && pos != Position.Evaluator) {
			official.addGame(this, pos);
		}
	}
	
	public int getPartPointsFor(Tier t) {
		switch (getLevel()) {
			case TrainingSubVarsity:
			case JV:
			case Sophomore:
			case Freshman:
			case JrHigh8thGrade:
			case JrHigh7thGrade:
				return t == Tier.V1 ? 20 : 10; 
			case Rec10min:
			case Rec9min:
			case Rec8min:
				return t == Tier.V1 ? 15 : 10;
			default: 
				return 0;
		}
	}
	
	public static Map<Integer, Game> readGames(String fileName, Map<String, Official> officials) {
		Map<Integer, Game> games = new TreeMap<>();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
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
				game.addOfficial(official, record.get("PositionName"));
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
	
	public String getDateString() {
		String day = date.getDayOfWeek().toString().substring(0, 3).toLowerCase();
		day = day.substring(0, 1).toUpperCase() + day.substring(1);
		return day + " " + date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)); 
	}
	public int getId() { return id; }
	public LocalDateTime getDate() { return date; }
	public String getLocation() { return location; }
	public String getHomeTeam() { return homeTeam; }
	public String getAwayTeam() { return awayTeam; }
	public Level getLevel() { return level; }	
	public Collection<Official> getOfficials(Position pos) { return officials.get(pos) == null ? new ArrayList<Official>() : officials.get(pos); }
	public Map<Position, Collection<Official>> getOfficials() { return officials;	}

	@Override
	public String toString() {
		return String.format("%s: %s @ %s (%s) - %s", getDateString(), awayTeam, homeTeam, level, officials.values());
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
