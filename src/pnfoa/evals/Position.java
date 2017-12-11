package pnfoa.evals;

public enum Position {
	Referee,
	Umpire,
	HeadLinesman,
	LineJudge,
	BackJudge,
	Chains,
	Evaluator,
	Other;
	
	public static Position parse(String s) {
		try {
			return Position.valueOf(s.replaceAll("[\\W\\d]", ""));
		} catch (IllegalArgumentException e) {
			return Position.Other;
		}
	}
}
