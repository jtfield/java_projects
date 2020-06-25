package jasper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
	      childNames.add(kid);
	   }
	
	/**
	 * Returns HashSet of child nodes for this node.
	 * 
	 * @return children HashSet of child nodes.
	 */
	public HashSet<String> getChildren() {
	      return childNames;
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
		return orgName + ", " + parent + ", " + childNames + ", " + level;
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
	
	/**
	 * Returns the similarity percentage between this node and a child node.
	 * 
	 * @param childName The name of the child node.
	 * @return double similarity percentage
	 */
	public double getChildSim(String childName) {
		double sim = childSims.get(childName);
		return sim;
	}
	
	/**
	 * Takes a double similarity percentage and associates that with the parent node.
	 * 
	 * @param similarity Percentage similarity between node and parent of type double
	 */
	public void addParSim(double similarity) {
		parSim = similarity;
	}
	
	/**
	 * Returns the minimum similarity percentage of all descendants.
	 * 
	 * @return Similarity between node and child node with lowest similarity.
	 */
	public double minimumDescendantSim() {

		for(String childName : childNames) {
			if(childSims.get(childName) < minChildSim) {
				minChildSim = childSims.get(childName);
				//minChildName = childName;
			}
		}
		return minChildSim;
	}
	
	/**
	 * Returns name of child node with lowest similarity to this node.
	 * 
	 * @return Node name of child with lowest similarity (type String).
	 */
	public String minimumDescendantName() {
		for(String childName : childNames) {
			
			if(childSims.get(childName) < minChildSim) {
				minChildName = childName;
			}
		}
		return minChildName;
	}
	
	/**
	 * Adds name and similarity to HashMap if flagged as higher than a parent or child similarity. 
	 * 
	 * @param orgName
	 * @param sim
	 */
	public void flagRelation(String orgName, double sim) {
		flaggedRelationships.put(orgName, sim);
	}
	
	/**
	 * Returns HashMap holding all flagged relationships (nodes with higher similarity than
	 * this nodes parent or the lowest similarity child).
	 * @return HashMap
	 */
	public HashMap<String, Double> getFlaggedRelations(){
		return flaggedRelationships;
	}
	
	/**
	 * Add level value to node in the tree
	 * @param lvl Level of node in tree (type int).
	 */
	public void addLevel(int lvl) {
		level = lvl;
	}
	
	
	public void traverse(int level_) {
		
		level = level_;
		level_++;
		
		for(TreeNode childNode : childNodes) {
			childNode.traverse(level_);
		}
		
		
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	List<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	//HashMap holding node names and similarities flagged as higher than similarities with
	//direct children.
	HashMap<String, Double> flaggedRelationships = new HashMap<>();
	
	//Minimum similarity of all child nodes and this node.
	double minChildSim = 100;
	
	//Name of child node with minimum similarity.
	String minChildName = null;
	
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
	HashSet<String> childNames=new HashSet<String>();
	
	//Descendants of this node.
	
	//Name of the parent of this node.
	String parent;
	
	//Node leve within the tree.
	int level;
}
