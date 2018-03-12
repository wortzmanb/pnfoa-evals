package pnfoa.evals.gui;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import pnfoa.evals.*;

public class OfficialTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8820665206299457464L;
	
	private String[] columnNames = {"Name", "Tier", "# Games Worked", "# Evals Given", "Given Avg.", "# Evals Received", "Received Avg.", "# Evals Late", "Eval. Adjustment"};
	private List<Official> officials;
	private int rowCount;
	
	public OfficialTableModel(List<Official> officials) {
		this.officials = officials;
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
			case 4: return official.getAverageScoreGiven(false);
			case 5: return official.getNumEvalsReceived();
			case 6: return official.getAverageScoreReceived(false);
			case 7: return official.getNumEvalsLate();
			case 8: return official.getCompositeScore(false);
			case 9: return official.getAdjustment();
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
}
