import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class Solver {

	int[][] path;
	int[] solverLocation = {1, 1};
	int[] goalLocation = new int[2];
	int[][] origMaze;
	private MazeDisplay displayer; 


	public Solver(Maze maze, MazeDisplay _displayer) {
		origMaze = copyMaze(maze.getArrayRepresentation());
		path = copyMaze(maze.getArrayRepresentation());
		goalLocation[0] = (maze.getLength() - 2);
		goalLocation[1] = (maze.getLength() - 2);
		displayer = _displayer;
		fixMaze();
	}
	
	public void depthFirstSolve() {
		int numTimes = 0;
		Stack<int[]> visitedCells = new Stack<>();
		ArrayList<int[]> unvisitedCells = determineUnvisitedCells();

		visitedCells.push(new int[] {1,1});
		Stack<int[]> cellsToWipe = new Stack<>();
		while(!((solverLocation[0] == path.length - 3 && solverLocation[1] == path.length - 2) || (solverLocation[0] == path.length - 2 && solverLocation[1] == path.length - 3))) {
			int direction;
			int[] operatingCell;
			
			ArrayList<Integer> badDir = new ArrayList<>();
			if(solverLocation[0] - 1 <= 0 || path[solverLocation[0] - 1][solverLocation[1]] == 999) {
				badDir.add(0);
			}
			if(solverLocation[1] + 1 >= path.length || path[solverLocation[0]][solverLocation[1] + 1] == 999) {
				badDir.add(1);
			}
			if(solverLocation[0] + 1 >= path.length || path[solverLocation[0] + 1][solverLocation[1]] == 999) {
				badDir.add(2);
			}
			if(solverLocation[1] - 1 <= 0 || path[solverLocation[0]][solverLocation[1] - 1] == 999) {
				badDir.add(3);
			}
			ArrayList<Integer> directions = unvisitedNeighbors(solverLocation, unvisitedCells, badDir);		
			
			if(directions == null) {
				if(visitedCells.isEmpty()) {
					System.out.println("\nRan out of cells to go to, MAZE SOLVING FAILED :(");
					break;
				}
				operatingCell = visitedCells.pop();
				cellsToWipe.push(operatingCell);
			} else  {
				direction = directions.get((int)(directions.size() * Math.random()));
				//up
				operatingCell = new int[2];
				if(direction == 0) {
					operatingCell[0] = solverLocation[0] - 1;
					operatingCell[1] = solverLocation[1];
				}
				//east
				else if(direction == 1) {
					operatingCell[0] = solverLocation[0];
					operatingCell[1] = solverLocation[1] + 1;
				}
				//south
				else if(direction == 2) {
					operatingCell[0] = solverLocation[0] + 1;
					operatingCell[1] = solverLocation[1];
				}
				//west
				else if(direction == 3) {
					operatingCell[0] = solverLocation[0];
					operatingCell[1] = solverLocation[1] - 1;
					
				}
				path[operatingCell[0]][operatingCell[1]] = 1000;
				
				visitedCells.push(operatingCell);
			}
			if(directions == null) {
				path[solverLocation[0]][solverLocation[1]] = 0;

			} else {
				while(!cellsToWipe.isEmpty()) {
					int[] cell = cellsToWipe.pop();
					path[cell[0]][cell[1]] = 0;
				}
				path[solverLocation[0]][solverLocation[1]] = 3;
			}
			numTimes++;
			solverLocation = operatingCell;
			
			for(int i = 0; i < unvisitedCells.size(); i++) {
				if(arrayEqual(solverLocation, unvisitedCells.get(i))) {
					unvisitedCells.remove(i);
				}
			}
			
			
			if(arrayEqual(solverLocation, goalLocation)) {
				System.out.println("found solution!");
				break;
			}
			
			int[][] copyMaze = new int[path.length][path.length];
			for(int r = 0; r < path.length; r++) {
				for(int c = 0; c < path[r].length; c++) {
					copyMaze[r][c] += path[r][c];
				}
			}
			display(copyMaze);
		}
		System.out.println("Maze solving took " + numTimes + " loops");
	}
	
	
	public ArrayList<Integer> unvisitedNeighbors(int[] cell, ArrayList<int[]> unVisitedCells, ArrayList<Integer> badDir) {
		ArrayList<Integer> poss = new ArrayList<>();
		int[] northArr = {cell[0] - 1, cell[1]};
		int[] eastArr = {cell[0], cell[1] + 1};
		int[] southArr = {cell[0] + 1, cell[1]};
		int[] westArr = {cell[0], cell[1] - 1};

		for(int i = 0; i < unVisitedCells.size(); i++) {
			if(arrayEqual(unVisitedCells.get(i), northArr) && !(badDir.contains(0))) {
				poss.add(0);
			}
			if(arrayEqual(unVisitedCells.get(i), eastArr) && !(badDir.contains(1))) {
				poss.add(1);
			}
			if(arrayEqual(unVisitedCells.get(i), southArr) && !(badDir.contains(2))) {
				poss.add(2);
			}
			if(arrayEqual(unVisitedCells.get(i), westArr) && !(badDir.contains(3))) {
				poss.add(3);
			}
		}
		if(poss.isEmpty()) {
			return null;
		} else {
			return poss;
		}
	}
	
	public ArrayList<int[]> determineUnvisitedCells() {
		ArrayList<int[]> ret = new ArrayList<>();
		for(int r = 0; r < path.length; r++) {
			for(int c = 0; c < path.length; c++) {
				if(path[r][c] == 0) {
					ret.add(new int[] {r, c});
				}
			}
		}
		return ret;
	}
		
	public void display(int[][] mazeIntance) {
		displayer.addNewSolverDisplay(mazeIntance);
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

	public int[][] copyMaze(int[][] toCopy) {
		int[][] resp = new int[toCopy.length][toCopy.length];
		for(int r = 0; r < toCopy.length; r++) {
			for(int c = 0; c < toCopy[r].length; c++) {
				resp[r][c] = toCopy[r][c];
			}
		}
		return resp;
	}
	
	public void reset() {
		solverLocation[0] = 1;
		solverLocation[1] = 1;
		goalLocation[0] = (path.length - 2);
		goalLocation[1] = (path.length - 2);
		path = copyMaze(origMaze);
		fixMaze();
	}
	
	public void fixMaze() {
		for(int r = 0; r < path.length; r++) {
			for(int c = 0; c < path.length; c++) {
				if(path[r][c] == 1) {
					path[r][c] = 999;
				}
			}
		}
	}
}
