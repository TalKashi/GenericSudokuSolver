import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lpsolve.*;

public class SudokuSolver {
	
	static final int INVALID = 0;
	static final int SINGLE_PROBLEM = 1;
	static final int ALL = 2;
	static final int EXIT = 3;
	
	static final String ALL_STRING = "ALL";
	static final String EXIT_STRING = "Q";
	
	static LpSolve lp;
	static BufferedReader br = null;
	static final int VAR_NUMBER = 729;
	static final int CELLS_NUM = 81;
	static final int ROW_SIZE = (int) Math.sqrt(CELLS_NUM);
	static final int COLUMN_SIZE = ROW_SIZE;
	static final int BOX_SIZE = ROW_SIZE;
	static final int MAX_VALUE = ROW_SIZE;
	static double[] objFunc = new double[VAR_NUMBER + 1];
	static int[] colno = new int[COLUMN_SIZE];
	static double[] sparseRow = new double[ROW_SIZE];
	static double[] resultMatrix = new double[VAR_NUMBER];
	
  public static void main(String[] args) {
	  
	try {
		
		boolean shouldExit = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	  
		System.out.println("Welcome to the Sudoku Solver powered by a Generic algorithm");
		while(!shouldExit) {
			System.out.println("Please enter 81 digits that represent the Sudoku puzzle, or ALL to run over all 10,000 problems, or Q to quit:");
			String line = reader.readLine();
			switch(validateInput(line)) {
				case INVALID:
					System.out.println("Invalid input");
					break;
				  case SINGLE_PROBLEM:
					  initLp();
					  loadMatrixFromString(line);
					  lp.setObjFn(objFunc);
					  lp.setMaxim();
					  if(lp.solve() != LpSolve.OPTIMAL) {
						  System.err.println("No solution for the given line. Please enter a new valid sudoku puzzle");
					  } else {
						  printOutput();
					  }				  
					  lp.writeLp("modelGeneric.lp");
					  lp.deleteLp();
					  break;
				  case ALL:
					  setFilePath("C:\\temp\\5.txt");
					  final long start = System.nanoTime();
					  int lineNumber = 0;
					  while ((line = getNextLine()) != null && lineNumber < 10000) {
						  initLp();
						  loadMatrixFromString(line);
						  lp.setObjFn(objFunc);
						  lp.setMaxim();
						  if(lp.solve() != LpSolve.OPTIMAL) {
							  System.err.println("NOT OPTIMAL for line " + lineNumber);
						  }
						  lineNumber++;
						  System.out.println("Finished line " + lineNumber);
						  lp.deleteLp();
					  }
					  final long end = System.nanoTime();
					  System.out.println("finished in " + formatTime(end - start));
					  closeFile();
					  break;
				  case EXIT:
					  shouldExit = true;
					  break;
			}
		}
	} catch (LpSolveException e) {
		  e.printStackTrace();
	}
	catch (IOException e) {
		  e.printStackTrace();
	}
	System.out.println("Thanks for using our Specific Sudoku solver model!");	
  }
  
  private static int validateInput(String line) {
	  if(line == null) {
		  return INVALID;
	  }
		
	  if(line.length() == 1) {
		  if(line.toUpperCase().contains(EXIT_STRING)) {
			  return EXIT;
		  } else {
			  return INVALID;
		  }
	  }
		
	  if(line.length() == 3) {
		  if(line.toUpperCase().contains(ALL_STRING)) {
			  return ALL;
		  } else {
			  return INVALID;
		  }
	  }
		
	  if (line.length() != 81) {
		  return INVALID;
	  }
	  char[] arr = line.toCharArray();
	  for(int i = 0; i < arr.length; i++) {
		  if(arr[i] < '0' || arr[i] > '9') {
			  return INVALID;
		  }
	  }
	  return SINGLE_PROBLEM;
  }
  
  private static void printOutput() throws LpSolveException {
		
	  lp.getVariables(resultMatrix);
		
	  for (int i = 0 ; i< resultMatrix.length; i++) {
		  if(resultMatrix[i] != 0) {
			  System.out.print((i % 9) + 1);
		  }
	  }
	  System.out.println();
	  System.out.println();
  }
  
  public static int getIndex(int row, int column, int value) {
	  return 81 * (row - 1) + 9 * (column - 1) + value;
  }
	
	static void loadMatrixFromString(String matrixStr) throws LpSolveException {
		lp.setAddRowmode(true);
		char[] charArr = matrixStr.toCharArray();
		
		for(int i = 0; i < ROW_SIZE * COLUMN_SIZE * MAX_VALUE; i++) {
			objFunc[i] = 0;
		}
		
		for(int i = 0; i < ROW_SIZE; i++) {
			for(int j = 0; j < COLUMN_SIZE; j++) {
					if(charArr[ROW_SIZE * i + j] - '0' == 0) {
					} else {
						addCoef(i, j, charArr[ROW_SIZE * i + j] - '0');
					}
			}
		}

		lp.setAddRowmode(false);
	}
	
	private static void addCoef(int i, int j, int value) {
		int negativeBound = -100;
		for (int row = 0; row < ROW_SIZE; row++) {
			
			if (row == i) {
				objFunc[getIndex(row + 1, j + 1, value)] = -negativeBound;
			} else {
				objFunc[getIndex(row + 1, j + 1, value)] = negativeBound;
			}
		}
		
		for (int column = 0; column < COLUMN_SIZE; column++) {
			if (column == j) {
				objFunc[getIndex(i + 1, column + 1, value)] = -negativeBound;
			} else {
				objFunc[getIndex(i + 1, column + 1, value)] = negativeBound;
			}
		}
		
		int boxXTopCorner = (i / 3) * 3;
		int boxYTopCorner = (j / 3) * 3;
		
		for(int offset = 0; offset < BOX_SIZE; offset++) {
			int boxX = boxXTopCorner + (offset / 3);
			int boxY = boxYTopCorner + (offset % 3);
			if(boxX == i && boxY == j) {
				objFunc[getIndex(boxX + 1, boxY + 1, value)] = -negativeBound;
			} else {
				objFunc[getIndex(boxX + 1, boxY + 1, value)] = negativeBound;
			}
		}
		for(int k = 1; k <= ROW_SIZE; k++) {
			if(k == value) {
				objFunc[getIndex(i + 1, j + 1, k)] = -negativeBound;
			} else {
				objFunc[getIndex(i + 1, j + 1, k)] = negativeBound;
			}
		}
	}
	
	private static void closeFile() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getNextLine() {
		try {
			return br.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	private static void setFilePath(String path) {
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static String createString(int i, int j, int k) {
		return "" + i + j + k;
	}
	
	// A method that converts the nano-seconds to Seconds-Minutes-Hours form
	private static String formatTime(long nanoSeconds)
	{
	    int hours, minutes, remainder, totalSecondsNoFraction, seconds;
	    double totalSeconds;


	    // Calculating hours, minutes and seconds
	    totalSeconds = (double) nanoSeconds / 1000000000.0;
	    String s = Double.toString(totalSeconds);
	    String [] arr = s.split("\\.");
	    totalSecondsNoFraction = Integer.parseInt(arr[0]);
	    hours = totalSecondsNoFraction / 3600;
	    remainder = totalSecondsNoFraction % 3600;
	    minutes = remainder / 60;
	    seconds = remainder % 60;

	    // Formatting the string that conatins hours, minutes and seconds
	    StringBuilder result = new StringBuilder();
	    if (hours < 10) {
	    	result.append("0" + hours);
	    }
	    else {
	    	result.append(hours);
	    }
	    
	    if (minutes < 10) {
	    	result.append(":0" + minutes);
	    }
	    else {
	    	result.append(":" + minutes);
	    }
	    if (seconds < 10) {
	    	result.append(":0" + seconds);
	    }
	    else {
	    	result.append(":" + seconds);
	    }
	    result.append(":" + (nanoSeconds / 10000000));
	    
	    return result.toString();
	}
	
	private static void initLp() {
		try {
			lp = LpSolve.makeLp(0, VAR_NUMBER);
			lp.setVerbose(LpSolve.IMPORTANT);
			lp.setAddRowmode(true);
			
			// Setup variable names
			int curLine = 1;
			for(int i = 1; i <= ROW_SIZE; i++) {
				for(int j = 1; j <= COLUMN_SIZE; j++) {
					for(int k = 1; k <= MAX_VALUE; k++) {
						lp.setColName(curLine++, "x" + createString(i, j, k));
					}
				}
			}
			
			// Set Variables coefficients to 1
			for (int i = 0; i < sparseRow.length; i++) {
				sparseRow[i] = 1;
			}
			
			// Dealing with only one value per cell
			for (int row = 0; row < ROW_SIZE; row++) {
				for (int column = 0; column < COLUMN_SIZE; column++) {
					for (int value = 0; value < MAX_VALUE; value++) {
						colno[value] = getIndex(row + 1, column + 1, value + 1);
					}
					lp.addConstraintex(ROW_SIZE, sparseRow, colno, LpSolve.EQ, 1);
				}
			}
			
			// Dealing with only one value per row
			for (int row = 0; row < ROW_SIZE; row++) {
				for (int value = 0; value < MAX_VALUE; value++) {
					for (int column = 0; column < COLUMN_SIZE; column++) {
						colno[column] = getIndex(row + 1, column + 1, value + 1);
					}
					lp.addConstraintex(ROW_SIZE, sparseRow, colno, LpSolve.EQ, 1);
				}
			}
			
			// Dealing with only one value per column
			for (int column = 0; column < COLUMN_SIZE; column++) {
				for (int value = 0; value < MAX_VALUE; value++) {
					for (int row = 0; row < ROW_SIZE; row++) {
						colno[row] = getIndex(row + 1, column + 1, value + 1);
					}
					lp.addConstraintex(COLUMN_SIZE, sparseRow, colno, LpSolve.EQ, 1);
				}
			}
			
			// Dealing with only one value per box
			int sqrt = (int)Math.sqrt(MAX_VALUE);
			for (int m = 0; m < sqrt; m++) {
				for (int i = 0; i < sqrt; i++) {
					for (int value = 0; value < MAX_VALUE; value++) {
						int j = 0;
						for (int k = 0; k < sqrt; k++) {
							for (int n = sqrt * i; n < sqrt * i + sqrt ; n++) {
								colno[j++] = getIndex(k + (sqrt * m) + 1, n + 1, value + 1);
							}
						}
						lp.addConstraintex(9, sparseRow, colno, LpSolve.EQ, 1);
					}
				}
			}

			for (int i = 0; i < VAR_NUMBER; i++) {
				lp.setBinary(i + 1, true);
			}
			lp.setAddRowmode(false);
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
	}	
}
