
public class PlayerSkeleton {
	
	// get from the learning agent
	private double[] weight = {0.0, 0.0, 0.0, 0.0, 0.0};
	
	private static final int DC_INDEX = 0;
	private static final int COL_HEIGHT_WEIGHT_INDEX = 1;
	private static final int ABSOLUTE_DIFF_COL_HEIGHTS_WEIGHT_INDEX = 2;
	private static final int MAXIMUM_COL_HEIGHT_WEIGHT_INDEX = 3;
	private static final int NUM_HOLES_WEIGHT_INDEX = 4;
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			int move = p.pickMove(s,s.legalMoves());
			System.out.println("move " + move);
			//s.makeMove(p.pickMove(s,s.legalMoves()));
			s.makeMove(move);
			
			
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		int move = 0;
		double max = 0;
		
		for(int i=0; i<legalMoves.length; i++) {
			
			//change top and field according to the move
			int[][] field = s.getField().clone();
			int[] top = s.getTop().clone();
			
			int maxHeight = getFieldAndTop(s, legalMoves[i], field, top);
			
			int numRowsRemoved = getNumberRowsRemoved(field, maxHeight);
			double heuristics = getHeuristics(field, top, numRowsRemoved, maxHeight);
			
			double cost = numRowsRemoved + heuristics; 
					
			if(max < cost) {
				max = cost;
				move = i;
			}
		}
		
		
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
		
		int maxHeight = 0;
		
		int bottom = 0;
		// find bottom
		for(int i=0; i<pWidth; i++) {
			int pSlot = i + slot;
			
		}
		
		// change field and top
		maxHeight = bottom+pTop[0]-1; 
				
		for(int i=0; i<pWidth; i++) {
			for(int height=bottom+pBottom[i]; 
					height<bottom+pTop[i]; height++) {
				field[i+slot][height] = 1;
			}
			
			top[i+slot] = bottom+pTop[i]-1;
			maxHeight = maxHeight < top[i+slot] ? top[i+slot] : maxHeight;
		}
		
		
		return maxHeight;
	}

	private int getNumberRowsRemoved (int[][] field, int maxHeight) {
		
		int rowsCleared = 0;
		
		//check for complete rows - starting at the top
		for(int row = maxHeight; 
				row >= 0; row--) {
			
			boolean full = true;
			
			for(int col = 0; col < State.COLS; col++) {
				if(field[row][col] == 0) {
					full = false;
					break;
				}
			}
			
			if(full)
				rowsCleared++;
		}
		
		return rowsCleared;
	}
	
	private double getHeuristics(int[][] field, int[] top, int numRowsRemoved, int maxHeight) {
		
		// compute the sum
		double sum = weight[DC_INDEX] 
				+ weight[COL_HEIGHT_WEIGHT_INDEX] * sumOfColumnHeight(top, numRowsRemoved) 
				+ weight[ABSOLUTE_DIFF_COL_HEIGHTS_WEIGHT_INDEX] * sumOfAbsoluteDiffAdjacentColumnHeights(top)
				+ weight[MAXIMUM_COL_HEIGHT_WEIGHT_INDEX] * maximumColumnHeight(top, numRowsRemoved) 
				+ weight[NUM_HOLES_WEIGHT_INDEX] * numberOfHoles(field, maxHeight);
		
		return sum;
	}
	
	
	private int sumOfColumnHeight(int[] top, int numRowsRemoved) {
		int sum = 0;
		
		for(int i=0; i<State.COLS; i++)
			sum += (top[i]-numRowsRemoved);
		
		return sum;
	}
	
	private int sumOfAbsoluteDiffAdjacentColumnHeights (int[] top) {
		
		int sumDiff = 0;
		
		for(int i=0; i<State.COLS-1; i++)
			sumDiff += Math.abs(top[i]- top[i+1]);
		
		return sumDiff;
	}
	
	private int maximumColumnHeight(int[] top, int numRowsRemoved) {
		
		int maxHeight = 0;
		
		for(int i=0; i<State.COLS; i++) {
			maxHeight = maxHeight < top[i] ? top[i] : maxHeight;
		}
		
		return (maxHeight-numRowsRemoved);
	}
	
	private int numberOfHoles(int[][] field, int maxHeight) {
		
		for(int height=0; height<=maxHeight; height++) {
			
		}
		return 0;
	}
	
	
	
	
}
