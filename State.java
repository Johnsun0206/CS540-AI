import java.util.ArrayList;

/**
 * A state in the search represented by the (x,y) coordinates of the square and the parent. In other
 * words a (square,parent) pair where square is a Square, parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

  private Square square;
  private State parent;

  // Maintain the gValue (the distance from start)
  // You may not need it for the BFS but you will
  // definitely need it for AStar
  private int gValue;

  // States are nodes in the search tree, therefore each has a depth.
  private int depth;

  /**
   * @param square current square
   * @param parent parent state
   * @param gValue total distance from start
   */
  public State(Square square, State parent, int gValue, int depth) {
    this.square = square;
    this.parent = parent;
    this.gValue = gValue;
    this.depth = depth;
  }

  /**
   * @param visited explored[i][j] is true if (i,j) is already explored
   * @param maze initial maze to get find the neighbor
   * @return all the successors of the current state
   */
  public ArrayList<State> getSuccessors(boolean[][] explored, Maze maze) {


    ArrayList<State> result = new ArrayList<>();

    if (maze.getSquareValue(getX(), getY()-1) != '%' && !explored[getX()][getY()-1]) {
      
      Square left = new Square(getX(), getY()-1);
      result.add(new State(left, this, getGValue() + 1, getDepth() + 1));
      
    }
    if (maze.getSquareValue(getX()+1, getY()) != '%' && !explored[getX()+1][getY()]) {
      
      Square down = new Square(getX()+1, getY());   
      result.add(new State(down, this, getGValue() + 1, getDepth() + 1));

    }
    if (maze.getSquareValue(getX(), getY()+1) != '%'&& !explored[getX()][getY()+1]) {
      
      Square right = new Square(getX(), getY()+1);
      result.add(new State(right, this, getGValue() + 1, getDepth() + 1));

      
    }
    if (maze.getSquareValue(getX()-1, getY()) != '%' && !explored[getX()-1][getY()]) {
      
      Square up = new Square(getX()-1, getY());
      result.add(new State(up, this, getGValue() + 1, getDepth() + 1));

      
    }

    return result;



  }

  /**
   * @return x coordinate of the current state
   */
  public int getX() {
    return square.X;
  }

  /**
   * @return y coordinate of the current state
   */
  public int getY() {
    return square.Y;
  }

  /**
   * @param maze initial maze
   * @return true is the current state is a goal state
   */
  public boolean isGoal(Maze maze) {
    if (square.X == maze.getGoalSquare().X && square.Y == maze.getGoalSquare().Y)
      return true;

    return false;
  }

  /**
   * @return the current state's square representation
   */
  public Square getSquare() {
    return square;
  }

  /**
   * @return parent of the current state
   */
  public State getParent() {
    return parent;
  }

  /**
   * You may not need g() value in the BFS but you will need it in A-star search.
   * 
   * @return g() value of the current state
   */
  public int getGValue() {
    return gValue;
  }

  /**
   * @return depth of the state (node)
   */
  public int getDepth() {
    return depth;
  }
  
  /**
   * heuristic function with respect to the current state
   * 
   * @param maze 
   * @return the h() of the current state
   */
  public double h(Maze maze) {
    
    double Xdistance = Math.pow((getX()-maze.getGoalSquare().X), 2);
    double Ydistance = Math.pow((getY()-maze.getGoalSquare().Y), 2);
    double Euclidean = Math.sqrt(Xdistance + Ydistance);
    
    return Euclidean;
  }
  
  @Override
  public boolean equals(Object e) {
    
    if(e == null) return false;
  
    return ((State)e).square.X == this.square.X && ((State)e).square.Y == this.square.Y ;
 
    
  }
  
}
