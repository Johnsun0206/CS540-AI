import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

  /**
   * Calls the parent class constructor.
   * 
   * @see Searcher
   * @param maze initial maze.
   */
  public AStarSearcher(Maze maze) {
    super(maze);
  }

  /**
   * Main a-star search algorithm.
   * 
   * @return true if the search finds a solution, false otherwise.
   */
  public boolean search() {

    // explored list is a Boolean array that indicates if a state associated with a given position
    // in the maze has already been explored.
    boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

    PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
    State root = new State(maze.getPlayerSquare(), null, 0, 0);
    frontier.add(new StateFValuePair(root, root.getGValue() + root.h(this.maze)));

    while (!frontier.isEmpty()) {
      
      if (frontier.size() > maxSizeOfFrontier) {
        maxSizeOfFrontier = frontier.size();
      }

      StateFValuePair curr = frontier.poll(); // current value pair
      State cr = curr.getState(); // current state
      
      noOfNodesExpanded++;
      explored[curr.getState().getX()][curr.getState().getY()] = true;
      
      if (cr.getDepth() > maxDepthSearched) {
        maxDepthSearched = cr.getDepth();
      }
      
      cost = curr.getState().getGValue();


      if (cr.isGoal(this.maze)) {
        State s = cr;
        
        while (s!= null) {

          if (maze.getSquareValue(s.getX(), s.getY()) != 'G'
              && maze.getSquareValue(s.getX(), s.getY()) != 'S') {
            maze.setOneSquare(s.getSquare(), '.');
          }

          this.maze = getModifiedMaze();
          s = s.getParent();

        }
        return true;

      } else {
        for (State st : curr.getState().getSuccessors(explored, this.maze)) {

          StateFValuePair node = new StateFValuePair(st, st.getGValue() + st.h(this.maze));        

          StateFValuePair n = null;
            for (StateFValuePair rm : frontier) {
              if (rm.getState().equals(node.getState())) {
                n = rm;
                break;
              }
            }
            
            if(n == null) {
              frontier.add(node);
            } else {
              if(st.getGValue() < n.getState().getGValue()) {  
                frontier.remove(n);
                frontier.add(node);
              }
            }
 
        }

      }

    }

    return false; // return false if no solution
  }

}
