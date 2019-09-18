import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;


import matrix.Matrix;
import matrix.MatrixMathematics;
import matrix.NoSquareException;

//This class performs the actions for calculating rank

public class TeamRank {
	/*ID #'s for our favorite players
	public static int KEVIN = 0;
	public static int MATT = 1;
	public static int PATRICK = 2;
	public static int AARON = 3;
	public static int YUESHAN = 4;
	public static int OWEN = 5;
	public static int ERIC = 6;*/
	
	public static String doc = "SmashRankingsDocument.txt";
	
	//Tracks the number of players
	static int players;
	
	//Tracks the current selection
	static int selection;
	
	//Scanner to read user input
	static Scanner scan = new Scanner(System.in);
	
	//The appropriate matrices
	static Matrix M;
	static Matrix q;
	
	//Variables for Matrix update
	static int player1;
	static int player2;
	static int scoreDiff;
	
	//Hashmap for output of scores
	static HashMap<String, Double> hm = new HashMap<String, Double>();
	static String curr;
	
	//For File data management, stores matrix values
	static List<Integer> numbers;
	
	//Reads data from a file
	public static void readFile() throws IOException {
		Path filePath = Paths.get(doc);
		Scanner scanner = new Scanner(filePath);
		numbers = new ArrayList<>();
		
		while (scanner.hasNext()) {
		    if (scanner.hasNextInt()) {
		        numbers.add(scanner.nextInt());
		    } else {
		        scanner.next();
		    }
		}
		
		/*Document format is as follows:
		 * Fills in the matrix starting with the first row and going left to right. 
		 * Last (players) digits of the document make up the scoreDiff q.*/
		//Now we need to make the matrix M
		//System.out.print("How many players? ");
		players = 7;
		M = new Matrix(players, players);
		q = new Matrix(players, 1);
		
		int count = 0; //tracks our location in the document
		
		for (int i = 0; i < M.getNrows(); i++) {
			for (int j = 0; j < M.getNrows(); j++) {
				M.setValueAt(i, j, numbers.get(count++));
			}
		}
		
		for (int i = 0; i < q.getNrows(); i++) {
			q.setValueAt(i, 0, numbers.get(count++));
		}
	}
	
	//Writes data to a file
	public static void writeFile() throws IOException {
		numbers = new ArrayList<>();
		
		PrintWriter writer = new PrintWriter(doc);
		
		//Get values from Matrix M
		for (int i = 0; i < M.getNrows(); i++) {
			for (int j = 0; j < M.getNrows(); j++) {
				writer.print((int) M.getValueAt(i, j) + " ");
			}
		}
		
		//Get values from vector q
		for (int row = 0; row < q.getNrows(); row++){
			writer.print((int) q.getValueAt(row, 0) +" ");
		}
		
		//Close the writer
		writer.close();
	}
	
	//Start a new league!
	public static void newLeague() {
	
		System.out.print("How many players? ");
		players = scan.nextInt();
		M = new Matrix(players, players);
		q = new Matrix(players, 1);
		
		//Initialize the values
		for (int i = 0; i < M.getNrows(); i++) {
			for (int j = 0; j < M.getNrows(); j++) {
				M.setValueAt(i, j, 0);
			}
		}
		
		for (int i = 0; i < q.getNrows(); i++) {
			q.setValueAt(i, 0, 0);
		}
	}
	
	//Input a game
	public static void inputGame() throws IOException {
		System.out.print("Home: ");
		player1 = scan.nextInt();
		System.out.print("\nAway: ");
		player2 = scan.nextInt();
		
		System.out.print("\nHow much did home win by? ");
		scoreDiff = scan.nextInt();
		
		//update the matrices
		M.MasseysUpdate(player1, player2, scoreDiff);
		q.rankingsUpdate(player1, player2, scoreDiff);
		
		//Save the file
		writeFile();
	}
	
	//Displays Rankings
	public static void viewRankings() throws NoSquareException, IOException {
		Matrix tempM = MatrixMathematics.createCopyMatrix(M);
		Matrix tempQ = MatrixMathematics.createCopyMatrix(q);
		//set last row to 1's
		for (int col = 0; col < tempM.getNcols(); col++){
			tempM.setValueAt(tempM.getNrows()-1, col, 1);
		}
		//set last entry to 0
		tempQ.setValueAt(tempQ.getNrows()-1, 0, 0);
		
		//solve the matrix equation for rankings matrix
		//(M'*M)r = M'*q, or r = (M'*M)^-1  * M'*q
		Matrix transM = MatrixMathematics.transpose(tempM);
		Matrix temp = MatrixMathematics.multiply(transM, tempM);
		Matrix MtransposeQ = MatrixMathematics.multiply(transM, tempQ);
		Matrix inverse = MatrixMathematics.inverse(temp);
		Matrix rankings = MatrixMathematics.multiply(inverse, MtransposeQ);
		
		//Put rankings into hashmap
		for (int i = 0; i < rankings.getNrows(); i++) {
			if (i == 0) {
				curr = "Kevin";
			} else if (i == 1) {
				curr = "Matt";
			} else if (i == 2) {
				curr = "Patrick";
			} else if (i == 3) {
				curr = "Aaron";
			} else if (i == 4) {
				curr = "Yueshan";
			} else if (i == 5) {
				curr = "Owen";
			} else if (i == 6) {
				curr = "Eric";
			}
			hm.put(curr, rankings.getValueAt(i, 0));
		}	
		
		//Sort rankings by value
		Map<String, Double> hm1 = sortByValue(hm);
		
		//Display the rankings in sorted hashmap
		System.out.println("Name        Ranking Score");
        for (Entry<String, Double> en : hm1.entrySet()) { 
        	System.out.print(en.getKey() + "\t\t");
        	System.out.printf("%.2f", en.getValue());
        	System.out.println();
           // System.out.println(en.getKey() + "\t\t" + en.getValue()); 
        } 
        System.out.println("\n");
        
        //Save the file
        writeFile();
	}
	
	//Helper method to sort HashMap
	public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Double> > list = 
               new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
            public int compare(Map.Entry<String, Double> o1,  
                               Map.Entry<String, Double> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
        for (Map.Entry<String, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
	
	public static void menu() throws NoSquareException, IOException {
		System.out.println("Select an option: ");
		System.out.println("1. Input a game");
		System.out.println("2. View the current rankings");
		
		selection = scan.nextInt();
		if (selection == 1) {
			inputGame();
		} else if (selection == 2) {
			viewRankings();	
		} else {
			System.out.println("Please select a valid option!");
		}
	}
	
}
