package jasper;

import java.util.ArrayList;
import java.util.HashSet;

public class TreeNode {

	String orgName;
	int taxId;
	HashSet<String> children=new HashSet<String>();
	String parent;
	
	// This is the constructor of the class TreeNode
	public TreeNode(String name, String olds) {
	    //this.taxId = id;
	    this.orgName = name;
	    //this.children = kids;
	    this.parent = olds;
	    
    }
	
	public void addChildren(String kid) {
	      children.add(kid);
	   }
	
	public HashSet<String> getChildren() {
	      return children;
	   }
	
	public String getParent() {
	      return parent;
	   }
	
	public String toString() {
		return orgName + ", " + parent + ", " + children;
	}
	
	public void printOrg() {
	    //System.out.println("ID:"+ taxId );
	    System.out.println("Name:" + orgName );
	    System.out.println("children nodes: " + children);
	    System.out.println("parent node:" + parent);
	   }
	
}
