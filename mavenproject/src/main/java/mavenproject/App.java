package mavenproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.LeafDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;



public class App {
	
	/*-----------------------------Methods for checking domination-----------------------------*/

	private static boolean dominates(ArrayList<Point> dominant, Geometry dominated){
		for(Point skylineGeometry : dominant) {
			Point sk =  skylineGeometry;
			//point vs point domination
			if (skylineGeometry instanceof Point) {
				Rectangle p =  (Rectangle) dominated;
				if(sk.x() <= p.x1() && sk.y() <= p.y1() && (sk.x() < p.x1() || sk.y() < p.y1())) {
					return true;
				}
			}
			//point vs mbr domination
			else if (dominated instanceof Rectangle) {
				Rectangle p = (Rectangle) dominated;
				if(sk.x() <= p.x1() && sk.y() <= p.y1() && (sk.x() < p.x1() || sk.y() < p.y1())) {
					return true;
				}
			}
		}
		return false;
	}
	//checking skylines against the new point
	private static boolean dominates(Point newsk, Geometry sk){
		Point p1 = (Point) newsk;
		if (sk instanceof Point) {
			Point p2 = (Point) sk;
			return p1.x() <= p2.x() && p1.y() <= p2.y() && (p1.x() < p2.x() || p1.y() < p2.y());
		}else {
			Rectangle p2 = (Rectangle) sk;
			return p1.x() <= p2.x1() && p1.y() <= p2.y1() && (p1.x() < p2.x1() || p1.y() < p2.y1());
		}
	}
	//minDist method used as the standard for the priority queue
	private static double minDist(Rectangle mbr) {
	    return (mbr.x1() + mbr.y1());
	}
	
	/*-----------------------------Methods for checking domination(END)-----------------------------*/

	
	/*--------------------------------------BRUTE-FORCE--------------------------------------------*/
	public static void skylineBrute(RTree<String, Point> tree) {
		long bruteStart = System.currentTimeMillis();
		ArrayList<Point> skylines = new ArrayList<>(); 
		tree.entries().forEach(entry -> {
			Point current = entry.geometry();
			if (!dominates(skylines, current)) {
				skylines.add(current); //add the point to the skyline list if its not dominated by an existing skyline 
				skylines.removeIf(p -> dominates(current, p)); //check if the added point dominates an already existing skyline(and remove, if true)
			}
				
		});
		long bruteEnd = System.currentTimeMillis();
		System.out.println("Brute force returned: "+skylines.size()+" items");
		System.out.println("Time for the brute force algorithm to return skylines: "+(bruteEnd - bruteStart)+"ms");
	}
	
	/*-------------------------------------BRUTE-FORCE(END)-----------------------------------------*/
	
	
	/*-------------------------------------BRANCH AND BOUND-----------------------------------------*/
	public static void skylineBNB(RTree<String, Point> tree) {
		try (Scanner sc = new Scanner(System.in)) {
			long branchStart = System.currentTimeMillis(); //time starts
			ArrayList<Point> skylines = new ArrayList<>();
			//at least 5 elements in the tree
			if(tree.root().get() instanceof NonLeafDefault) {
				Node<String,?> root = tree.root().get(); 
				//a priority queue where entries are ordered based on their minimum distance
			    PriorityQueue<Node<String, ?>> queue = new PriorityQueue<>(
						(a, b) -> Double.compare(minDist(a.geometry().mbr()), minDist(b.geometry().mbr()))
						);
			    queue.add(root); 
			    while(!queue.isEmpty()) {
			        	Node<String, ?> currentNode = queue.poll(); 
			        	if(currentNode instanceof NonLeafDefault) {
			        		NonLeafDefault<String, ?> nonLeaf = (NonLeafDefault<String, ?>) currentNode;
			        		//for every child store the geometry 
			        		for(Node<String, ?> child: nonLeaf.children()) {
			        			Geometry geometry = child.geometry();
			    				if(!dominates(skylines, geometry)) {
			    					//if the child is not dominated and is a point add it to the skylines list
			    					if (geometry instanceof Point) {
			    						skylines.add((Point) geometry);
			        					skylines.removeIf(skyline -> dominates((Point)geometry, skyline));
			    					}else {
			    						queue.add(child); //if it is a MBR(rectangle) add it to the queue
			    					}
			    				}
			        				
			        		}
			        	}else if (currentNode instanceof LeafDefault) {
			                LeafDefault<String, Point> leaf = (LeafDefault<String, Point>) currentNode;
			                //for each entry of the leaf node
			                for (Entry<String, Point> entry : leaf.entries()) {
			                    Point point = entry.geometry(); //store the geometry
			                    //add point to skylines if it's not dominated
			                    if (!dominates(skylines, point)) {
			                        skylines.add(point);
			                        //remove any skyline dominated by this point
			                        skylines.removeIf(skyline -> dominates(point, skyline));}
			                }
			        	}
			    }
			}
			    long branchEnd = System.currentTimeMillis(); //time ends
				System.out.println("Branch and Bound returned: "+skylines.size()+" items");
				skylineBrute(tree);
			    System.out.println("Time for the Branch and Bound algorithm to return skylines: "+(branchEnd - branchStart)+"ms");
			    System.out.println("Type \"yes\" if you want to reveal the skylines):");
			    String ans = sc.nextLine();
			    if(ans.equals("yes")) {
			        System.out.println("The skylines are: ");
			    	for (Point p : skylines) {
			        	System.out.println("("+p.x()+", "+p.y()+")");
					}	
			    }else {
			     	System.out.println("Exiting...");
			    }
		}

}
	/*--------------------------------BRANCH AND BOUND(END)---------------------------------------*/

	
	/*---------------------------Generating data of specific Correlation------------------------*/
	private static void posCor(Random random) {
		RTree<String, Point>treePos = RTree.create();
		for(int i=0; i<1000; i++) {
			double x = Math.round(random.nextDouble() * 10000.0)/100.0;
			double y = Math.round(x + Math.abs(random.nextGaussian()) * 100.0)/100.0;
			treePos = treePos.add("Point" + i, Geometries.point(x, y) );
	}
			skylineBNB(treePos);
	}
	private static void negCor(Random random) {
		RTree<String, Point>treeNeg = RTree.create();
		for(int i=0; i<1000; i++) {
			double x = Math.round(random.nextDouble() * 10000.0)/100.0;
			double y = Math.round(-x + 100 + Math.abs(random.nextGaussian()) * 100.0)/100.0;
			treeNeg = treeNeg.add("Point" + i, Geometries.point(x, y) );
		}
			skylineBNB(treeNeg);
	}
	
	private static void nonCor(Random random) {
		RTree<String, Point>treeNon = RTree.create();
		for(int i=0; i<1000; i++) {
			double x = Math.round(random.nextDouble() * 10000.0)/100.0;
			double y = Math.round(random.nextDouble() * 1000.0)/100.0;
			treeNon = treeNon.add("Point" + i, Geometries.point(x, y) );
		}
			skylineBNB(treeNon);
	}
	/*---------------------------Generating data of specific Correlation(END)------------------------*/
	
	
	/*-------------------------------------------Prompt for user---------------------------------------*/
	private static int getUserChoice(Scanner sc) {
		System.out.println("Select correlation type: ");
	    System.out.println("1. Positive");
	    System.out.println("2. Negative");
	    System.out.println("3. Non-existent");
	    System.out.println("4. Real-world data");
	    
		while (true) {
			int choice;
			try {
				System.out.println("Enter your choice: ");
			    choice = sc.nextInt();
			    if (choice >= 1 && choice <= 4) {
			        return choice;
			    }
			    System.out.println("Invalid input. Please enter a number between 1 and 4.");
			} catch (Exception e) {
				sc.nextLine(); //Get the next input
			    System.out.println("Invalid input. Please enter a number.");
			}		
		}
	}
	
	/*----------------------------------------Prompt for user(END)-------------------------------------*/

	
	/*----------------------------------------------MAIN-----------------------------------------------*/
		
	public static void main(String[] args) {
		String file = "localPath/final_hotels_dist.csv"; //Replace with your local file path
		String line = "";
		RTree<String, Point> tree = RTree.create();
		Random random = new Random();
		Scanner sc = new Scanner(System.in);
		int counter = 1;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
			while ((line = reader.readLine()) != null) {
			    String[] data = line.split(","); 
			    double x = Double.parseDouble(data[0].trim()); //distance
			    double y = Double.parseDouble(data[1].trim()); //price
			    tree = tree.add("Point" + counter, Geometries.point(x, y));
			    counter++;
			}
		} catch (NumberFormatException e) {
		    System.err.println("Error parsing number at line: " + line);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("I/O exception: " + e.getMessage());
			e.printStackTrace();
		}
		int answer = getUserChoice(sc);
		switch (answer) {
		case 1:
			posCor(random);
			break;
		case 2:
			negCor(random);
			break; 	
		case 3:
			nonCor(random);
			break;
		case 4:
			skylineBNB(tree);
			break;
		}
	}
}

