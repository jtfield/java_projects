package jasper;

public class Comparison {

	public Comparison(int queryID_, int refID_, double identity_) {
		
		this.queryID = queryID_;
		this.refID = refID_;
		this.identity = identity_;
	}
	
	int queryID;
	int refID;
	double identity;
	
}
