// src/main/java/proymodpredictivoia/demo/ai/PredictResponse.java
package proymodpredictivoia.demo.ai;

import java.util.List;

public class PredictResponse {
    private double probability;
    private List<List<Object>> top_features;

    // Constructor vacío para Jackson
    public PredictResponse() {}

    // Getter para 'probability'
    public double getProbability() {
        return probability;
    }

    // Setter para 'probability' (necesario para Jackson)
    public void setProbability(double probability) {
        this.probability = probability;
    }

    // Getter para 'top_features'
    public List<List<Object>> getTop_features() {
        return top_features;
    }

    // Setter para 'top_features' (necesario para Jackson)
    public void setTop_features(List<List<Object>> top_features) {
        this.top_features = top_features;
    }

    @Override
    public String toString() {
        return "PredictResponse{" +
               "probability=" + probability +
               ", top_features=" + top_features +
               '}';
    }
}
