import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.spi.TransactionalWriter;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {

        double result = 0;
      
        int num = trainData.size() / k;
        for(int i = 0; i < trainData.size(); i += num) {   
          
          clf = new NaiveBayesClassifier();

          List<Instance> tmp = new ArrayList<>();
          tmp.addAll(trainData.subList(0, i));
          tmp.addAll(trainData.subList(i+num, trainData.size()));

          clf.train(tmp, v);
          double testcount = 0;
          double accurate = 0;
          for(int j = i; j < i + num && j < trainData.size(); j++) {
            if(clf.classify(trainData.get(j).words).label == trainData.get(j).label) {accurate++;}
            testcount++;
          }
          result += accurate / testcount;
        }
      
        return result / k;
    }
}
