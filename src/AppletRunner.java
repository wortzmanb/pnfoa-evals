import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import pnfoa.evals.Evaluation;
import pnfoa.evals.Game;
import pnfoa.evals.Official;
import pnfoa.evals.gui.EvaluationTableModel;
import pnfoa.evals.gui.GameTableModel;
import pnfoa.evals.gui.OfficialTableModel;
import pnfoa.evals.gui.RankingTableModel;
import pnfoa.util.CSVParser;

public class AppletRunner extends JApplet {
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private Map<Integer, Evaluation> evals;
	
	public static final String DIRECTORY = "C:\\Users\\brettwo\\OneDrive\\PNFOA Board\\2017 - Evaluations\\Evals App\\Move-Up";

	public AppletRunner() throws HeadlessException {
	}
	
	@Override
	public void init() {
		officials = Official.readOfficials(DIRECTORY + "\\Officials.csv");
		games = Game.readGames(DIRECTORY + "\\Assignments.csv", officials);
		evals = Evaluation.readEvals(DIRECTORY + "\\Evaluations.csv", officials, games);
		readPartPoints(DIRECTORY + "\\Participation.csv", officials);
		readTestScores(DIRECTORY + "\\Test.csv", officials);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JFrame frame = getFrame();
					setContentPane(frame.getContentPane());
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readPartPoints(String fileName, Map<String, Official> officials) {
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official official = officials.get(record.get("Official Name"));
				if (official == null) continue;
				
				official.addPartPoints(Integer.parseInt(record.get("Points")));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void readTestScores(String fileName, Map<String, Official> officials) {
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official official = officials.get(record.get("Official Name"));
				if (official == null) continue;
				
				String strScore1 = record.get("Test Score");
				double score1 = strScore1 == null || strScore1.isEmpty() ? 0 : Double.parseDouble(strScore1);
				
				String strScore2 = record.get("Test Score 2");
				double score2 = strScore2 == null || strScore2.isEmpty() ? 0 : Double.parseDouble(strScore2);
				
				official.setTestScore(score1);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private JFrame getFrame() {
		JFrame frame = new JFrame("Evals App -- TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1600, 900));

		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tabbedPane.addTab("Rankings", makePanel("Rankings"));
		tabbedPane.addTab("Officials", makePanel("Officials"));
		tabbedPane.addTab("Games", makePanel("Games"));
		tabbedPane.addTab("Evaluations", makePanel("Evaluations"));

		panel.add(tabbedPane);
		frame.pack();
//		frame.setVisible(true);
//		frame.toFront();
		return frame;
	}

	private JComponent makePanel(String panelName) {
		JTable table;
		if (panelName.equals("Evaluations")) {
			table = new JTable(new EvaluationTableModel(new ArrayList<Evaluation>(evals.values())));
		} else if (panelName.equals("Officials")) {
			OfficialTableModel model = new OfficialTableModel(new ArrayList<Official>(officials.values()));
			table = new JTable(model) {
				public String getToolTipText(MouseEvent e) {
			        java.awt.Point p = e.getPoint();
			        int rowIndex = rowAtPoint(p);
			        int colIndex = columnAtPoint(p);
			        int realColumnIndex = convertColumnIndexToModel(colIndex);
			        String tip = model.getToolTipText(rowIndex, realColumnIndex); 
			        return (tip == null ? super.getToolTipText() : tip);
				}
			};
		} else  if (panelName.equals("Games")) {
			table = new JTable(new GameTableModel(new ArrayList<Game>(games.values())));
		} else if (panelName.equals("Rankings")) {
			table = new JTable(new RankingTableModel(new ArrayList<Official>(officials.values()), true));
		} else {
			throw new IllegalArgumentException("Invalid tab name");
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
