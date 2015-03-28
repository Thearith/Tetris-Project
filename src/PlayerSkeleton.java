
public class PlayerSkeleton {
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
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
		int max = 0;
		
		for(int i=0; i<legalMoves.length; i++) {
			int cost = findNumberRowsRemoved(s, legalMoves[i]);
			if(max < cost) {
				max = cost;
				move = i;
			}
		}
		
		
		return move;
	}
	
	private int findNumberRowsRemoved (State s, int[] legalMove) {
		
		int rowsCleared = 0;
		
		// info about the building
		int[] top = s.getTop();
		int[][] field = s.getField().clone();
		
		// info about this block piece
		int piece = s.getNextPiece();
		int slot = legalMove[State.SLOT];
		int orient = legalMove[State.ORIENT];
		
		int[] pBottom = State.getpBottom()[piece][orient];
		int[] pTop = State.getpTop()[piece][orient];
		
		int pWidth = State.getpWidth()[piece][orient];
		int pHeight = State.getpHeight()[piece][orient];
		
		
		// find the maximum height of the building when adding the piece block
		int height = top[slot] - pBottom[0];	
		for(int col = 1; col < pWidth; col++) {
			height = Math.max(height,
					top[slot+col] - pBottom[col]);
		}
		
		//for each column in the piece
		for(int i = 0; i < pWidth; i++) {
			
			//from bottom to top of brick
			for(int h = height + pBottom[i]; 
					h < height + pTop[i]; h++) {
				System.out.println("h = " + h);
				System.out.println("i+slot = " + (i+slot));
				field[h][i+slot] = 1;
			}
		}
		
		
		//check for complete rows - starting at the top
		for(int row = height + pHeight - 1; 
				row >= height; row--) {
			
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
	
	
}
