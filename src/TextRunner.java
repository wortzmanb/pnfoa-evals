import java.util.*;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import pnfoa.util.*;
import pnfoa.evals.*;
import pnfoa.evals.gui.*;


public class TestRunner {
//	private JPanel centerPanel;
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private Map<Integer, Evaluation> evals;
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		System.out.print("Directory? ");
		String directoryName = kb.nextLine();
		
//		System.out.print("Officials file? ");
//		String offFileName = kb.nextLine();
		Map<String, Official> officials = Official.readOfficials(directoryName + "\\Officials.csv");
		
//		System.out.print("Assignments file? ");
//		String assFileName = kb.nextLine();		
		Map<Integer, Game> games = Game.readGames(directoryName + "\\Assignments.csv", officials);
		
//		System.out.print("Evaluations file? ");
//		String evalFileName = kb.nextLine();
		Map<Integer, Evaluation> evals = Evaluation.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		kb.close();
		
//		Official brett = officials.get("Wortzman, Brett");
//		System.out.println(brett + ": ");
//		System.out.println("   " + brett.getNumGamesWorked() + " games worked");
//		System.out.println("   " + brett.getNumEvalsGiven() + " evals given (average = " + brett.getAverageScoreGiven() + ")");
//		System.out.println("       " + brett.getEvalsGiven());
//		System.out.println("   " + brett.getNumEvalsReceived() + " evals received (average = " + brett.getAverageScoreReceived() + ")");
//		System.out.println("       " + brett.getEvalsReceived());
		
		TestRunner runner = new TestRunner(officials, games, evals);
		runner.showGui();
	}
	
	public TestRunner(Map<String, Official> officials, Map<Integer, Game> games, Map<Integer, Evaluation> evals) {
		this.officials = officials;
		this.games = games;
		this.evals = evals;
	}

	private void showGui() {
		JFrame frame = new JFrame("Evals App -- TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1400, 1050));

		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tabbedPane.addTab("Officials", makePanel("Officials"));
		tabbedPane.addTab("Games", makePanel("Games"));
		tabbedPane.addTab("Evaluations", makePanel("Evaluations"));

		panel.add(tabbedPane);
		frame.pack();
		frame.setVisible(true);
	}

	private JComponent makePanel(String panelName) {
		JTable table;
		if (panelName.equals("Evaluations")) {
			table = new JTable(new EvaluationTableModel(new ArrayList<Evaluation>(evals.values())));
		} else if (panelName.equals("Officials")) {
			table = new JTable(new OfficialTableModel(new ArrayList<Official>(officials.values())));
		} else { // if (viewName.equals("Games")) {
			table = new JTable(new GameTableModel(new ArrayList<Game>(games.values())));
		}
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		initColumnSizes(table);

		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
	}	
	
    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     * 
     * Modified from Java Tutorial TableRenderDemo.java
     */
    private void initColumnSizes(JTable table) {
        TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < model.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
 
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, table.getValueAt(0, i),
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
 
//            if (DEBUG) {
//                System.out.println("Initializing width of column "
//                                   + i + ". "
//                                   + "headerWidth = " + headerWidth
//                                   + "; cellWidth = " + cellWidth);
//            }
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }	
}