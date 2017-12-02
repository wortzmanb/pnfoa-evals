import java.util.*;
import java.util.List;
import java.io.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import pnfoa.util.*;
import pnfoa.evals.*;
import pnfoa.evals.gui.*;


public class TextRunner {
	private Map<String, Official> officials;
	private Map<Integer, Game> games;
	private Map<Integer, Evaluation> evals;
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Directory? ");
		String directoryName = kb.nextLine();
		
//		System.out.print("Officials file? ");
//		String offFileName = kb.nextLine();
		Map<String, Official> officials = Official.readOfficials(directoryName + "\\Officials.csv");
		
//		System.out.print("Assignments file? ");
//		String assFileName = kb.nextLine();		
		Map<Integer, Game> games = Game.readGames(directoryName + "\\Assignments.csv", officials);
		
//		System.out.print("Evaluations file? ");
//		String evalFileName = kb.nextLine();
		Map<Integer, Evaluation> evals = Evaluation.readEvals(directoryName + "\\Evaluations.csv", officials, games);
		
		kb.close();
		
		Official brett = officials.get("Wortzman, Brett");
		System.out.println(brett + ": ");
		System.out.println("   " + brett.getNumGamesWorked() + " games worked");
		System.out.println("   " + brett.getNumEvalsGiven() + " evals given (average = " + brett.getAverageScoreGiven() + ")");
		System.out.println("       " + brett.getEvalsGiven());
		System.out.println("   " + brett.getNumEvalsReceived() + " evals received (average = " + brett.getAverageScoreReceived() + ")");
		System.out.println("       " + brett.getEvalsReceived());
    }	
}
