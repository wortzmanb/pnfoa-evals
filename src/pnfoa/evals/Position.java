package pnfoa.evals;

public enum Position {
	Referee,
	Umpire,
	HeadLinesman,
	LineJudge,
	BackJudge,
	HL_LJ,
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
	
	public boolean matches(Position other) {
		if (this.equals(HL_LJ)) {
			return other.equals(HL_LJ) || other.equals(HeadLinesman) || other.equals(LineJudge);
		} else if (other.equals(HL_LJ)) {
			return this.equals(HL_LJ) || this.equals(HeadLinesman) || this.equals(LineJudge);
		}
		return this.equals(other);
	}
}
