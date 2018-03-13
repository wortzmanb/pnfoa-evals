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

	private String[] columnNames = {"Name", "Tier", 
									"R Games Worked", "R Average", "R Rank", 
									"U Games Worked", "U Average", "U Rank", 
									"HL Games Worked", "HL Average", "HL Rank", 
									"LJ Games Worked", "LJ Average", "LJ Rank", 
									"BJ Games Worked", "BJ Average", "BJ Rank"};
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
			case 3: return official.getCompositeScore(Position.Referee, adjusted);
			case 4: return official.getRank(Position.Referee, adjusted);
			case 5: return official.getGamesWorked(Level.Varsity, Position.Umpire).size();
			case 6: return official.getCompositeScore(Position.Umpire, adjusted);
			case 7: return official.getRank(Position.Umpire, adjusted);
			case 8: return official.getGamesWorked(Level.Varsity, Position.HeadLinesman).size();
			case 9: return official.getCompositeScore(Position.HeadLinesman, adjusted);
			case 10: return official.getRank(Position.HeadLinesman, adjusted);
			case 11: return official.getGamesWorked(Level.Varsity, Position.LineJudge).size();
			case 12: return official.getCompositeScore(Position.LineJudge, adjusted);
			case 13: return official.getRank(Position.LineJudge, adjusted);
			case 14: return official.getGamesWorked(Level.Varsity, Position.BackJudge).size();
			case 15: return official.getCompositeScore(Position.BackJudge, adjusted);
			case 16: return official.getRank(Position.BackJudge, adjusted);
			default: return null;
		}
	}
}
