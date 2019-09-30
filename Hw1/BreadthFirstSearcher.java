import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

  /**
   * Calls the parent class constructor.
   * 
   * @see Searcher
   * @param maze initial maze.
   */
  public BreadthFirstSearcher(Maze maze) {
    super(maze);
  }

  /**
   * Main breadth first search algorithm.
   * 
   * @return true if the search finds a solution, false otherwise.
   */
  public boolean search() {

    // explored list is a 2D Boolean array that indicates if a state associated with a given
    // position in the maze has already been explored.
    boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

    // Queue implementing the Frontier list
    LinkedList<State> queue = new LinkedList<State>();
    queue.add(new State(maze.getPlayerSquare(), null, 0, 0));
    maxSizeOfFrontier++;

    while (!queue.isEmpty()) {

      if (queue.size() > this.maxSizeOfFrontier) {
        this.maxSizeOfFrontier = queue.size();
      }

      // return true if find a solution
      // maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
      // maxDepthSearched, maxSizeOfFrontier during
      // the search
      // update the maze if a solution found
      // use queue.add(...) to add elements to queue

      State curr = queue.poll();
      explored[curr.getX()][curr.getY()] = true;
      noOfNodesExpanded++; // number of nodes removed from queue
      if (curr.getDepth() > maxDepthSearched) {
        maxDepthSearched = curr.getDepth();
      }


      if (curr.isGoal(maze)) {

        State tr = curr;
        while (tr != null) {
          if (maze.getSquareValue(tr.getX(), tr.getY()) != 'G'
              && maze.getSquareValue(tr.getX(), tr.getY()) != 'S') {
            maze.setOneSquare(tr.getSquare(), '.');
          }
          tr = tr.getParent();
          cost++;
          maze = getModifiedMaze();

        }
        cost--;
        return true;

      } else {
       
        for (State st : curr.getSuccessors(explored, maze)) {
          
          if (!queue.contains(st)) {
            queue.add(st);
          }
        }


      }

    }

    return false;
  }
}
