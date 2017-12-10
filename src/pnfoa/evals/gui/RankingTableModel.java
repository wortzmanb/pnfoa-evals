package pnfoa.evals.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pnfoa.evals.Official;

public class RankingTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 803033384115475256L;

	private String[] columnNames = {"Name", "Tier", "Part. Points", "Test Score", "Eval. Avg.", "Penalty", "Unadj. COMPOSITE", "Unadj. RANK", "Adj. COMPOSITE", "Adj. RANK"};
	private List<Official> officials;
	private int rowCount;
	
	public RankingTableModel(List<Official> officials) {
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
			case 2: return official.getParticipationPoints();
			case 3: return official.getTestScore();
			case 4: return official.getAverageScoreReceived();
			case 5: return official.getEvalPenalty();
			case 6: return official.getCompositeScore();
			case 7: return official.getRank();
			case 8: return official.getAdjustedComposite();
			case 9: return official.getAdjustedRank();
			default: return null;
		}
	}
}
