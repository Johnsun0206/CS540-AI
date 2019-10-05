import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
  private int size; // The number of stones
  private boolean[] stones; // Game state: true for available stones, false for taken ones
  private int lastMove; // The last move

  /**
   * Class constructor specifying the number of stones.
   */
  public GameState(int size) {

    this.size = size; 

    // For convenience, we use 1-based index, and set 0 to be unavailable
    this.stones = new boolean[this.size + 1];
    this.stones[0] = false;

    // Set default state of stones to available
    for (int i = 1; i <= this.size; ++i) {
      this.stones[i] = true;
    }

    // Set the last move be -1
    this.lastMove = -1;
  }

  /**
   * Copy constructor
   */
  public GameState(GameState other) {
    this.size = other.size;
    this.stones = Arrays.copyOf(other.stones, other.stones.length);
    this.lastMove = other.lastMove;
  }


  /**
   * This method is used to compute a list of legal moves
   *
   * @return This is the list of state's moves
   */
  public List<Integer> getMoves() {

    List<Integer> list = new ArrayList<>();
    if (lastMove == -1) {
      for (int i = 1; i <= this.size; i++) {
        if((i < size/2.0) && (i % 2 != 0))
          list.add(i);
      }
    } else {
      for (int i = 1; i <= this.size; i++) {
        if (stones[i] == true && (lastMove % i == 0 || isMultiple(i, lastMove, size))) {
          list.add(i);
        }
      }
    }


    return list;
  }

  // x is a multiple of y
  private static boolean isMultiple(int x, int y, int size) {

    if(x % y == 0) return true;
    return false;
  }



  /**
   * This method is used to generate a list of successors using the getMoves() method
   *
   * @return This is the list of state's successors
   */
  public List<GameState> getSuccessors() {
    return this.getMoves().stream().map(move -> {
      var state = new GameState(this);
      state.removeStone(move);
      return state;
    }).collect(Collectors.toList());
  }


  /**
   * This method is used to evaluate a game state based on the given heuristic function
   *
   * @return double This is the static score of given state
   */
  public double evaluate() {

    double result;

    if (getMoves().contains(1))
      return 0.0;
    if (getSuccessors().size() == 0) {
      result = -1.0;
    } else if (lastMove == 1) {
      int size = getSuccessors().size();
      if (size % 2 == 0)
        result = -0.5;
      else
        result = 0.5;
    } else if (Helper.isPrime(lastMove)) {
      int count = countMultiples(lastMove);
      if (count % 2 != 0)
        result = 0.7;
      else
        result = -0.7;
    } else {
      int primefactor = Helper.getLargestPrimeFactor(lastMove);
      int count = countMultiples(primefactor);
      if(stones[primefactor]) count++;
      if (count % 2 != 0)
        result = 0.6;
      else
        result = -0.6;
    }

     if (countStonesTaken() % 2 != 0) result = result * (-1);
    
    return result;
  }

  public int countStonesTaken() {
    int count = 0;
    for (int i = 1; i <= this.size; i++) {
      if (!stones[i])
        count++;
    }
    return count;
  }

  public int countMultiples(int x) {

    int count = 0;
    for (GameState s: getSuccessors()) {
      if (isMultiple(s.getLastMove(), x, this.size))
        count++;
    }
    return count;
  }

  /**
   * This method is used to take a stone out
   *
   * @param idx Index of the taken stone
   */
  public void removeStone(int idx) {
    this.stones[idx] = false;
    this.lastMove = idx;
  }

  /**
   * These are get/set methods for a stone
   *
   * @param idx Index of the taken stone
   */
  public void setStone(int idx) {
    this.stones[idx] = true;
  }

  public boolean getStone(int idx) {
    return this.stones[idx];
  }

  /**
   * These are get/set methods for lastMove variable
   *
   * @param move Index of the taken stone
   */
  public void setLastMove(int move) {
    this.lastMove = move;
  }

  public int getLastMove() {
    return this.lastMove;
  }

  /**
   * This is get method for game size
   *
   * @return int the number of stones
   */
  public int getSize() {
    return this.size;
  }

}
