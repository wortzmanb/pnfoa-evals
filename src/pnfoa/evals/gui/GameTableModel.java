package pnfoa.evals.gui;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import pnfoa.evals.*;

public class GameTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8055541800982502424L;
	
	private String[] positions = {"Referee", "Umpire", "Head Linesman", "Line Judge", "Back Judge"};
	private String[] columnNames = {"Game #", "Date", "Location", "Teams", "Level", "Referee", "Umpire", "Head Linesman", "Line Judge", "Back Judge"};
	private List<Game> games;
	private int rowCount;

	public GameTableModel(List<Game> games) {
		this.games = games;
		this.rowCount = games.size();
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
		Game game = games.get(rowIndex);
		switch (columnIndex) {
			case 0: return game.getId();
			case 1: return game.getDate();
			case 2: return game.getLocation();
			case 3: return game.getAwayTeam() + " @ " + game.getHomeTeam();
			case 4: return game.getLevel();
			case 5: 
			case 6: 
			case 7: 
			case 8: 
			case 9: return game.getOfficials(Position.parse(positions[columnIndex - 5]));
			default: return null;
		}
	}

}
