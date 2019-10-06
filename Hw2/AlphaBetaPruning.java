import java.util.ArrayList;


public class AlphaBetaPruning {

  private int move;
  private double value;
  private int nodesvisited;
  private int nodesevaluated;
  private int depthreached;
  private ArrayList<Double> list = new ArrayList<>();

  private ArrayList<Double> branchingfactor = new ArrayList<>();


  private int realvisited;
  private int realevaluated;
  private int realdepth;
  private double actualbf = 0.0;

  public AlphaBetaPruning() {}

  /**
   * This function will print out the information to the terminal, as specified in the homework
   * description.
   */
  public void printStats() {

    System.out.println("Move: " + move);
    System.out.println("Value: " + value);
    System.out.println("Number of Nodes Visited: " + realvisited);
    System.out.println("Number of Nodes Evaluated: " + realevaluated);
    System.out.println("Max Depth Reached: " + realdepth);
    System.out.println("Avg Effective Branching Factor: " + String.format("%.1f", actualbf));
    System.out.println("");

  }

  /**
   * This function will start the alpha-beta search
   * 
   * @param state This is the current game state
   * @param depth This is the specified search depth
   */
  public void run(GameState state, int depth) {



    if (state.countStonesTaken() % 2 != 0) {
      value = alphabeta(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, depth);

      realvisited = nodesvisited;
      realevaluated = nodesevaluated;
      realdepth = depthreached;
      int count = 0;
      double sum = 0.0;
      for (int i = 0; i < branchingfactor.size(); i++) {
        sum += branchingfactor.get(i);
        count++;
      }

      actualbf = sum / count;


      for (int i = 0; i < state.getSuccessors().size(); i++) {

        double curr = alphabeta(state.getSuccessors().get(i), 1, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, true, depth);

        if (curr == value) {
          move = state.getSuccessors().get(i).getLastMove();
          break;
        }
      }

      // int index = list.indexOf(value);
      // move = state.getSuccessors().get(index).getLastMove();


    } else {
      value = alphabeta(state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, depth);

      realvisited = nodesvisited;
      realevaluated = nodesevaluated;
      realdepth = depthreached;

      int count = 0;
      double sum = 0.0;
      for (int i = 0; i < branchingfactor.size(); i++) {
        sum += branchingfactor.get(i);
        count++;
      }

      actualbf = sum / count;

      
      for (int i = 0; i < state.getSuccessors().size(); i++) {

        double curr = alphabeta(state.getSuccessors().get(i), 1, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, false, depth);

        if (curr == value) {
          move = state.getSuccessors().get(i).getLastMove();
          break;
        }
      }

      // int index = list.indexOf(value);
      // move = state.getSuccessors().get(index).getLastMove();



    }

  }

  /**
   * This method is used to implement alpha-beta pruning for both 2 players
   * 
   * @param state This is the current game state
   * @param depth Current depth of search
   * @param alpha Current Alpha value
   * @param beta Current Beta value
   * @param maxPlayer True if player is Max Player; Otherwise, false
   * @return double This is the number indicating score of the best next move
   */
  private double alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer,
      int maxdepth) {

    nodesvisited++;
    depthreached = Math.max(depthreached, depth);

    if (state.getSuccessors().size() == 0 || depth == maxdepth) {
      nodesevaluated++;
      return state.evaluate();
    }

    double bf = 0.0;

    if (maxPlayer) {
      double v = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < state.getSuccessors().size(); i++) {
        bf++;
        v = Math.max(v,
            alphabeta(state.getSuccessors().get(i), depth + 1, alpha, beta, !maxPlayer, maxdepth));

        // double curr = alphabeta(state.getSuccessors().get(i), depth + 1, alpha, beta, !maxPlayer,
        // maxdepth);
        // v = Math.min(curr, v);
        //
        // if(depth == 1) list.add(curr);

        if (v >= beta) {

          break;
        }
        alpha = Math.max(alpha, v);
      }
      // if(depth == 1)
      // {list.add(v);}
      branchingfactor.add(bf);
      return v;
    } else {
      double v = Double.POSITIVE_INFINITY;
      for (int i = 0; i < state.getSuccessors().size(); i++) {
        bf++;
        v = Math.min(v,
            alphabeta(state.getSuccessors().get(i), depth + 1, alpha, beta, !maxPlayer, maxdepth)); // min
                                                                                                    // alphabeta
        // double curr = alphabeta(state.getSuccessors().get(i), depth + 1, alpha, beta, !maxPlayer,
        // maxdepth);
        // v = Math.min(curr, v);
        // if(depth == 1) list.add(curr);

        if (v <= alpha) {
          break;
        }
        beta = Math.min(beta, v);
      }
      // if(depth == 1)
      // {list.add(v);}
      branchingfactor.add(bf);
      return v;
    }

  }

}
