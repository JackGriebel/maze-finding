import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Maze {

	private int[][] maze;
	private MazeDisplay displayer; 
	public Maze(int size, int speed) {
		displayer = new MazeDisplay(1000 - 1000 % size, 1000 - 1000 % size + 30, speed);
		maze = new int[size][size];
		populateMaze();
	}



	public void makeBacktrackWalls() {
		if(maze.length % 2 == 0) {
			throw new IllegalArgumentException("Must have an odd sized array!");
		}
		makeCells();
		ArrayList<int[]> toVisit = generateToVisitArray();

		int numTimes = 0;
		Stack<int[]> visitedCells = new Stack<>();
		Stack<int[]> cellsToWipe = new Stack<>();

		visitedCells.push(toVisit.get(0));

		while(!toVisit.isEmpty()) {
			int direction;
			int[] currentCell;
			int[] lastVisited = visitedCells.peek();
			
			ArrayList<Integer> badDir = new ArrayList<>();
			if(lastVisited[0] - 2 <= 0) {
				badDir.add(0);
			}
			if(lastVisited[1] + 2 >= maze.length) {
				badDir.add(1);
			}
			if(lastVisited[0] + 2 >= maze.length) {
				badDir.add(2);
			}
			if(lastVisited[1] - 2 <= 0) {
				badDir.add(3);
			}
			
			ArrayList<Integer> directions = unvisitedNeighbors(lastVisited, toVisit, badDir);
			if(directions == null) {
				currentCell = visitedCells.pop();
			} else  {
				
				direction = directions.get((int)(directions.size() * Math.random()));
				//up
				currentCell = new int[2];
				if(direction == 0) {
					currentCell[0] = lastVisited[0] - 2;
					currentCell[1] = lastVisited[1];
					maze[lastVisited[0] - 1][lastVisited[1]] = 0;
				}
				//east
				else if(direction == 1) {
					currentCell[0] = lastVisited[0];
					currentCell[1] = lastVisited[1] + 2;
					maze[lastVisited[0]][lastVisited[1] + 1] = 0;
				}
				//south
				else if(direction == 2) {
					currentCell[0] = lastVisited[0] + 2;
					currentCell[1] = lastVisited[1];
					maze[lastVisited[0] + 1][lastVisited[1]] = 0;
				}
				//west
				else if(direction == 3) {
					currentCell[0] = lastVisited[0];
					currentCell[1] = lastVisited[1] - 2;
					maze[lastVisited[0]][lastVisited[1] - 1] = 0;
				}
				visitedCells.push(currentCell);
			}
			numTimes++;
			if(numTimes > 50000) {
				throw new IllegalArgumentException("Maze creator ran for too long... infinite loop?");
			}


			
			if(arrayEqual(lastVisited, currentCell)) {
				maze[lastVisited[0]][lastVisited[1]] = 3;
				cellsToWipe.push(lastVisited);
			} else {
				maze[lastVisited[0]][lastVisited[1]] = 0;
				maze[currentCell[0]][currentCell[1]] = 2;
				while(!cellsToWipe.isEmpty()) {
					int[] cell = cellsToWipe.pop(); 
					maze[cell[0]][cell[1]] = 0;
				}
			}
			lastVisited = currentCell;
			for(int i = 0; i < toVisit.size(); i++) {
				if(toVisit.get(i)[0] == lastVisited[0] && toVisit.get(i)[1] == lastVisited[1]) {
					toVisit.remove(i);
//					System.out.println("visited new cell. num left: " + toVisit.size());
				}
			}
			toVisit.remove(currentCell);
			if(toVisit.size() == 0) {
				maze[1][1] = 4;
				maze[maze.length - 2][maze.length - 2] = 5;
				maze[currentCell[0]][currentCell[1]] = 0;
			}
			int[][] copyMaze = new int[maze.length][maze.length];
			for(int r = 0; r < maze.length; r++) {
				for(int c = 0; c < maze[r].length; c++) {
					copyMaze[r][c] += maze[r][c];
				}
			}
			
			display(copyMaze);
			
		}
		System.out.println("Maze generation took " + numTimes + " loops");
		
	}

	public ArrayList<Integer> unvisitedNeighbors(int[] cell, ArrayList<int[]> toVisit, ArrayList<Integer> badDir) {
		ArrayList<Integer> poss = new ArrayList<>();
		int[] northArr = {cell[0] - 2, cell[1]};
		int[] eastArr = {cell[0], cell[1] + 2};
		int[] southArr = {cell[0] + 2, cell[1]};
		int[] westArr = {cell[0], cell[1] - 2};

		for(int i = 0; i < toVisit.size(); i++) {
			if(arrayEqual(toVisit.get(i), northArr) && !(badDir.contains(0))) {
				poss.add(0);
			}
			if(arrayEqual(toVisit.get(i), eastArr) && !(badDir.contains(1))) {
				poss.add(1);
			}
			if(arrayEqual(toVisit.get(i), southArr) && !(badDir.contains(2))) {
				poss.add(2);
			}
			if(arrayEqual(toVisit.get(i), westArr) && !(badDir.contains(3))) {
				poss.add(3);
			}
		}
		if(poss.isEmpty()) {
			return null;
		} else {
			return poss;
		}
	}

	public boolean arrayEqual(int[] arr1, int[] arr2) {
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i = 0; i < arr1.length; i++) {
			if(arr1[i] != arr2[i]) {
				return false;
			}
		}

		return true;
	}

	private ArrayList<int[]> generateToVisitArray() {
		ArrayList<int[]> toVisit = new ArrayList<>();
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze[r].length; c++) {
				if(maze[r][c] == 0) {
					toVisit.add(new int[] {r,c});
				}
			}
		}
		return toVisit;
	}

	private void makeCells() {
		for(int r = 0; r < maze.length; r+=2) {
			for(int i = 0; i < maze[r].length; i++) {
				maze[r][i] = 1;
			}
		}
		for(int c = 0; c < maze[0].length; c+=2) {
			for(int i = 0; i < maze.length; i++) {
				maze[i][c] = 1;
			}
		}
	}

	public void display(int[][] mazeIntance) {
		displayer.addNewCreatorDisplay(mazeIntance);
	}

	
	public void display() {
		displayer.addNewCreatorDisplay(maze);
	}

	@Override
	public String toString() {
		String resp = "";
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze[r].length; c++) {
				resp += maze[r][c] + " ";
			}
			resp += "\n";
		}
		return resp;
	}
	//fills maze with 0s
	public void populateMaze() {
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze[r].length; c++) {
				maze[r][c] = 0;
			}
		}
	}
	public int getLength() {
		return maze.length;
	}
	public int[][] getArrayRepresentation() {
		return maze;
	}
	public int getStatus(int r, int c) {
		return maze[r][c];
	}
	public MazeDisplay getDisplayer() {
		return displayer;
	}
}
