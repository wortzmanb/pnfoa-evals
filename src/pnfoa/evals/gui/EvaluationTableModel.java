package pnfoa.evals.gui;

import javax.swing.table.*;
import java.util.*;
import pnfoa.evals.*;

public class EvaluationTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5096358054313988840L;
	
	private String[] columnNames = {"Game #", "Game Date", "Due", "Submitted", "Evaluator", "Official", "Scores", "Late?"};
	private List<Evaluation> evals;
	private int rowCount;
	
	public EvaluationTableModel(List<Evaluation> evals) {
		this.evals = evals;
		this.rowCount = evals.size();
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
		Evaluation eval = evals.get(rowIndex);
		switch (columnIndex) {
			case 0: return eval.getGame().getId();
			case 1: return eval.getGame().getDate();
//			case 2: return eval.getGame().getAwayTeam() + " @ " + eval.getGame().getHomeTeam() + " (" + eval.getGame().getLevel() + ")";
			case 2: return eval.dueDate();
			case 3: return eval.getDate();
			case 4: return eval.getEvaluator();
			case 5: return eval.getOfficial();
			case 6: return eval.getScores();
			case 7: return eval.isLate();
			default: return null;
		}
	}

}
