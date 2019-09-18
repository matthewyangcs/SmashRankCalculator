import java.io.IOException;
import java.util.Scanner;
import matrix.*;


public class Driver {
	static Scanner userInput = new Scanner(System.in);
	static int choice = 0;
	
	public static void main(String[] args) throws NoSquareException, IOException {
		
		//New league, or text file?
		
		while (choice != 1 && choice != 2) {
			//New league, or text file?
			System.out.print("Input a new league (1), or read from text file? (2) ");
			choice = userInput.nextInt();
			
			if (choice == 1) {
				//Start a new league
				TeamRank.newLeague();
			} else {
				//Input read from text file
				TeamRank.readFile();
			}
		}
		
		while(true) {
			TeamRank.menu();
		}
	}
}
