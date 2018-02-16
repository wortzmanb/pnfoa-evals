package pnfoa.evals.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import pnfoa.evals.Level;
import pnfoa.evals.Official;
import pnfoa.evals.Position;

public class PositionTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101636009343426590L;

	private String[] columnNames = {"Name", "Tier", "R Games Worked", "R Average", "U Games Worked", "U Average", "HL Games Worked", "HL Average", "LJ Games Worked", "LJ Average", "BJ Games Worked", "BJ Average"};
	private List<Official> officials;
	private int rowCount;
	private boolean adjusted;
	
	public PositionTableModel(List<Official> officials, boolean adjusted) {
		this.officials = officials.stream().filter((Official o) -> o.getNumGamesWorked() > 0).collect(Collectors.toList());
		this.rowCount = this.officials.size();
		this.adjusted = adjusted;
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
			case 2: return official.getGamesWorked(Level.Varsity, Position.Referee).size();
			case 3: return adjusted ? official.getAdjustedAverageScoreReceived(Position.Referee) : official.getAverageScoreReceived(Position.Referee);
			case 4: return official.getGamesWorked(Level.Varsity, Position.Umpire).size();
			case 5: return adjusted ? official.getAdjustedAverageScoreReceived(Position.Umpire) : official.getAverageScoreReceived(Position.Umpire);
			case 6: return official.getGamesWorked(Level.Varsity, Position.HeadLinesman).size();
			case 7: return adjusted ? official.getAdjustedAverageScoreReceived(Position.HeadLinesman) : official.getAverageScoreReceived(Position.HeadLinesman);
			case 8: return official.getGamesWorked(Level.Varsity, Position.LineJudge).size();
			case 9: return adjusted ? official.getAdjustedAverageScoreReceived(Position.LineJudge) : official.getAverageScoreReceived(Position.LineJudge);
			case 10: return official.getGamesWorked(Level.Varsity, Position.BackJudge).size();
			case 11: return adjusted ? official.getAdjustedAverageScoreReceived(Position.BackJudge) : official.getAverageScoreReceived(Position.BackJudge);
			default: return null;
		}
	}
}
