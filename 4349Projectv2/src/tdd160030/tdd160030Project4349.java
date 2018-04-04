// CS 4349.003 Project (Spring 2018). 
// Project: Print Neatly given a line with character length M uising dynamic programming

// tdd160030 - Tiffany Do
package tdd160030;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class tdd160030Project4349{
    public static void main(String[] args) throws FileNotFoundException {
	Scanner in;
	int M = 80;
	char extra = ' ';
	if(args.length == 0 || args[0].equals("-")) {
	    in = new Scanner(System.in);
	} else {		
	    File inputFile = new File(args[0]);
	    in = new Scanner(inputFile);
	}
	if (args.length > 1) {
	    M = Integer.parseInt(args[1]);
	    if(M < 0) {
		M = -M;
		extra = '+';
	    }
	}
        //list to hold amount of penalties per paragraph to display in additional info section
        ArrayList<Integer> penaltyPerParagraph = new ArrayList<>();
        ArrayList<String> listWords = new ArrayList<>();
	while(in.hasNextLine()) {
	    String s = in.nextLine();
	    if(s.trim().isEmpty()) {
                // Empty line, marking end of paragraph
                //runs DP at a completely empty line(new paragraph) in case words are separated by lines
                if(listWords.size() > 0){
                    printNeatly(listWords, listWords.size(), M, penaltyPerParagraph, extra);
                    listWords.clear();
                    System.out.println("\n");
                }
	    } else {  // Use scanner to break line into words
		Scanner strScanner = new Scanner(s);
		while(strScanner.hasNext()) {
		    String word = strScanner.next();  // Next word of input
                    listWords.add(word); //adds words to list
		}
	    }
	}
        //runs DP for a document with only 1 paragraph
        if(listWords.size() > 0){
            printNeatly(listWords, listWords.size(), M, penaltyPerParagraph, extra);
            System.out.println("\n");
        }
        //print out additional details
        int sum = penaltyPerParagraph.stream().mapToInt(Integer::intValue).sum();
        System.out.print(sum + "\n\nAdditional details:\nNumber of paragraphs: " + penaltyPerParagraph.size() + "\nPenalties per paragraph: ");
        //prints out the penalty for every paragraph
        penaltyPerParagraph.forEach((s) -> {
            System.out.print(s + " ");
        });
    }
    
    //main DP to print program
    static void printNeatly (ArrayList<String> l, int n, int M, ArrayList<Integer> pp, char extra)
    {
        //stores amount of penalties (extra spaces). [i][j] is extra spaces of a line from i to j
        int penalty[][] = new int[n+1][n+1];
     
        //stores cost of a line. [i][j] is cost of a line from i to j
        int lineCost[][]= new int[n+1][n+1];
     
        //cost if words from 1...i are used
        int cost[] = new int[n+1];
     
        //use table to hold position of words
        int use[] = new int[n+1];
        
        //fills out penalty and lineCost tables
        makeTables(l, n, M, penalty, lineCost);
        
        //base
        cost[0] = 0;
        //main DP
        for (int j = 1; j <= n; j++)
        {
            cost[j] = Integer.MAX_VALUE;
            for (int i = 1; i <= j; i++)
            {
                //checks for overflow, then for DP recurrence
                if (cost[i-1] != Integer.MAX_VALUE && lineCost[i][j] != Integer.MAX_VALUE && (cost[i-1] + lineCost[i][j] < cost[j]))
                {
                    cost[j] = cost[i-1] + lineCost[i][j];
                    use[j] = i;  //stores position in use for later retrieval
                }
            }
        }
        pp.add(cost[n]); //add penalty to penalty list for display of additional info
        //prints the paragraph
        printIt(use, n, l, extra, lineCost);
    }
    
    //makes tables for cost of lines and penalties. Penalty and linecost arrays are passed as parameters that are directly modified
    static void makeTables(ArrayList<String> l, int n, int M, int[][] penalty, int[][] lc)
    {
        for (int i = 1; i <= n; i++)
        {
            //penalty if a word is placed on a line by itself
            penalty[i][i] = M - l.get(i-1).length();
            //calculates penalty from i to j 
            for (int j = i+1; j <= n; j++)
                penalty[i][j] = penalty[i][j-1] - l.get(j-1).length() - 1;
        }
        
        //calculates cost of each line 
        for (int i = 1; i <= n; i++)
        {
            for (int j = i; j <= n; j++)
            {
                if (penalty[i][j] < 0)
                    lc[i][j] = Integer.MAX_VALUE;
                else if (j == n && penalty[i][j] > -1)
                    lc[i][j] = 0;
                else
                    lc[i][j] = penalty[i][j]*penalty[i][j]*penalty[i][j];
            }
        }
    }
    
    //helper method to print
    private static int printIt(int[] use, int n, ArrayList<String> wl, char extra, int[][] lineCost)
    {
       int j;
       int i = use[n];
       if (use[n] == 1)
           j = 1;
       else
           j = printIt (use, i-1, wl, extra, lineCost) + 1;
       if(i != 1) //only prints a new line if it is not the first line
           System.out.println();
       int numSpaces = (int)(Math.cbrt((lineCost[i][n])));
       for(int k = i; k < n + 1; k++)
       {
           if(numSpaces > 0)
           {
               if(k == n)
               {
                   //makes sure all extra spaces are added
                   for(int r = 0; r < numSpaces; r++)
                       System.out.print(extra);
               }
               //distribute spaces
               else if(k % 2 == 0 && k != i)
               {
                   numSpaces--;
                   System.out.print(extra);
               }
           }
           System.out.print(wl.get(k-1) + " ");
       }
       return j;
    }
}