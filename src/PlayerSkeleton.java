import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PlayerSkeleton {
	
	private State s = null;
	private TFrame f = null;
	
	private double[] weight = {
		-4.500158825082766,
		-3.2178882868487753,
		-9.348695305445199,
		-7.899265427351652,
		-3.3855972247263626,
		3.4181268101392694,
	};

	private static final int LANDING_HEIGHTS_INDEX = 0;
	private static final int ROWS_TRANSITION_INDEX = 1;
	private static final int COLS_TRANSITION_INDEX = 2;
	private static final int NUM_HOLES_INDEX       = 3;
	private static final int WELL_SUMS_INDEX       = 4;
	private static final int ROWS_COMPLETED_INDEX  = 5;
	
	static int index = 0;
	
//	public PlayerSkeleton(double[] param) {
//		weight = param;
//	}		
	
	public PlayerSkeleton(double[] param) {
		s = new State();
		f = new TFrame(s);
		weight = param;
		
		// set up a log file
		Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  

	    try {  

	        fh = new FileHandler("C:/temp/test/MyLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  


	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    logger.info("Hi How r u?"); 
	    
	}
	
	public void run() {
		//State s = new State();
		//new TFrame(s);
		
		while(!s.hasLost()) {
			
			int move = this.pickMove(s, s.legalMoves());
			index++;
			
			if(move < 0) // every move will lose the game
				break;
			s.makeMove(move);
			
//			try {
//				Thread.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		if (s.getRowsCleared() > 500) {
			System.out.println("You have completed "+s.getRowsCleared()+" rows.");
			System.out.printf("%f, %f, %f, %f, %f, %f\n", weight[0], weight[1], weight[2], weight[3], weight[4], weight[5]);
		}
	}
	
	public int getRowsCleared(){
		return s.getRowsCleared();
	}
	
	
	public int pickMove(State s, int[][] legalMoves) {
		
		int move = -1;
		double max = -Double.MAX_VALUE;
		
		for(int i=0; i<legalMoves.length; i++) {
			
			//copy top and field from the previous state
			int[][] sField = s.getField();
			int[][] field = new int[sField.length][];
			
			for(int j=0; j<sField.length; j++) {
				int length = sField[j].length;
				field[j] = new int[length];
				System.arraycopy(sField[j], 0, field[j], 0, length);
			}
			
			int[] top = Arrays.copyOf(s.getTop(), s.getTop().length);
			
			
			int pieceLandingHeight = getLandingHeight(s, top, legalMoves[i]);
			
			// update the field
			int canCarryOn = updateFieldAndTop(s, legalMoves[i], field, top);
			
			if(canCarryOn < 0)
				continue; //this move will lose the game
			
			int maxHeight = getMaxHeight(top); // get max height
			int pieceHeight = getPieceHeight(s.getNextPiece(), legalMoves[i][State.ORIENT]); 
			
			//int numRowsRemoved = getNumberRowsRemoved(field, top, maxHeight);
			double heuristics = getHeuristics(field, top, maxHeight, 
					pieceHeight, pieceLandingHeight);
			
			double cost = heuristics;
					
			if(max < cost) {
				max = cost;
				move = i;
			}
		}
		
		System.out.println("move made is " + move);
		return move;

	}
	
	/*
	 * Update field and top according to the move
	 * the updated field and top will only be used for this move only
	 * */ 
	private int updateFieldAndTop(State s, int legalMove[], int[][] field
			, int[] top) {
		
		// Tetris block information (piece, orient, slot)
 		int piece = s.getNextPiece();
		int orient = legalMove[State.ORIENT];
		int slot = legalMove[State.SLOT];
		
		// dimension of the piece with "orient" orientation
		int[] pBottom = State.getpBottom()[piece][orient];
		int[] pTop = State.getpTop()[piece][orient];
		int pWidth = State.getpWidth()[piece][orient];
		int pHeight = State.getpHeight()[piece][orient];	
		
		int landingHeight = top[slot] - pBottom[0];
		// find 
		for(int i=1; i<pWidth; i++) {
			landingHeight = landingHeight < (top[i+slot]-pBottom[i]) ? 
					(top[i+slot]-pBottom[i]) : landingHeight;
		}
		
		if(landingHeight+pHeight >= State.ROWS) {
			return -1; // it exceeds the height so the move will lose the game
		}
		
		// update field and top
		for(int i=0; i<pWidth; i++) {
			for(int height = landingHeight + pBottom[i]; 
					height < landingHeight + pTop[i]; height++) {
				field[height][i+slot] = 1;
			}
			
			top[i+slot] = landingHeight + pTop[i];
		}
		
		return 1; //the game will not lose with this move
	}
	
	/*
	 * Utility function
	 * */
	
	/*
	 * get the maximum landing height for a tetris to land
	 * This function is called before the field and top are updated according to each move 
	 * */
	private int getLandingHeight(State s, int[] top, int[] legalMove) {
		int piece = s.getNextPiece();
		int orient = legalMove[State.ORIENT];
		int slot = legalMove[State.SLOT];
		
		int pWidth = State.getpWidth()[piece][orient];
		
		int bottom = top[slot];
		// find bottom
		for(int i=1; i<pWidth; i++) {
			bottom = bottom < top[i+slot] ? top[i+slot] : bottom;
		}
		
		return bottom;
	}
	
	/*
	 * Get max height of the field
	 * so that we do not have to loop through all field[][]
	 * */
	private int getMaxHeight(int[] top) {
		int maxHeight = 0;
		for(int col=0; col<State.COLS; col++)
			maxHeight = maxHeight < top[col] ? top[col] : maxHeight;
		
		return maxHeight;
	}
	
	/* 
	 * Get piece height
	 * */
	private int getPieceHeight(int pieceID, int orient) {
		return State.getpHeight()[pieceID][orient];
	}

	
	/*
	 * Feature Heuristics functions
	 * */
	
	private double getHeuristics(int[][] field, int[] top, int maxHeight,
			int pieceHeight, int bottom) {
		
		double sum  = 	weight[LANDING_HEIGHTS_INDEX] * landingHeights(bottom, pieceHeight)
				      + weight[ROWS_TRANSITION_INDEX] * rowTransitions(field, maxHeight)
				      + weight[COLS_TRANSITION_INDEX] * columnTransitions(field, top)
				      + weight[NUM_HOLES_INDEX] * numberOfHoles(field, maxHeight)
				      + weight[WELL_SUMS_INDEX] * wellSum(field, top)
				      + weight[ROWS_COMPLETED_INDEX] * getNumberRowsRemoved(field, top, maxHeight);
		return sum;
	}
	
	/*
	 * Heuristics 1: get the landing height 
	 * 				which is the sum of the maximum landing height for the tetris piece to land on
	 * 				and the average of the pieceHeight
	 * */
	private float landingHeights(int height, int pieceHeight){
		return (float) (height + pieceHeight/2.0);
	}
	
	
	
	
	/*
	 * Heuristics 2: get number of rowTransitions
	 *               loop through every row
	 *               and in each row, count the number of transitions between the filled cell and empty cell
	 *                                and vice versa
	 * */
	
	
	private int rowTransitions(int field[][], int maxHeight) {
		
		int numRowTransitions = 0;

		boolean isPrevCellFilled = true;
		for(int row=0; row<maxHeight; row++) {
			for(int col=0; col<State.COLS-1; col++) {
				boolean isCurrentCellFilled = false;
				if(field[row][col] != 0) {
                    isCurrentCellFilled = true;
                }
				if(isPrevCellFilled != isCurrentCellFilled) {
                    numRowTransitions++;
                }
                isPrevCellFilled = isCurrentCellFilled;
 			}
 			if(!isPrevCellFilled) {
                numRowTransitions++;
            }
            isPrevCellFilled = true;
		}
		
		return numRowTransitions;
	}
	
	/*
	 * Heuristics 3: get number of columnTransition
	 *               loop through every column
	 *               and in each column, count the number of transitions between the filled cell and empty cell
	 *                                and vice versa
	 * */
	
	private int columnTransitions(int field[][], int[] top) {
		
		int numColTransitions = 0;
		boolean isPrevCellFilled = true;	

		for(int col=0; col<State.COLS; col++) {	
			for(int row=0; row<top[col]; row++) {
				boolean isCurrentCellFilled = false;
                if(field[row][col] != 0) {
                    isCurrentCellFilled = true;
                }
                if(isPrevCellFilled != isCurrentCellFilled) {
                    numColTransitions++;
                }
                isPrevCellFilled = isCurrentCellFilled;
				
			}
				
		}
		
		return numColTransitions;
	}
	
	/*
	 * Heuristics 4: get number of holes in the field
	 * 
	 * */
	
	private int numberOfHoles(int[][] field, int maxHeight) {
		
		int numHoles = 0;
		boolean hasHole = false;
		
		for(int col=0; col<State.COLS; col++) {
			hasHole = false;
			for(int row=maxHeight-2; row>=0; row--) {
				if(field[row][col] == 0 && (field[row+1][col] != 0 || hasHole)) {
					numHoles++; // there is a hole
					hasHole = true;
				} else if(field[row][col] != 0)
					hasHole = false;
			}
		}
		
		return numHoles;
	}	
	
	/*
	 * Heuristics 5: get the sum of wells
	 *               A well is an empty cell with both left neighbor cell and right neighbor cell being filled
	 * */
	
	private int wellSum(int field[][], int top[]) {
		
		int wellSum = 0;
		
		for(int col=0; col<State.COLS; col++) {
			int rowStartIndex = 0;
            if(col == 0) {
                rowStartIndex = top[1];
            } else if(col == State.COLS - 1) {
                rowStartIndex = top[State.COLS - 2];
            } else {
                rowStartIndex = top[col - 1];
                if(top[col + 1] < rowStartIndex) {
                    rowStartIndex = top[col + 1];
                }
            }

            rowStartIndex = rowStartIndex - 1;
            for(int row = rowStartIndex; row >= 0; row--) {                                
                boolean isSquareEmpty = true;
                
                if(field[row][col] != 0) {
                    isSquareEmpty = false;
                }
                
                boolean isSquareOnLeftFilled = false;
                boolean isSquareOnRightFilled = false;
                if(col == 0 || field[row][col - 1] != 0) {
                    isSquareOnLeftFilled = true;
                }
                if(col == State.COLS-1 || field[row][col + 1] != 0) {
                    isSquareOnRightFilled = true;
                }
                if(isSquareEmpty && isSquareOnLeftFilled && isSquareOnRightFilled) {                    
                    wellSum++;
                    for(int wellIndex = row - 1; wellIndex >= 0; wellIndex--) {
                        if(field[wellIndex][col] == 0) {
                            wellSum++;
                        } else {
                            break;
                        }
                    }
                }
            }

		}
		
		return wellSum;
	}
	
	/*
	 * Heuristics 6: get number of rows cleared
	 * */
	private int getNumberRowsRemoved (int[][] field, int[] top, int maxHeight) {
		
		int rowsCleared = 0;
		
		//check for complete rows - starting at the top
		for(int row = maxHeight-1; row >= 0; row--) {
			
			boolean full = true;			
			for(int col = 0; col < State.COLS; col++) {
				if(field[row][col] == 0) {
					full = false;
					break;
				}
			}
			
			if(full) {
				rowsCleared++;
			}
		}
		
		return rowsCleared;
	}
	
	
}
