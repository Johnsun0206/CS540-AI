//////////////////// ALL ASSIGNMENTS INCLUDE THIS SECTION /////////////////////
//
// Title: P3 Binray Decision Tree
// Files: DecisionTreeImpl.java, DecTreeNode.java, HW3.java
// Course: CS 540
//
// Author: Lingjun Sun
// Email: lsun89@wisc.edu
// Lecturer's Name: Chuck Dyer
// Lecture Number: 001
//
//////////////////// PAIR PROGRAMMERS COMPLETE THIS SECTION ///////////////////
//
// Partner Name:
// Partner Email:
// Partner Lecturer's Name:
//
// VERIFY THE FOLLOWING BY PLACING AN X NEXT TO EACH TRUE STATEMENT:
// X Write-up states that pair programming is allowed for this assignment.
// X We have both read and understand the course Pair Programming Policy.
// X We have registered our team prior to the team registration deadline.
//
///////////////////////////// CREDIT OUTSIDE HELP /////////////////////////////
//
// Students who get help from sources other than their partner must fully
// acknowledge and credit those sources of help here. Instructors and TAs do
// not need to be credited here, but tutors, friends, relatives, room mates,
// strangers, and others do. If you received no outside help from either type
// of source, then please explicitly indicate NONE.
//
// Persons: NA
// Online Sources: NA
//
/////////////////////////////// 80 COLUMNS WIDE ///////////////////////////////

import java.util.ArrayList;
import java.util.List;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */
public class DecisionTreeImpl {
  public DecTreeNode root;
  public List<List<Integer>> trainData;
  public int maxPerLeaf;
  public int maxDepth;
  public int numAttr;

  // Build a decision tree given a training set
  DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
    this.trainData = trainDataSet;
    this.maxPerLeaf = mPerLeaf;
    this.maxDepth = mDepth;
    if (this.trainData.size() > 0)
      this.numAttr = trainDataSet.get(0).size() - 1;
    this.root = buildTreeHelper(trainDataSet, 0);

  }

  private DecTreeNode buildTreeHelper(List<List<Integer>> example, int depth) {
    // try to see if there is a leaf node and we need to return
    if (example.isEmpty()) {
      return new DecTreeNode(1, -1, -1);
    }
   
    int countones = 0;
    int countzeros = 0;
    
    for (int i = 0; i < example.size(); i++) {
      if (example.get(i).get(numAttr) != 0) {
        countones++;
      } else {
        countzeros++;
      }
    }
    int majority = -1;
    if (countzeros <= countones) {
      majority = 1;
    } else {
      majority = 0;
    }

    if ((example.size() <= maxPerLeaf) || (depth == maxDepth)) {
      return new DecTreeNode(majority, -1, -1);
    }
    // if we get to this point and still don't have a leaf
    // attribute: 0,1,...,numAttr-1
    // threshold: 1,2,3...,9
    double best = Double.NEGATIVE_INFINITY;
    int bestatt = 0;
    int bestthr = 0;
    
    for (int i = 0; i < numAttr; i++) {
      for (int j = 1; j < 10; j++) {

        double newIG = infoGain(example, i, j);
        if (ifnewBest(newIG, best) == true) {
          best = newIG;
          bestatt = i;
          bestthr = j;
        }
      }
    }

    if (best == 0) {
      return new DecTreeNode(majority, -1, -1);
    }

    List<List<Integer>> left = new ArrayList<List<Integer>>();
    // instances where attribute > threshold
    List<List<Integer>> right = new ArrayList<List<Integer>>();
    for (int i = 0; i < example.size(); i++) {
      List<Integer> current = example.get(i);
      if (current.get(bestatt) <= bestthr) {
        left.add(current);
      } else {
        right.add(current);
      }
    }

    DecTreeNode node = new DecTreeNode(majority, bestatt, bestthr);
    node.left = buildTreeHelper(left, depth + 1);
    node.right = buildTreeHelper(right, depth + 1);

    return node;
  }

  private double log2(double n) {
    
    if (n != 0) {
      return Math.log(n) / Math.log(2.0);
    }

    return 0;
  }

  // calculate the Entropy of labels
  private double getEntropy(List<List<Integer>> example) {
 
    double sum = 0.0;
    double entropy = 0.0;

    double countones = 0.0;
    double countzero = 0.0;
    
    for (int i = 0; i < example.size(); i++) {
      if (example.get(i).get(numAttr) == 0) {
        countzero++;
      } else {
        countones++;
      }
    }
    sum = countzero + countones;

    entropy = (-1.0)
        * ((countzero / sum) * log2(countzero / sum) + (countones / sum) * log2(countones / sum));

    return entropy;

  }

  private double getEntropy(List<List<Integer>> example, int curAttr, int curThre) {
    
    double entropy = 0.0;
    double lower0 = 0.0;
    double upper0 = 0.0;
    
    double lower1 = 0.0;
    double upper1 = 0.0;

    double low = 0.0;
    double up = 0.0;

    // while majority equals zeroes
    for (int a = 0; a < example.size(); a++) {
      if (example.get(a).get(numAttr) == 0) {
        if (example.get(a).get(curAttr) <= curThre) {
          lower0++;
        } else {
          upper0++;
        }
      } else {
        if (example.get(a).get(curAttr) <= curThre) {
          lower1++;
        } else {
          upper1++;
        }
      }
    }

    low = lower0 + lower1;
    up = upper0 + upper1;
    double data = low + up;

   entropy = (-1.0) * (low / data)
        * ((lower0 / low) * log2(lower0 / low) + (lower1 / low) * log2(lower1 / low))
        + (-1.0) * (up / data)
            * ((upper0 / up) * log2(upper0 / up) + (upper1 / up) * log2(upper1 / up));

    return entropy;

  }

  private double infoGain(List<List<Integer>> example, int curAttr, int curThre) {
    double result = getEntropy(example) - getEntropy(example, curAttr, curThre);
    return result;
  }

  private boolean ifnewBest(double newNum, double best) {

    if (newNum > best) {
      return true;
    }
    return false;

  }

  
  private int classify(List<Integer> instance) {
    
    DecTreeNode curr = this.root;
    while (!curr.isLeaf()) {
      if (instance.get(curr.attribute) > curr.threshold) {
        curr = curr.right;
      } else {
        curr = curr.left;
      }
    }
    return curr.classLabel;
  }


  // Print the decision tree in the specified format
  public void printTree() {
    printTreeNode("", this.root);
  }

  public void printTreeNode(String prefixStr, DecTreeNode node) {
    String printStr = prefixStr + "X_" + node.attribute;
    System.out.print(printStr + " <= " + String.format("%d", node.threshold));
    if (node.left.isLeaf()) {
      System.out.println(" : " + String.valueOf(node.left.classLabel));
    } else {
      System.out.println();
      printTreeNode(prefixStr + "|\t", node.left);
    }
    System.out.print(printStr + " > " + String.format("%d", node.threshold));
    if (node.right.isLeaf()) {
      System.out.println(" : " + String.valueOf(node.right.classLabel));
    } else {
      System.out.println();
      printTreeNode(prefixStr + "|\t", node.right);
    }
  }

  public double printTest(List<List<Integer>> testDataSet) {
    int numEqual = 0;
    int numTotal = 0;
    for (int i = 0; i < testDataSet.size(); i++) {
      int prediction = classify(testDataSet.get(i));
      int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
      System.out.println(prediction);
      if (groundTruth == prediction) {
        numEqual++;
      }
      numTotal++;
    }
    double accuracy = numEqual * 100.0 / (double) numTotal;
    System.out.println(String.format("%.2f", accuracy) + "%");
    return accuracy;
  }
}
