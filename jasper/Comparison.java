package jasper;

public class Comparison {

	public Comparison(int queryID_, int refID_, double identity_) {
		
		this.queryID = queryID_;
		this.refID = refID_;
		this.identity = identity_;
	}
	
	public String toString() {
		return "Query node ID = " + queryID + ", Reference node ID = " + refID + 
				", Similarity identity = " + identity;
	}
	
	int queryID;
	int refID;
	double identity;
	
}
