import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoublePredicate;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {

    private double vocabsize; // size of vocabulary
    
    private double pos = 0;   // number of positive labels
    private double neg = 0;  // number of negative labels
    private double total = 0; // number of total instances
    
    private double poswords = 0; // number of words under positive label
    private double negwords = 0; // opposite to the above
    private double totalwords = 0; 
    
    private Map<String, Integer> p_map = new HashMap<>();
    private Map<String, Integer> n_map = new HashMap<>();
    
    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        // TODO : Implement
        // Hint: First, calculate the documents and words counts per label and store them. 
        // Then, for all the words in the documents of each label, count the number of occurrences of each word.
        // Save these information as you will need them to calculate the log probabilities later.
        //
        // e.g.
        // Assume m_map is the map that stores the occurrences per word for positive documents
        // m_map.get("catch") should return the number of "catch" es, in the documents labeled positive
        // m_map.get("asdasd") would return null, when the word has not appeared before.
        // Use m_map.put(word,1) to put the first count in.
        // Use m_map.replace(word, count+1) to update the value
      
        vocabsize = (double) v;
               
        for(int i = 0; i < trainData.size(); i++) {
          Instance instance = trainData.get(i);
           if(instance.label == Label.POSITIVE) {
             pos++;
             poswords += instance.words.size();
             for(String e: instance.words) {
               if(p_map.containsKey(e)) p_map.put(e, p_map.get(e) + 1);
               else p_map.put(e, 1);
             }
           } else {
             neg++;
             negwords += instance.words.size();
             for(String e: instance.words) {
               if(n_map.containsKey(e)) n_map.put(e, n_map.get(e) + 1);
               else n_map.put(e, 1);
             }
           }
        }
        
        total = pos + neg;
        totalwords = poswords + negwords;
          
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
      
        Map<Label, Integer> result = new HashMap<>();
        int numPos = 0;
        int numNeg = 0;
        
        for(Instance e : trainData) {
          if(e.label == Label.POSITIVE) numPos += e.words.size();
          else numNeg += e.words.size();
        }
        
        result.put(Label.POSITIVE, numPos);
        result.put(Label.NEGATIVE, numNeg);
        return result;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        
        Map<Label, Integer> result = new HashMap<>();
        
        int numPos = 0;
        int numNeg = 0;
        for(Instance e : trainData) {
          if(e.label == Label.POSITIVE) numPos++;
          else numNeg++;
        }
        
        result.put(Label.POSITIVE, numPos);
        result.put(Label.NEGATIVE, numNeg);
        return result;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.

      if(label == label.POSITIVE) return pos/total;
      return neg/total;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
      
        if(label == Label.POSITIVE) {
          if(! p_map.containsKey(word)) return 1 / (vocabsize+poswords);
          return (p_map.get(word) + 1) / (vocabsize + poswords);
        }
        else {
          if(! n_map.containsKey(word)) return 1 / (vocabsize+negwords);
          return (n_map.get(word) + 1) / (negwords + vocabsize);
        }  
    }

    /**
     * Classifies an list of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
      
        double postive = Math.log(p_l(Label.POSITIVE));
        double negative = Math.log(p_l(Label.NEGATIVE));
        
        for(String e : words) {
          postive += Math.log(p_w_given_l(e, Label.POSITIVE));
          negative += Math.log(p_w_given_l(e, Label.NEGATIVE));
        }
        
        Map<Label, Double> logProb = new HashMap<>();
        logProb.put(Label.POSITIVE, postive);
        logProb.put(Label.NEGATIVE, negative);
        
        ClassifyResult result = new ClassifyResult();
        result.logProbPerLabel = logProb;
        
        if(postive > negative) result.label = Label.POSITIVE;
        else result.label = Label.NEGATIVE;
      
        return result;
    }


}
