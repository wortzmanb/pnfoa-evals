package pnfoa.evals.gui;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import pnfoa.evals.*;

public class OfficialTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8820665206299457464L;
	
	public static final double PART_POINTS_MAX = 100;
	public static final double EVAL_MAX = 9;
	public static final double TEST_MAX = 100;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TEST_WEIGHT = 0.2;
	public static final double EVAL_WEIGHT = 0.7;	
	
	private String[] columnNames = {"Name", "Tier", "# Games Worked", "# Evals Given", "Given Avg.", "# Evals Received", "Received Avg.", "# Evals Late", "Eval. Adjustment"};
	private List<Official> officials;
	private EvaluationList evals;
	private int rowCount;
	
	public OfficialTableModel(List<Official> officials, EvaluationList evals) {
		this.officials = officials;
		this.evals = evals;
		this.rowCount = officials.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int colIndex) {
		return false;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Official official = officials.get(rowIndex);
		switch (columnIndex) {
			case 0: return official.getName();
			case 1: return official.getTier();
			case 2: return official.getNumGamesWorked();
			case 3: return official.getNumEvalsGiven();
			case 4: return evals.getAverageGivenBy(official, false);
			case 5: return official.getNumEvalsReceived();
			case 6: return evals.getAverageReceivedBy(official, false);
			case 7: return official.getNumEvalsLate();
			case 8: return getCompositeScoreFor(official, null, false);
			case 9: return evals.getAdjustmentFor(official);
			default: return null;
		}
	}

	public String getToolTipText(int row, int column) {
		if (column == 2) {
			StringBuilder str = new StringBuilder();
			str.append("<html>");
			Official official = officials.get(row);
			if (official.getAssignments() == null) return "";
			for (Map.Entry<Game, Position> entry : official.getAssignments().entrySet()) {
				Game g = entry.getKey();
				str.append(String.format("%s: %s @ %s (%s) - %s<br />", g.getDateString(), g.getAwayTeam(), g.getHomeTeam(), g.getLevel(), entry.getValue()));
			}
			return str.append("</html>").toString();
		}
		return null;
	}
	
	private double getCompositeScoreFor(Official o, Position pos, boolean adjusted) {
		return getCompositeScore(o.getParticipationPoints(), o.getTestScore(), evals.getAverageReceivedBy(o, pos, adjusted), o.getEvalPenalty());
	}	
	
	private double getCompositeScore(double part, double test, double evals, double penalty) {
		return (part / PART_POINTS_MAX * PART_POINTS_WEIGHT) + 
			   (test / TEST_MAX * TEST_WEIGHT) +
			   ((evals - penalty) / EVAL_MAX * EVAL_WEIGHT);
	}	
}
