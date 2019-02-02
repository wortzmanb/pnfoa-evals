package pnfoa.evals;

public enum Level {
	Varsity,
	VarsityCrew,
	TrainingSubVarsity,
	JV,
	Sophomore,
	Freshman,
	CTeam,
	JrHigh8thGrade,
	JrHigh7thGrade,
	Rec10min,
	Rec9min,
	Rec8min,
	Scrimmage,
	Other;
	
	public static Level parse(String s) {
		try {
			return Level.valueOf(s.replaceAll("[\\W]", ""));
		} catch (IllegalArgumentException e) {
			return Level.Other;
		}
	}
}
