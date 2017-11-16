import java.util.*;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import javax.swing.*;
import java.awt.*;

import pnfoa.util.*;
import pnfoa.evals.*;


public class TestRunner {
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		System.out.print("Officials file? ");
		String offFileName = kb.nextLine();
		Map<String, Official> officials = readOfficials(offFileName);

		
		System.out.print("Assignments file? ");
		String assFileName = kb.nextLine();		
		Map<Integer, Game> games = readGames(assFileName, officials);
		
		System.out.print("Evaluations file? ");
		String evalFileName = kb.nextLine();
		Map<Integer, Evaluation> evals = readEvals(evalFileName, officials, games);
		
//		for (Evaluation eval : evals.values() ) {
//			System.out.println(eval);
//		}

		kb.close();
		
		showGui(officials, games, evals);
	}

	private static void showGui(Map<String, Official> officials, Map<Integer, Game> games,
			Map<Integer, Evaluation> evals) {
		JFrame frame = new JFrame("Evals App -- TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Object[] colNames = {"Game #", "Date", "Teams", "Evaluator", "Official", "Scores"};
		
		List<Object[]> rows = new ArrayList<>();
		for (Evaluation eval : evals.values()) {
			List<Object> data = new ArrayList<>();
			data.add(eval.getGame().getId());
			data.add(eval.getGame().getDate());
			data.add(eval.getGame().getAwayTeam() + " @ " + eval.getGame().getHomeTeam() + " (" + eval.getGame().getLevel() + ")");
			data.add(eval.getEvaluator());
			data.add(eval.getOfficial());
			data.add(eval.getScores());
			rows.add(data.toArray());
		}
		
		JTable table = new JTable(rows.toArray(new Object[1][1]), colNames);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}

	private static Map<String, Official> readOfficials(String fileName) {
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
	
	private static Map<Integer, Game> readGames(String fileName, Map<String, Official> officials) {
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
	
	private static Map<Integer, Evaluation> readEvals(String fileName, Map<String, Official> officials, Map<Integer, Game> games) {
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
}
