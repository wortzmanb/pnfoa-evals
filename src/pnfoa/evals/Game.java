package pnfoa.evals;

import java.text.*;
import java.util.*;

public class Game {
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
	
	public List<Official> getOfficials(String pos) { return officials.get(pos); }
	
	public int getId() { return id; }
	public String getHomeTeam() { return homeTeam; }
	public String getAwayTeam() { return awayTeam; }
	public Date getDate() { return date; }
	public String getLevel() { return level; }
	
	public String toString() {
		return String.format("%s: %s @ %s (%s) - %s", date, awayTeam, homeTeam, level, officials.values());
	}
}
