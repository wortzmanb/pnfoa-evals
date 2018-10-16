package pnfoa.evals;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.*;

import pnfoa.util.CSVParser;

public class EvaluationList implements Iterable<Evaluation> {
	public static final boolean SKIP_APPRENTICES = true;
	public static final boolean SKIP_NON_CREW = true;
	
	private Map<Integer, Evaluation> evals;
	private double average;
	private boolean isAverageStale;

	private EvaluationList() {
		evals = new HashMap<>();
	}

	public void add(Evaluation e) { isAverageStale = true; evals.put(e.getId(), e);	}
	public boolean contains(Evaluation e) { return containsId(e.getId()); }
	public boolean containsId(int id) { return evals.containsKey(id); }
	public Evaluation get(int id) { return evals.get(id); }
	public boolean isEmpty() { return evals.isEmpty(); }
	public Iterator<Evaluation> iterator() { return evals.values().iterator(); }
	public Evaluation remove(Evaluation e) { isAverageStale = true; return evals.remove(e.getId()); }
	public int size() { return evals.size(); }

	public void removeIf(Predicate<? super Evaluation> filter) {
		Iterator<Evaluation> iter = iterator();
		while (iter.hasNext()) {
			if (filter.test(iter.next())) {
				iter.remove();
			}
		}
	}
	
	public double getAverage(boolean adjusted) {
		if (isAverageStale) {
			average = getAverage(evals.values().stream(), adjusted);
		}
		return average;
	}
	
	public double getAverageGivenBy(Official o, boolean adjusted) {
		return getAverage(evals.values().stream()
							   			.filter((Evaluation e) -> e.getEvaluator().equals(o)), adjusted);
	}
	
	public double getAverageReceivedBy(Official o, boolean adjusted) {
		return getAverage(evals.values().stream()
										.filter((Evaluation e) -> e.getOfficial().equals(o)), adjusted);
	}
	
	public double getAverageReceivedBy(Official o, Position pos, boolean adjusted) {
		return getAverage(evals.values().stream()
										.filter((Evaluation e) -> e.getEvaluator().equals(o))
										.filter((Evaluation e) -> e.getPosition().equals(pos)), adjusted);
					
	}
	
	public double getAdjustmentFor(Official o) {
		return getAverage(false) - getAverageGivenBy(o, false);
	}
	
	private double getAverage(Stream<Evaluation> s, boolean adjusted) {
		return s.mapToDouble((Evaluation e) -> (e.getCompositeScore() + (adjusted ? getAdjustmentFor(e.getEvaluator()) : 0)))
				.average()
				.getAsDouble();
	}
	
	public static EvaluationList fromCollection(Collection<Evaluation> c) {
		EvaluationList list = new EvaluationList();
		for (Evaluation e : c) {
			list.add(e);
		}
		return list;
	}
	
	public static EvaluationList readEvals(String fileName, Map<String, Official> officials, Map<Integer, Game> games) {
		EvaluationList list = new EvaluationList();
		try {
			CSVParser parser = new CSVParser(fileName);
			
			while (parser.hasNextRecord()) {
				Map<String, String> record = parser.nextRecord();
				if (record == null) continue;
				
				Official evaluator = officials.get(record.get("Evaluator_Name"));
				Official official = officials.get(record.get("Official_Name"));
				Position position = Position.parse(record.get("Position_Worked"));
				Game game = games.get(Integer.parseInt(record.get("GameID")));
				
				int id = Integer.parseInt(record.get("Evaluation_ID"));
				if (!list.containsId(id)) {
					list.add(new Evaluation(id,
							 			    game,
											evaluator,
											official,
											position,
											record.get("Date_Submitted")));
				}
				Evaluation eval = list.get(id);
				eval.addScore(record.get("Evaluation_Criteria_Name"), Integer.parseInt(record.get("Criteria_Value")), record.get("Criteria_Comments"));
				eval.addSummaryComment(record.get("Summary_Comments"));
				
				// don't count evaluations from Apprentices
				if (EvaluationList.SKIP_APPRENTICES && (evaluator.getTier() == Tier.A1 || evaluator.getTier() == Tier.A2)) {
					System.out.println("Skipping Apprentice evaluation: " + eval);
					continue;
				}
				
				// don't count evaluations from officials not on the crew
				Position evalPos = game.getPositionOf(evaluator); 
				if (EvaluationList.SKIP_NON_CREW &&
					(evalPos != null && evalPos != Position.Referee && evalPos != Position.Umpire && 
					 evalPos != Position.HeadLinesman && evalPos != Position.LineJudge && evalPos != Position.BackJudge)) {
					System.out.println("Skipping non-crew evaluation: " + eval);
					continue;
				}
				
				if (evaluator.getEvalsGiven() == null || !evaluator.getEvalsGiven().contains(eval)) 
					evaluator.addEvalGiven(eval);
				if (official.getEvalsReceived() == null || !official.getEvalsReceived().contains(eval))
					official.addEvalReceived(eval);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println(list.size() + " evaluations read");
		return list;
	}	
}
