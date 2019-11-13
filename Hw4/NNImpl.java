import java.util.*;

/**
 * The main class that handles the entire network Has multiple attributes each with its own use
 */

public class NNImpl {
  private ArrayList<Node> inputNodes; // list of the output layer nodes.
  private ArrayList<Node> hiddenNodes; // list of the hidden layer nodes
  private ArrayList<Node> outputNodes; // list of the output layer nodes

  private ArrayList<Instance> trainingSet; // the training set

  private double learningRate; // variable to store the learning rate
  private int maxEpoch; // variable to store the maximum number of epochs
  private Random random; // random number generator to shuffle the training set

  /**
   * This constructor creates the nodes necessary for the neural network Also connects the nodes of
   * different layers After calling the constructor the last node of both inputNodes and hiddenNodes
   * will be bias nodes.
   */

  NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch,
    Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
    this.trainingSet = trainingSet;
    this.learningRate = learningRate;
    this.maxEpoch = maxEpoch;
    this.random = random;

    // input layer nodes
    inputNodes = new ArrayList<>();
    int inputNodeCount = trainingSet.get(0).attributes.size();
    int outputNodeCount = trainingSet.get(0).classValues.size();
    for (int i = 0; i < inputNodeCount; i++) {
      Node node = new Node(0);
      inputNodes.add(node);
    }

    // bias node from input layer to hidden
    Node biasToHidden = new Node(1);
    inputNodes.add(biasToHidden);

    // hidden layer nodes
    hiddenNodes = new ArrayList<>();
    for (int i = 0; i < hiddenNodeCount; i++) {
      Node node = new Node(2);
      // Connecting hidden layer nodes with input layer nodes
      for (int j = 0; j < inputNodes.size(); j++) {
        NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
        node.parents.add(nwp);
      }
      hiddenNodes.add(node);
    }

    // bias node from hidden layer to output
    Node biasToOutput = new Node(3);
    hiddenNodes.add(biasToOutput);

    // Output node layer
    outputNodes = new ArrayList<>();
    for (int i = 0; i < outputNodeCount; i++) {
      Node node = new Node(4);
      // Connecting output layer nodes with hidden layer nodes
      for (int j = 0; j < hiddenNodes.size(); j++) {
        NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
        node.parents.add(nwp);
      }
      outputNodes.add(node);
    }
  }

  /**
   * Get the prediction from the neural network for a single instance Return the idx with highest
   * output values. For example if the outputs of the outputNodes are [0.1, 0.5, 0.2], it should
   * return 1. The parameter is a single instance
   */

  public int predict(Instance instance) {

    forwardProp(instance);
    int maxindex = 0;
    for (int i = 1; i < outputNodes.size(); i++) {
      if (outputNodes.get(i).getOutput() > outputNodes.get(maxindex).getOutput())
        maxindex = i;
    }

    return maxindex;
  }

  private void forwardProp(Instance instance) {

    for (int i = 0; i < inputNodes.size() - 1; i++) {
      inputNodes.get(i).setInput(instance.attributes.get(i));
    }

    for (int i = 0; i < hiddenNodes.size() - 1; i++) {
      hiddenNodes.get(i).calculateOutput(0);
    }

    double softsum = 0;
    for (Node e : outputNodes) { // compute the total denominator for softmax function
      double tmp = 0;
      for (NodeWeightPair o : e.parents) {
        tmp += o.node.getOutput() * o.weight;
      }
      softsum += Math.exp(tmp);
    }

    for (Node e : outputNodes) {
      e.calculateOutput(softsum);
    }

  }
  
  static int test = 1;
  
  private void backProp(Instance instance) {
    
    updateDelta(instance);
    for(Node e : outputNodes) {
      e.updateWeight(learningRate);
    }
    for(Node e: hiddenNodes) {
      e.updateWeight(learningRate);
    }
   
//    if(test == 1) {
//      test = 0;
//      System.out.println("Weight from input to hidden layer");
//      for(int i = 0; i < hiddenNodes.size()-1; i++) {
//        for(NodeWeightPair e: hiddenNodes.get(i).parents) {
//          System.out.println(e.weight);
//        }
//      }
//      System.out.println("Weight from Hidden to Output layer");
//      for(int i = 0; i < outputNodes.size(); i++) {
//        for(NodeWeightPair e: outputNodes.get(i).parents) {
//          System.out.println(e.weight);
//        }
//      }
//    }
    
  }

  private void updateDelta(Instance instance) {

    for (int i = 0; i < outputNodes.size(); i++) {
      double delta = instance.classValues.get(i) - outputNodes.get(i).getOutput();
      outputNodes.get(i).calculateDelta(delta);
    }

    for (int i = 0; i < hiddenNodes.size() - 1; i++) {
      Node curr = hiddenNodes.get(i);
      double sigma = 0;
      for (Node e : outputNodes) {

        // ∑W(jk)Δk means "you have a hidden node j. look at all the output nodes that j is connected to (we
        // will call each of these output nodes k). then for each k, sum (the weight between node j
        // and node k) * (delta of the output node k).
        sigma += e.parents.get(i).weight * e.getDelta(); // sigma is used for hidden layer delta

      }
      if(curr.getOutput() <= 0) {
        curr.calculateDelta(0);
      } else {
        curr.calculateDelta(sigma);
      }
              
    }

  }

  /**
   * Train the neural networks with the given parameters
   * <p>
   * The parameters are stored as attributes of this class
   */  
  public void train() {
    
    for (int i = 0; i < maxEpoch; i++) {
      double totalloss = 0;
      Collections.shuffle(trainingSet,random);
      for (Instance e : trainingSet) {
        train(e);
      }
      for (Instance e : trainingSet) {
        totalloss += loss(e);
      }
      totalloss = totalloss / trainingSet.size();
      System.out.println("Epoch: " + i + String.format(", Loss: %.3e", totalloss));
    }
    

  }

  
  public void train(Instance instance) {
    forwardProp(instance);
    backProp(instance);
  }

  /**
   * Calculate the cross entropy loss from the neural network for a single instance. The parameter
   * is a single instance
   */
  private double loss(Instance instance) {
    double sum = 0.00;
    forwardProp(instance);
    for(int i = 0; i < outputNodes.size(); i++) {
      sum -= Math.log(outputNodes.get(i).getOutput()) * instance.classValues.get(i);
    }
    return sum;
  }
}
