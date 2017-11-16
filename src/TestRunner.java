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
		Map<String, Official> officials = Official.readOfficials(offFileName);

		
		System.out.print("Assignments file? ");
		String assFileName = kb.nextLine();		
		Map<Integer, Game> games = Game.readGames(assFileName, officials);
		
		System.out.print("Evaluations file? ");
		String evalFileName = kb.nextLine();
		Map<Integer, Evaluation> evals = Evaluation.readEvals(evalFileName, officials, games);
		
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
		
		Object[] colNames = {"Game #", "Game Date", "Teams", "Evaluator", "Official", "Scores"};
		
		List<Object[]> rows = new ArrayList<>();
		for (Evaluation eval : evals.values()) {
			Game g = eval.getGame();
			List<Object> data = new ArrayList<>();
			
			data.add(g.getId());
			data.add(g.getDate());
			data.add(g.getAwayTeam() + " @ " + g.getHomeTeam() + " (" + g.getLevel() + ")");
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
}
