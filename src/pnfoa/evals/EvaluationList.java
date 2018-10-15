package pnfoa.evals;

import java.util.*;
import java.util.stream.*;

public class EvaluationList {
	private List<Evaluation> evals;
	private double average;
	private boolean isAverageStale;

	public EvaluationList() {
		evals = new ArrayList<>();
	}

	public boolean add(Evaluation e) { isAverageStale = true; return evals.add(e);	}
	public boolean addAll(Collection<Evaluation> c) { isAverageStale = true; return evals.addAll(c); }
	public boolean contains(Evaluation e) { return evals.contains(e); }
	public boolean isEmpty() { return evals.isEmpty(); }
	public Iterator<Evaluation> iterator() { return evals.iterator(); }
	public boolean remove(Evaluation e) { isAverageStale = true; return evals.remove(e); }
	public boolean removeAll(Collection<?> c) { isAverageStale = true; return evals.removeAll(c); }
	public int size() { return evals.size(); }
	
	public double getAverage() {
		if (isAverageStale) {
			average = getAverage(evals.stream());
		}
		return average;
	}
	
	public double getAverageGivenBy(Official o) {
		return getAverage(evals.stream()
							   .filter((Evaluation e) -> e.getEvaluator().equals(o)));
	}
	
	public double getAverageReceivedBy(Official o) {
		return getAverage(evals.stream()
							   .filter((Evaluation e) -> e.getOfficial().equals(o)));
	}
	
	public double getAdjustmentFor(Official o) {
		return getAverage() - getAverageGivenBy(o);
	}
	
	private static double getAverage(Stream<Evaluation> s) {
		return s.mapToDouble(Evaluation::getCompositeScore)
				.average()
				.getAsDouble();
	}
}
