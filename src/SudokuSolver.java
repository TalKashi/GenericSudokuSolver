import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import lpsolve.*;

public class SudokuSolver {
	
	static LpSolve lp;
	static BufferedReader br = null;
	static final int VAR_NUMBER = 729;
	static final int CELLS_NUM = 81;
	static final int ROW_SIZE = (int) Math.sqrt(CELLS_NUM);
	static final int COLUMN_SIZE = ROW_SIZE;
	static final int MAX_VALUE = ROW_SIZE;
	static double[] objFunc = new double[VAR_NUMBER + 1];
	static int[] colno = new int[COLUMN_SIZE];
	static double[] sparseRow = new double[ROW_SIZE];
	
  public static void main(String[] args) {
	  
	try {
		final long start = System.nanoTime();
		setFilePath("C:\\temp\\5.txt");
		
		String line = null;
		int readUntil = 0;
		
		
		while ((line = getNextLine()) != null && (readUntil < 10000)) {
			initLp();
			loadMatrixFromString(line);
			lp.setObjFn(objFunc);
			lp.setMaxim();
			lp.writeLp("model"+(readUntil + 1) +".lp");
			lp.solve();
			//lp.
			readUntil++;
			System.out.println(readUntil);
			//lp.deleteLp();
		}
		lp.deleteLp();
		final long end = System.nanoTime();
		System.out.println(formatTime(end - start));
		System.out.println("DONE");
		//lp.deleteLp();
		closeFile();
		
	} catch (LpSolveException e) {
		e.printStackTrace();
	}
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
				//for (int value = 0; value < MAX_VALUE; value++) {
					if(charArr[ROW_SIZE * i + j] - '0' == 0) {
						//addZeros(i, j);
					} else {
						//System.out.println(createString(i + 1, j + 1, charArr[ROW_SIZE * i + j] - '0'));
						addCoef(i, j, charArr[ROW_SIZE * i + j] - '0');
					}
				//}
			}
		}
		//colno[0] = getIndex()
//		for(int i = 0; i < colno.length; i++) {
//			for(int j = 0; j < colno.length; j++) {
//				for (int k = 0; k < 9; k++) {
//					colno[0] = getIndex(i + 1, j + 1, k + 1);
//					if ((charArr[9 * i + j] - '0' != (k + 1)) && (charArr[9 * i + j] - '0' != 0)) {
//						lp.addConstraintex(1, sparseRow, colno, LpSolve.LE, 0);
//					} else if ((charArr[9 * i + j] - '0' == (k + 1))) {
//						lp.addConstraintex(1, sparseRow, colno, LpSolve.EQ, 1);
//					} else {
//						lp.addConstraintex(1, sparseRow, colno, LpSolve.LE, 1);
//					}
//				}
				
				//colno[0] = getIndex(i + 1, j + 1, (charArr[9 * i + j] - '0'));
				// ij1 + 2ij2 + 3ij3 + ..... + 9ij9 >= charArr[9 * i + j] - '0'
//				for (int k = 0; k < colno.length; k++) {
//					//colno[k] = counter++;
//				}
				//lp.addConstraintex(1, sparseRow, colno, LpSolve.EQ, 1);
				
				
				
//				if(charArr[9 * i + j] - '0' != 0 ) {
//					addConstraint(i, j, charArr[9 * i + j] - '0');
//				}
//			}
//		}
//		colno[0] = getIndex(1, 5, 7);
//		lp.addConstraintex(1, sparseRow, colno, LpSolve.EQ, 1);
		lp.setAddRowmode(false);
	}
	
	private static void addCoef(int i, int j, int value) {
		for (int row = 0; row < ROW_SIZE; row++) {
			int bound = -1000000;
			if (row == i) {
				bound = 1000000;
				//System.out.println(createString(i + 1, j + 1, value));
				//System.out.println(getIndex(row + 1, j + 1, value));
				objFunc[getIndex(row + 1, j + 1, value)] = bound;
			} else {
				objFunc[getIndex(row + 1, j + 1, value)] = bound;
			}
		}
		
		for (int column = 0; column < COLUMN_SIZE; column++) {
			//System.out.println(createString(i + 1, j + 1, value));
			int bound = -1000000;
			if (column == j) {
				bound = 1000000;
				objFunc[getIndex(i + 1, column + 1, value)] = bound;
			} else {
				objFunc[getIndex(i + 1, column + 1, value)] = bound;
			}
		}
	}
	
//	private static void addZeros(int i, int j) {
//		for (int row = 0; row < ROW_SIZE; row++) {
//			for (int column = 0; column < COLUMN_SIZE; column++) {
//				//objFunc[getIndex(i + 1, j + 1, value + 1)] = 1;
//			}
//		}
//		
//	}

//	static int getFirstIndex() {
//		
//	}

//	private static void addConstraint(int i, int j, int value) throws LpSolveException {
//		int j1;
//		int v;
//		for(int k2 = 1; k2 <= 9; k2++) {
//			j1 = 0;
//			v = 0;
//			if(k2 == value) {
//				v = 1;
//			}
//			colno[j1] = getIndex(i + 1, j + 1, k2);
//			//row[j1++] = 1;
//			lp.addConstraintex(1, sparseRow, colno, LpSolve.EQ, v);
//		}
//	}
	


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
	    //if(arr[1].contains("E")) seconds = Double.parseDouble(arr[1]);
	    //else seconds += Double.parseDouble(arr[1]);


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
			
			// Only VALUE_SIZE cells can be assign to each value
//			for (int value = 0; value < MAX_VALUE; value++) {
//				for (int row = 0; row < ROW_SIZE; row++) {
//					for (int column = 0; column < COLUMN_SIZE; column++) {
//						colno[9 * row + column] = getIndex(row + 1, column + 1, value + 1);
//					}
//				}
//				lp.addConstraintex(CELLS_NUM, sparseRow, colno, LpSolve.EQ, MAX_VALUE);
//			}

			for (int i = 0; i < VAR_NUMBER; i++) {
				lp.setBinary(i + 1, true);
			}
			lp.setAddRowmode(false);
		} catch (LpSolveException e) {
			e.printStackTrace();
		}
	}
		
}
