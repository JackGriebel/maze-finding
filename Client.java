
public class Client {
	static MazeDisplay displayer;
	public static void main(String[] args) {
		//press s to solve, f to fast forward
		//first argument is the size of the maze, second is the fps for the displayer (set to -1 for max)
		//Maze sizes seems to max out at about ~150; after that memory issues and other errors occur
		Maze maze = new Maze(149, -1);
		System.out.println("\n\nCreating Maze...\n");
		maze.makeBacktrackWalls();
		Solver solver = new Solver(maze, maze.getDisplayer());
		System.out.println("\n\nMaze generation done, now solving...\n");
		solver.depthFirstSolve();
		System.out.println("\n\nSolving done, sending to displayer...\n");
		maze.getDisplayer().run();
	}
	
}
