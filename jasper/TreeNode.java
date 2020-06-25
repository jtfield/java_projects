package jasper;

import java.util.HashMap;
import java.util.HashSet;

public class TreeNode {
	
	/**
	 * Creates an object of class TreeNode
	 * 
	 * @param name The name of the organism in this node
	 * @param olds The name of the parent node
	 */
	public TreeNode(String name, String olds) {
	    //this.taxId = id;
	    this.orgName = name;
	    //this.children = kids;
	    this.parent = olds;
	    
    }
	
	/**
	 * Add a child node to the HashSet of children nodes for this node.
	 * 
	 * @param kid Name of child node/organism.
	 */
	public void addChildren(String kid) {
	      children.add(kid);
	   }
	
	/**
	 * Returns HashSet of child nodes for this node.
	 * 
	 * @return children HashSet of child nodes.
	 */
	public HashSet<String> getChildren() {
	      return children;
	   }
	
	/**
	 * Returns the parent node of this node.
	 * 
	 * @return parent The name of the parent node.
	 */
	public String getParent() {
	      return parent;
	   }
	
	/**
	 * Returns a string of the structure <Organism name>, <Parent Organism/node name>, <Child node names if any>.
	 */
	public String toString() {
		return orgName + ", " + parent + ", " + children;
	}
	
	/**
	 * Add similarity values and child node names to a HashMap.
	 * 
	 * @param childName Name of direct child node.
	 * @param similarity Similarity percentage between child node and this node.
	 */
	public void addChildSim(String childName, double similarity) {
		childSims.put(childName, similarity);
	}
	
	public double getChildSim(String childName) {
		double sim = childSims.get(childName);
		return sim;
	}
	
	public void addParSim(double similarity) {
		parSim = similarity;
	}
	
	public double minimumDescendantSim() {

		for(String childName : children) {
			if(childSims.get(childName) < minChildSim) {
				minChildSim = childSims.get(childName);
				//minChildName = childName;
			}
		}
		return minChildSim;
	}
	
	public String minimumDescendantName() {
		for(String childName : children) {
			
			if(childSims.get(childName) < minChildSim) {
				minChildName = childName;
			}
		}
		return minChildName;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	double minChildSim = 100;
	
	String minChildName = null;
	
	HashMap<String, Double> minSimChild = new HashMap<>();
	
	//HashMap holding the names and similarity values between any direct children nodes
	//and this node.
	HashMap<String, Double> childSims = new HashMap<>();
	
	//Similarity percentage to the parent node.
	double parSim = -1;
	
	//Organisms name associated with this node.
	String orgName;
	
	//Taxanomic ID of this organism.
	int taxId;
	
	//HashSet of direct children of this node.
	HashSet<String> children=new HashSet<String>();
	
	//Descendents of this node.
	
	//Name of the parent of this node.
	String parent;
	
	//Node leve within the tree.
	int level;
}
