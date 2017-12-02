package pnfoa.evals;

public enum Tier {
	V1,
	V2,
	V3,
	A2,
	A1,
	NA;
	
	public static Tier parse(String s) {
		Tier t;
		try {
			t = Tier.valueOf(s);
		} catch (IllegalArgumentException e) {
			t = Tier.NA;
		}
		
		return t;
	}
	
	@Override
	public String toString() {
		if (this == Tier.NA)
			return "N/A";
		return super.toString();
	}
}
