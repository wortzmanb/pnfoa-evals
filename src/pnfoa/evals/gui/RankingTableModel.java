package pnfoa.evals.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import pnfoa.evals.Level;
import pnfoa.evals.Official;

public class RankingTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 803033384115475256L;

	private String[] columnNames = {"Name", "Tier", "Rank", "Tier Rank", "Games Worked", "Varsity Games Worked", "Composite", "Part. Points", "Test Score", "Eval. Avg.", "Penalty"};
	private List<Official> officials;
	private int rowCount;
	private boolean adjusted;
	
	public RankingTableModel(List<Official> officials, boolean adjusted) {
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
			case 2: return official.getRank(adjusted);
			case 3: return official.getTierRank(adjusted);
			case 4: return official.getNumGamesWorked();
			case 5: return official.getNumGamesWorked(Level.Varsity);
			case 6: return official.getCompositeScore(adjusted);
			case 7: return official.getParticipationPoints();
			case 8: return official.getTestScore();
			case 9: return official.getAverageScoreReceived(adjusted);
			case 10: return official.getEvalPenalty();
			default: return null;
		}
	}
}
