package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MultipleKnapsackInstanceGenerator {

    public static void main(String[] args) {
        // Different problem sizes (number of objects)
        int[] problemSizes = {50,100,200,300,400,500,700 };

        // Number of knapsacks
        int[] numKnapsacks = {10, 20, 30,40,50,100};

        for (int size : problemSizes) {
            for (int numSacks : numKnapsacks) {
                int[][] objects = generateRandomInstance(size);
                int[] capacities = generateRandomCapacities(numSacks, 10, 40); // Generate capacities for multiple knapsacks
                saveInstanceToCSV(objects, capacities, "instance_" + size + "_" + numSacks + ".csv");
            }
        }
    }

    // Generates a random instance of the multiple knapsack problem
    static int[][] generateRandomInstance(int numObjects) {
        Random random = new Random();
        int[][] objects = new int[numObjects][3]; // [ID, Weight, Value]
        for (int i = 0; i < numObjects; i++) {
            objects[i][0] = i + 1; // ID
            objects[i][1] = random.nextInt(50) + 1; // Random weight between 1 and 50
            objects[i][2] = random.nextInt(100) + 1; // Random value between 1 and 100
        }
        return objects;
    }

    // Generates random capacities for the knapsacks
    static int[] generateRandomCapacities(int numSacks, int minValue, int maxValue) {
        Random random = new Random();
        int[] capacities = new int[numSacks];
        for (int i = 0; i < numSacks; i++) {
            capacities[i] = random.nextInt(maxValue - minValue + 1) + minValue; // Random capacity between minValue and maxValue
        }
        return capacities;
    }

    // Saves a multiple knapsack problem instance to a CSV file
    public static void saveInstanceToCSV(int[][] objects, int[] capacities, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("ID,Poids,Valeur\n");
            for (int i = 0; i < objects.length; i++) {
                writer.write(objects[i][0] + "," + objects[i][1] + "," + objects[i][2] + "\n");
            }
            writer.write("\nCapacites\n");
            for (int i = 0; i < capacities.length; i++) {
                writer.write(capacities[i] + "\n");
            }
            System.out.println("Multiple knapsack problem instance saved to file: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
