package pnfoa.evals.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import pnfoa.evals.*;

public class RankingTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 803033384115475256L;
	
	
	public static final double PART_POINTS_MAX = 100;
	public static final double EVAL_MAX = 9;
	public static final double TEST_MAX = 100;
	
	public static final double PART_POINTS_WEIGHT = 0.1;
	public static final double TEST_WEIGHT = 0.2;
	public static final double EVAL_WEIGHT = 0.7;	

	private String[] columnNames = {"Name", "Tier", "Rank", "Tier Rank", "Games Worked", "Varsity Games Worked", "Composite", "Part. Points", "Test Score", "Eval. Avg.", "Penalty"};
	private List<Official> officials;
	private EvaluationList evals;
	private int rowCount;
	private boolean adjusted;
	
	public RankingTableModel(List<Official> officials, EvaluationList evals, boolean adjusted) {
		this.officials = officials.stream().filter((Official o) -> o.getNumGamesWorked() > 0).collect(Collectors.toList());
		this.evals = evals;
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
			case 2: return 0; // return evals.getRankFor(official, adjusted);
			case 3: return 0; // return evals.getTierRankFor(official, adjusted);
			case 4: return official.getNumGamesWorked();
			case 5: return official.getNumGamesWorked(Level.Varsity);
			case 6: return getCompositeScoreFor(official, null, adjusted);
			case 7: return official.getParticipationPoints();
			case 8: return official.getTestScore();
			case 9: return evals.getAverageReceivedBy(official, adjusted);
			case 10: return official.getEvalPenalty();
			default: return null;
		}
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
