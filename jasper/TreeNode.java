package jasper;

public class TreeNode {

	String orgName;
	int taxId;
	String[] children;
	String parent;
	
	// This is the constructor of the class Organism
	public TreeNode(String name, String[] kids, String olds) {
	    //this.taxId = id;
	    this.orgName = name;
	    this.children = kids;
	    this.parent = olds;
	    
    }
	
	
	public void printOrg() {
	    //System.out.println("ID:"+ taxId );
	    System.out.println("Name:" + orgName );
	    System.out.println("children nodes: " + children);
	    System.out.println("parent node:" + parent);
	   }
	
}
