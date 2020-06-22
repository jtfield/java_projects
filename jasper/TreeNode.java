package jasper;

import java.util.HashSet;

public class TreeNode {

	String orgName;
	int taxId;
	HashSet<String> children=new HashSet<String>();
	String parent;
	
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
	

	
}
