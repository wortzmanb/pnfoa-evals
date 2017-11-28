import java.util.*;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pnfoa.util.*;
import pnfoa.evals.*;
import pnfoa.evals.gui.*;


public class TestRunner implements ItemListener {
	private JPanel centerPanel;
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private Map<Integer, Evaluation> evals;
	
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
		
		kb.close();
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("   " + brett.getNumGamesWorked() + " games worked");
		System.out.println("   " + brett.getNumEvalsGiven() + " evals given (average = " + brett.getAverageScoreGiven() + ")");
		System.out.println("       " + brett.getEvalsGiven());
		System.out.println("   " + brett.getNumEvalsReceived() + " evals received (average = " + brett.getAverageScoreReceived() + ")");
		System.out.println("       " + brett.getEvalsReceived());
		
//		TestRunner runner = new TestRunner(officials, games, evals);
//		runner.showGui();
	}
	
	public TestRunner(Map<String, Official> officials, Map<Integer, Game> games, Map<Integer, Evaluation> evals) {
		this.officials = officials;
		this.games = games;
		this.evals = evals;
	}

	private void showGui() {
		JFrame frame = new JFrame("Evals App -- TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);

		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);

		String[] viewStrings = {"Officials", "Games", "Evaluations"};
		JComboBox<String> viewList = new JComboBox<>(viewStrings);
		viewList.setSelectedIndex(0);
		panel.add(viewList, BorderLayout.NORTH);
		centerPanel = new JPanel();
		panel.add(centerPanel, BorderLayout.CENTER);
		updateView("Officials");
		
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			JComboBox<String> cb = (JComboBox<String>)e.getSource();
			String viewName = (String)cb.getSelectedItem();
			updateView(viewName);
		}
	}

	private void updateView(String viewName) {
		JTable table;
		if (viewName.equals("Evaluations")) {
			table = new JTable(new EvaluationTableModel(new ArrayList<Evaluation>(evals.values())));
		} else if (viewName.equals("Officials")) {
			table = new JTable(new OfficialTableModel(new ArrayList<Official>(officials.values())));
		} else { // if (viewName.equals("Games")) {
			table = new JTable(new GameTableModel(new ArrayList<Game>(games.values())));
		}
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);
		centerPanel.removeAll();
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		centerPanel.repaint();
	}	
}
