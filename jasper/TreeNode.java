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
	public TreeNode(String name, String olds, int nodeId) {
	    //this.taxId = id;
	    this.orgName = name;
	    //this.children = kids;
	    this.parent = olds;
	    this.nodeId = nodeId;
	    
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
	 * Add a descendant node to the HashSet of descendant nodes for this node.
	 * 
	 * @param kid Name of child node/organism.
	 */
	public void nodeAddDescendantNames(HashSet<String> desNames) {
		//Iterate over child nodes
		for(TreeNode childNode : childNodes) {
			
			//If the name of the descendant node is not equal to this node
			//This is only applicable for the "life"/"0" node
			if(childNode.orgName != orgName) {
				
				//Add the descendant node name to the input HashSet of names.
				desNames.add(childNode.orgName);
				
				//Run this function on the child nodes of each child node of the starting node.
				childNode.nodeAddDescendantNames(desNames);
			}
		}
	}
	
	/**
	 * Return HashSet containing the names of descendant nodes.
	 * @return descendantNames HashSet<String> of descendant nodes.
	 */
	public HashSet getDescendantNames() {
		return descendentNames;
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
	public String getParentName() {
	      return parent;
	   }
	
	/**
	 * Returns a string of the structure <Organism name>, <Parent Organism/node name>, <Child node names if any>.
	 */
	public String toString() {
		return "Name = " + orgName + ", Parent = " + parent + ", Child names = " + childNames + ", Level = " + level
				+ ", Nodes with identity = " + nodesWithIdentity + ", Identity = " + identity + 
				", Average identity" + averageIdentity();
	}
	
//	/**
//	 * Add similarity values and child node names to a HashMap.
//	 * 
//	 * @param childName Name of direct child node.
//	 * @param similarity Similarity percentage between child node and this node.
//	 */
//	public void addChildSim(String childName, double similarity) {
//		childSims.put(childName, similarity);
//	}
	
//	/**
//	 * Returns the similarity percentage between this node and a child node.
//	 * 
//	 * @param childName The name of the child node.
//	 * @return double similarity percentage
//	 */
//	public double getChildSim(String childName) {
//		double sim = childSims.get(childName);
//		return sim;
//	}
	
	/**
	 * Takes a double similarity percentage and associates that with the parent node.
	 * 
	 * @param similarity Percentage similarity between node and parent of type double
	 */
	public void addParSim(double similarity) {
		parSim = similarity;
	}
	
//	/**
//	 * Returns the minimum similarity percentage of all descendants.
//	 * 
//	 * @return Similarity between node and child node with lowest similarity.
//	 */
//	public double minimumDescendantSim() {
//
//		for(String childName : childNames) {
//			if(childSims.get(childName) < minChildSim) {
//				minChildSim = childSims.get(childName);
//				//minChildName = childName;
//			}
//		}
//		return minChildSim;
//	}
	
//	/**
//	 * Returns name of child node with lowest similarity to this node.
//	 * 
//	 * @return Node name of child with lowest similarity (type String).
//	 */
//	public String minimumDescendantName() {
//		for(String childName : childNames) {
//			
//			if(childSims.get(childName) < minChildSim) {
//				minChildName = childName;
//			}
//		}
//		return minChildName;
//	}
	
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
	
	/**
	 * Adds hierarchical levels to nodes recursively through the tree.
	 * @param level_
	 */
	public void traverse(int level_) {
		
		level = level_;
		level_++;
		
		
		for(TreeNode childNode : childNodes) {
			
			if(childNode.orgName != orgName) {
				childNode.traverse(level_);
				
			}
			//childNode.traverse(level_);
		}
		
	}
	
	/**
	 * Returns hierarchical level of the node.
	 * @return int level
	 */
	public int getLevel() {
		return level;
	}
	
	
	/**
	 * Tests to see if argument is descendant of this.
	 * A node is considered a descendant of itself.
	 * @param nodeB 
	 * @return true if nodeB is a descendant of this
	 */
	public boolean isDescendantOf(final TreeNode nodeB) {
		if(this == nodeB) {return true;}
		else if(this.parentNode == this){return false;}
		else {return parentNode.isDescendantOf(nodeB);}
	}
	
	
	public boolean isAncestorOf(final TreeNode nodeB) {
		return nodeB.isDescendantOf(this);
	}
	
	public void resetIdentity() {
		identity = 0;
		identitySum = 0;
		nodesWithIdentity = 0;
		sizeSum = 0;
		

		for(TreeNode childNode : childNodes) {

			if(childNode.orgName != orgName) {
				childNode.resetIdentity();

			}
		}
	}
	
/**
 * Set nodes identity to the average identity of its descendants.
 * 
 * @param queryNode int ID of node relative to this node.
 */
	public void percolateIdentityUp(int queryNode) {

		if(identity > 0 && nodeId != queryNode) {nodesWithIdentity = 1; sizeSum = size; identitySum = identity;}

		for(TreeNode childNode : childNodes) {

			if(childNode.orgName != orgName) {
				childNode.percolateIdentityUp(queryNode);

				nodesWithIdentity+=childNode.nodesWithIdentity;
				identitySum+=childNode.identitySum;
				sizeSum+=childNode.sizeSum;

			}
		}
	}
	
	
	public double averageIdentity() {
		if(nodesWithIdentity < 1) {return 0;}
		else {return identitySum / nodesWithIdentity;}
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	List<TreeNode> childNodes = new ArrayList<TreeNode>();
	
	TreeNode parentNode = null;
	
	//HashMap holding node names and similarities flagged as higher than similarities with
	//direct children.
	HashMap<String, Double> flaggedRelationships = new HashMap<>();
	
	//Minimum similarity of all child nodes and this node.
	double minChildSim = 100;
	
	//Name of child node with minimum similarity.
	String minChildName = null;
	
	//HashMap holding the names and similarity values between any direct children nodes
	//and this node.
	//HashMap<String, Double> childSims = new HashMap<>();
	
	//Similarity percentage to the parent node.
	double parSim = -1;
	
	//Organisms name associated with this node.
	String orgName;
	
	//Taxanomic ID of this organism.
	int taxId;
	
	final int nodeId;
	
	//HashSet of direct children of this node.
	HashSet<String> childNames=new HashSet<String>();
	
	HashSet<String> descendentNames=new HashSet<String>();
	
	//Descendants of this node.
	
	//Name of the parent of this node.
	String parent;
	
	//Node leve within the tree.
	int level;
	
	
	double identity = 0.0;
	
	//
	long size = 0;
	
	long descendantSize = 0;
	
	int numDescendants = 0;
	
	double identitySum = 0;
	
	long nodesWithIdentity = 0;
	
	long sizeSum = 0;
}
