package ResearchPaper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import edu.stanford.nlp.pipeline.*;
import java.util.Properties;

public class Analysis {
    public static class word {
        private String myPos;
        private Double myPosSentiment;
        private Double myNegSentiment;
        public word(String pos, Double posSentiment, Double negSentiment) {
            myPos = pos;
            myPosSentiment = posSentiment;
            myNegSentiment = negSentiment;
        }
        public String getMyPos() {return  myPos;}
        public Double getMyPosSentiment() {return  myPosSentiment;}
        public Double getMyNegSentiment() {return  myNegSentiment;}
    }
    public static void main(String[] args) {
        try {
            var WordNetScanner = new Scanner(new File("SentiWordNet_3.0.0.txt"));
            Map<String, word> WordMap = new HashMap<>();
            while (WordNetScanner.hasNext()) {
                String[] line = WordNetScanner.nextLine().split("\\s+");
                String tempPos = line[0];
                Double tempPosSentiment = Double.parseDouble(line[2]);
                Double tempNegSentiment = Double.parseDouble(line[3]);
                int i = 4;
                while (line[i].contains("#")) {
                    WordMap.put(line[i], new word(tempPos, tempPosSentiment, tempNegSentiment));
                    i++;
                }
            }
            var CommonScentences = new Scanner(new File("yelp_labelled.txt"));
            double successes = 0.0;
            int total = 0;
            String[] negation = {"no", "not", "neither", "never", "none", "nothing", "nor", "nobody", "doesnt", "havent", "nowhere", "wasnt", "dont", "wont", "cant", "never", "arent", "isnt", "werent", "couldnt", "mustnt", "shouldnt", "wouldnt", "didnt", "hasnt", "havent", "hadnt", "lack", "without", "hardly", "barely", "scarcely", "fail"};
            while (CommonScentences.hasNext()) {
                String[] line = CommonScentences.nextLine().split("\\s+");

                for (int i = 0; i < line.length-1; i++) {
                    line[i] = line[i].toLowerCase().replaceAll("\\p{Punct}", "");
                }
                int score = Integer.parseInt(line[line.length-1]);
                line[line.length-1] = "";
                double average = 0.0;
                double value = 0.0;
                boolean negated = false;
                /*  PRINT LINES AND SCORES
                for (int i = 0; i < line.length-1; i++) {
                    System.out.print(line[i]);
                    if (WordMap.get(line[i] + "#1") != null) {
                        System.out.print(WordMap.get(line[i] + "#1").getMyPosSentiment() + "|" + WordMap.get(line[i] + "#1").getMyNegSentiment());
                    }
                }
                */
                for (int i = 0; i < line.length-1; i++) {
                    value = 0.0;
                    negated = false;
                    if (WordMap.get(line[i] + "#1") != null) {
                        for (int j = 0; j < 5; j++) { //j < window size
                            if (i > j) {
                                for (String item : negation) {
                                    if (line[i-(j+1)].equals(item)) {
                                        negated = !negated;
                                        break;
                                    }
                                }
                            }
                        }
                        if (negated) {
                            average += (WordMap.get(line[i] + "#1").getMyNegSentiment() - WordMap.get(line[i] + "#1").getMyPosSentiment());
                        } else {
                            average += (WordMap.get(line[i] + "#1").getMyPosSentiment() - WordMap.get(line[i] + "#1").getMyNegSentiment());
                        }
                    }
                }
                average = average/(line.length-1);
                System.out.print(score + " " + average);
                if ((int)(average+1) == score && average != 0.0) {
                    successes++;
                    //System.out.print(" success"); Print Successes
                }
                if (average != 0.0) {
                    total++;
                }
                //System.out.println(); Spacing
            }
            System.out.println((successes/total)*100); // percent accuracy
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
