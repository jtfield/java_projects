package jasper;

public class Comparison {

	/**
	 * Object for storing sequence similarity values between two nodes
	 * @param queryID_ int Node ID of the primary query sequence.
	 * @param refID_ int Node ID of the reference sequence.
	 * @param identity_ double similarity value between both nodes.
	 */
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
