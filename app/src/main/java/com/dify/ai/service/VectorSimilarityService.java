package com.dify.ai.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * VectorSimilarityService provides vector-based similarity matching for intent classification.
 * It implements the vector-based similarity matching as described in the design.
 */
@Service
public class VectorSimilarityService {

    /**
     * Calculates cosine similarity between two vectors.
     *
     * @param vectorA the first vector
     * @param vectorB the second vector
     * @return the cosine similarity score between 0 and 1
     */
    public double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0; // Indicate no similarity if one of the vectors is zero vector
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Converts text to a simple TF-IDF-like vector representation.
     * This is a simplified implementation for demonstration purposes.
     *
     * @param text the input text
     * @param vocabulary the vocabulary to use for vectorization
     * @return a vector representation of the text
     */
    public double[] textToVector(String text, Set<String> vocabulary) {
        if (text == null || text.trim().isEmpty()) {
            return new double[vocabulary.size()];
        }

        // Simple tokenization (split by spaces and punctuation)
        String[] tokens = text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", " ")
                .split("\\s+");

        // Count term frequencies
        Map<String, Integer> termFreq = new HashMap<>();
        for (String token : tokens) {
            if (token.trim().isEmpty()) continue;
            termFreq.put(token, termFreq.getOrDefault(token, 0) + 1);
        }

        // Create vector based on vocabulary
        double[] vector = new double[vocabulary.size()];
        List<String> vocabList = new ArrayList<>(vocabulary);
        Collections.sort(vocabList); // Sort for consistent indexing

        for (int i = 0; i < vocabList.size(); i++) {
            String term = vocabList.get(i);
            vector[i] = termFreq.getOrDefault(term, 0);
        }

        return vector;
    }

    /**
     * Finds the most similar intent based on vector similarity.
     *
     * @param userInput the user input to classify
     * @param intentPatterns map of intent names to their pattern vectors
     * @param threshold minimum similarity threshold for a match
     * @return the most similar intent name, or null if no match exceeds the threshold
     */
    public String findMostSimilarIntent(String userInput, Map<String, double[]> intentPatterns, double threshold) {
        if (userInput == null || userInput.trim().isEmpty() || intentPatterns.isEmpty()) {
            return null;
        }

        // Convert user input to vector (this would need the same vocabulary used for intent patterns)
        // For simplicity, we'll assume the vocabulary is shared between user input and patterns
        Set<String> allVocabulary = new HashSet<>();
        for (double[] pattern : intentPatterns.values()) {
            // In a real implementation, we'd have the vocabulary for each pattern
            // For now, we'll skip this step and assume textToVector is called elsewhere
        }

        // Since we don't have the vocabulary here, we'll simulate by assuming we have a way to convert
        // In a real implementation, you'd have pre-computed vectors for user input
        double[] userInputVector = computeUserInputVector(userInput);

        String bestMatch = null;
        double bestScore = 0.0;

        for (Map.Entry<String, double[]> entry : intentPatterns.entrySet()) {
            double[] patternVector = entry.getValue();
            double similarity = cosineSimilarity(userInputVector, patternVector);

            if (similarity > bestScore && similarity >= threshold) {
                bestScore = similarity;
                bestMatch = entry.getKey();
            }
        }

        return bestMatch;
    }

    /**
     * Computes a vector representation for user input.
     * This is a simplified implementation for demonstration.
     *
     * @param userInput the user input
     * @return a vector representation of the user input
     */
    private double[] computeUserInputVector(String userInput) {
        // In a real implementation, this would use a pre-trained model or a shared vocabulary
        // For this example, we'll just return a simple vector based on character counts
        // This is not a real vectorization approach but serves as a placeholder

        // Just a simple example: count occurrences of some common letters
        double[] vector = new double[10]; // arbitrary size for demo
        String lowerInput = userInput.toLowerCase();

        vector[0] = countOccurrences(lowerInput, 'a');
        vector[1] = countOccurrences(lowerInput, 'e');
        vector[2] = countOccurrences(lowerInput, 'i');
        vector[3] = countOccurrences(lowerInput, 'o');
        vector[4] = countOccurrences(lowerInput, 'u');
        vector[5] = lowerInput.split("\\s+").length; // word count
        vector[6] = lowerInput.length(); // total length
        vector[7] = countOccurrences(lowerInput, 'y');
        vector[8] = countOccurrences(lowerInput, 'h');
        vector[9] = countOccurrences(lowerInput, 'n');

        return vector;
    }

    /**
     * Counts occurrences of a character in a string.
     *
     * @param str the string to search
     * @param ch the character to count
     * @return the number of occurrences
     */
    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) count++;
        }
        return count;
    }
}