import javax.swing.*; 
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MazeDisplay extends JFrame implements KeyListener {

	private final int MAX_FPS;
	private final int WIDTH;
	private final int HEIGHT;
	private BufferStrategy strategy;
	Graphics2D g;
	private Queue<int[][]> creatorFramesToDraw = new LinkedList<>();
	private Queue<int[][]> solverFramesToDraw = new LinkedList<>();
	private boolean doneDrawing = false;
	private boolean readyToSolve = false;
	boolean trueSpeed = false;


	public MazeDisplay(int width, int height, int fps){
		super("aMAZEing Jframe");
		this.MAX_FPS = fps;
		this.WIDTH = width;
		this.HEIGHT = height;
		if(fps == -1) {
			trueSpeed = true;
		}
		addKeyListener(this);
	}

	void init(){
		setBounds(0, 0, WIDTH, HEIGHT);
		setResizable(false);

		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createBufferStrategy(2);
		strategy = getBufferStrategy();
		g = (Graphics2D) strategy.getDrawGraphics();
		setBackground(new Color(0, 0, 100));

	}

	public void run(){

		init();
		System.out.println("Have " + creatorFramesToDraw.size() + " creator frames to draw");
		System.out.println("Have " + solverFramesToDraw.size() + " solver frames to draw\n");
		while(!doneDrawing){

			draw();

			if(!trueSpeed) {
				try{ Thread.sleep(1000/MAX_FPS); }
				catch (InterruptedException e){ e.printStackTrace(); }
			}

		}

	}

	private void draw(){
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		int[][] creatorDraw = creatorFramesToDraw.poll();
		if(creatorDraw != null && !readyToSolve) {
			creatorDisplayMaze(creatorDraw);
		} else if(readyToSolve) { 
			if(solverFramesToDraw.size() != 1) {
				solverDisplayMaze(solverFramesToDraw.poll());
			} else if(solverFramesToDraw.size() == 1) {
				displayFinalSolution(solverFramesToDraw.poll());
				doneDrawing = true;
			}
		}
		g.dispose();
		strategy.show();
	}

	void creatorDisplayMaze(int[][] maze) {
		int gridSize = WIDTH / maze.length;
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze.length; c++) {
				int mazeStatus = maze[r][c];
				if(mazeStatus == 0) {
					//space
					g.setColor(Color.WHITE);
				} else if(mazeStatus == 1) {
					//wall
					g.setColor(new Color(0, 0, 100));
				} else if(mazeStatus == 2) {
					//tracker
					g.setColor(new Color(255, 127, 0));
				}  else if(mazeStatus == 3) {
					//backtracker
					g.setColor(new Color(72, 255, 0));
				}   else if(mazeStatus == 4) {
					//start
					g.setColor(Color.DARK_GRAY);
				}   else if(mazeStatus == 5) {
					//finish
					g.setColor(Color.RED);
				} else if(mazeStatus == 6) {
					g.setColor(Color.PINK);
				}
				g.fillRect(c * gridSize, r * gridSize + 30, gridSize, gridSize);
			}
		}
	}

	private void solverDisplayMaze(int[][] maze) {
		int gridSize = WIDTH / maze.length;
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze.length; c++) {
				int mazeStatus = maze[r][c];
				if(mazeStatus == 999) {
					//wall
					g.setColor(new Color(0, 0, 100));
				} else if(mazeStatus == 0) {
					//space
					g.setColor(Color.WHITE);
				} else if(mazeStatus >= 1000) {
					// tracker
					g.setColor(Color.RED);
				} else {
					//path that gets darker as it is stepped on more
					int rVal = (int)(0 * (1.00 - 0.05 * mazeStatus));
					int gVal = (int)(255 * (1.00 - 0.05 * mazeStatus));
					int bVal = (int)(0 * (1.00 - 0.05 * mazeStatus));
					if(rVal < 0) {
						rVal = 0;
					}
					if(gVal < 0) {
						gVal = 0;
					}
					if(bVal < 0) {
						bVal = 0;
					}
					g.setColor(new Color(rVal, gVal, bVal));
				}

				g.fillRect(c * gridSize, r * gridSize + 30, gridSize, gridSize);
			}
		}

	}


	private int[][] processFinalSolution(int[][] maze) {
		for(int r = 0; r < maze.length; r++) {
			for(int c = 0; c < maze.length; c++) {
				if(maze[r][c] == 5 || maze[r][c] == 1000) {
					maze[r][c] = 3;
				} else if(maze[r][c] == 999) {
					maze[r][c] = 1;
				}
			}
		}
		maze[1][1] = 4;
		maze[maze.length - 2][maze.length - 2] = 4;
		for(int i = 0; i < maze.length; i++) {
			for(int k = 0; k < maze.length; k++) {
				System.out.print(maze[i][k] + " ");
			}
			System.out.println();
		}
		return maze;
	}

	private void displayFinalSolution(int[][] maze) {
		maze = processFinalSolution(maze);
		int gridSize = WIDTH / maze.length;
		for(int r = maze.length - 1; r > 0; r--) {
			for(int c = maze.length - 1; c > 0; c--) {
				int mazeStatus = maze[r][c];
				if(mazeStatus == 1) {
					//wall
					g.setColor(new Color(0, 0, 100));
				} else if(mazeStatus == 0) {
					//space
					g.setColor(Color.WHITE);
				} else if(mazeStatus == 3) {
					int numNeighbors = 0;
					if(maze[r+1][c] == 3 || maze[r+1][c] == 4) {
						numNeighbors++;
					}
					if(maze[r-1][c] == 3 || maze[r-1][c] == 4) {
						numNeighbors++;
					}
					if(maze[r][c+1] == 3 || maze[r][c+1] == 4) {
						numNeighbors++;
					}
					if(maze[r][c-1] == 3 || maze[r][c-1] == 4) {
						numNeighbors++;
					}
					if(numNeighbors >= 2) {
						g.setColor(Color.MAGENTA);
					} else {
						g.setColor(Color.WHITE);
					}

				} else if(mazeStatus == 4){
					g.setColor(Color.MAGENTA);
				}
				g.fillRect(c * gridSize, r * gridSize + 30, gridSize, gridSize);
				g.setColor(Color.BLACK);
				//				g.drawRect(c * gridSize, r * gridSize + 30, gridSize, gridSize);
			}
		}

	}



	public void addNewCreatorDisplay(int[][] mazeInstance) {
		creatorFramesToDraw.add(mazeInstance);
	}

	public void addNewSolverDisplay(int[][] mazeInstance) {
		solverFramesToDraw.add(mazeInstance);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_F) {
			if(creatorFramesToDraw.poll() != null) {
				if(creatorFramesToDraw.size() > 100) {
					while(creatorFramesToDraw.size() > 100) {
						creatorFramesToDraw.poll();
					}
				} else {
					System.out.println("Too close to end of creation step to fast forward!");
				}
			} else {
				if(solverFramesToDraw.size() > 100) {
					while(solverFramesToDraw.size() > 100) {
						solverFramesToDraw.poll();
					}
				} else {
					System.out.println("Too close to end of solving step to fast forward!");
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			if(creatorFramesToDraw.size() == 0) {
				readyToSolve = true;
			} else {
				System.out.println("Wait till the maze is being done created to solve it!");
			}
		}
	}

}