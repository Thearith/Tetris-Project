import java.util.Arrays;


public class PlayerSkeleton {
	
	//private double[] weight = {0.0, -0.66569, -0.24077, -1/21.0, -0.46544};
	private State s = null;
	private TFrame f = null;
	private double[] weight = new double[NUM_WEIGHTS];
	
	private static final int NUM_WEIGHTS = 6;
	
	private static final int DC_INDEX = 0;
	private static final int COL_HEIGHT_WEIGHT_INDEX = 1;
	private static final int ABSOLUTE_DIFF_COL_HEIGHTS_WEIGHT_INDEX = 2;
	private static final int MAXIMUM_COL_HEIGHT_WEIGHT_INDEX = 3;
	private static final int NUM_HOLES_WEIGHT_INDEX = 4;
	private static final int COMPLETE_ROWS_INDEX = 5;
	
	static int index = 0;
	
//	public PlayerSkeleton(double[] param) {
//		weight = param;
//	}		
	
	public PlayerSkeleton(double[] param) {
		s = new State();
		f = new TFrame(s);
		weight = param;
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
			
			s.draw();
			s.drawNext(0,0);
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (s.getRowsCleared() > 500) {
			System.out.println("You have completed "+s.getRowsCleared()+" rows.");
			System.out.printf("%f, %f, %f, %f, %f, %f\n", weight[0], weight[1], weight[2], weight[3], weight[4], weight[5]);
		}
		f.closeFrame();
	}
	
	public int getRowsCleared(){
		return s.getRowsCleared();
	}
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		int move = -1;
		double max = -Double.MAX_VALUE;
		
		for(int i=0; i<legalMoves.length; i++) {
			
			//change top and field according to the move
			int[][] sField = s.getField();
			int[][] field = new int[sField.length][];
			
			
			for(int j=0; j<sField.length; j++) {
				int length = sField[j].length;
				field[j] = new int[length];
				System.arraycopy(sField[j], 0, field[j], 0, length);
			}
			
			int[] top = Arrays.copyOf(s.getTop(), s.getTop().length);
			
			int maxHeight = getFieldAndTop(s, legalMoves[i], field, top);
			
			if(maxHeight < 0)
				continue; //this move will lose the game
			
			int numRowsRemoved = getNumberRowsRemoved(field, top, maxHeight);
			double heuristics = getHeuristics(field, top, numRowsRemoved, maxHeight);
			//System.out.println("my move nw is " + thisMove + "utility is " + heuristics);
			double cost = weight[COMPLETE_ROWS_INDEX] * numRowsRemoved + heuristics; 
			//System.out.println("numRows" + numRowsRemoved + " heuristics " + heuristics);
					
			if(max < cost) {
				max = cost;
				move = i;
			}
		}
		
		//System.out.println("move made is " + move);
		return move;

	}
	
	// change field and top according to move
	
	private int getFieldAndTop(State s, int legalMove[], int[][] field
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
		
		int bottom = top[slot] - pBottom[0];
		// find bottom
		for(int i=1; i<pWidth; i++) {
			bottom = bottom < (top[i+slot]-pBottom[i]) ? (top[i+slot]-pBottom[i]) : bottom;
		}
		
		if(bottom+pHeight >= State.ROWS) {
			return -1;
		}
		
		// change field and top
		
		for(int i=0; i<pWidth; i++) {
			for(int height=bottom+pBottom[i]; 
					height<bottom+pTop[i]; height++) {
				field[height][i+slot] = 1;
			}
			
			top[i+slot] = bottom+pTop[i];
		}
		
		return bottom+pHeight;
	}

	private int getNumberRowsRemoved (int[][] field, int[] top, int maxHeight) {
		
		int rowsCleared = 0;
		
		//check for complete rows - starting at the top
		for(int row = maxHeight-1; 
				row >= 0; row--) {
			
			boolean full = true;
			
			for(int col = 0; col < State.COLS; col++) {
				if(field[row][col] == 0) {
					full = false;
					break;
				}
			}
			
			if(full) {
				rowsCleared++;
				
				for(int col = 0; col < State.COLS; col++) {

					//slide down all bricks
					for(int i = row; i < top[col]; i++) {
						field[i][col] = field[i+1][col];
					}
					//lower the top
					top[col]--;
					while(top[col]>=1 && field[top[col]-1][col]==0)	
						top[col]--;
				}
				
			}
		}
		
		return rowsCleared;
	}
	
	private double getHeuristics(int[][] field, int[] top, int numRowsRemoved, int maxHeight) {
		
		// compute the sum
		double sum = weight[DC_INDEX] 
				+ weight[COL_HEIGHT_WEIGHT_INDEX] * sumOfColumnHeight(top) 
				+ weight[ABSOLUTE_DIFF_COL_HEIGHTS_WEIGHT_INDEX] * sumOfAbsoluteDiffAdjacentColumnHeights(top)
				+ weight[MAXIMUM_COL_HEIGHT_WEIGHT_INDEX] * maximumColumnHeight(maxHeight, numRowsRemoved) 
				+ weight[NUM_HOLES_WEIGHT_INDEX] * numberOfHoles(field, maxHeight)
				+ weight[COMPLETE_ROWS_INDEX] * getNumberRowsRemoved(field, top, maxHeight);
		
		return sum;
	}
	
	
	private int sumOfColumnHeight(int[] top) {
		int sum = 0;
		
		for(int i=0; i<State.COLS; i++)
			sum += top[i];
		
		return sum;
	}
	
	private int sumOfAbsoluteDiffAdjacentColumnHeights (int[] top) {
		
		int sumDiff = 0;
		
		for(int i=0; i<State.COLS-1; i++)
			sumDiff += Math.abs(top[i]- top[i+1]);
		
		return sumDiff;
	}
	
	private int maximumColumnHeight(int maxHeight, int numRowsRemoved) {
		
		return (maxHeight-numRowsRemoved);
	}
	
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
}
