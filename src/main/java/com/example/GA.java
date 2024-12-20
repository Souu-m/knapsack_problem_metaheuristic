package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA {
    private static int[][] objects;
    private int[] capacities;
    private int populationSize;
    private int maxGenerations;
    private double mutationRate;
    private Random random;
    private int[] currentWeights; // Array to store the current weights of the knapsacks
    private int bestValue; // Variable to store the best value

    public GA(int[][] objects, int[] capacities, int populationSize, int maxGenerations, double mutationRate) {
        GA.objects = objects;
        this.capacities = capacities;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.random = new Random();
        this.currentWeights = new int[capacities.length]; // Initialize the current weights array
        this.bestValue = Integer.MIN_VALUE; // Initialize the best value
    }

    private List<List<Integer>> initializePopulation() {
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(createRandomSolution());
        }
        return population;
    }

    private List<Integer> createRandomSolution() {
        List<Integer> solution = new ArrayList<>();
        int[] weights = new int[capacities.length]; // Array to keep track of current weights of knapsacks

        for (int i = 0; i < objects.length; i++) {
            double probability = (double) objects[i][1] / (double) capacities.length;
            if (random.nextDouble() < probability) {
                int knapsackIndex = random.nextInt(capacities.length);
                if (weights[knapsackIndex] + objects[i][1] <= capacities[knapsackIndex]) {
                    solution.add(knapsackIndex);
                    weights[knapsackIndex] += objects[i][1];
                } else {
                    solution.add(-1); // If the item can't be added to any knapsack, mark it as -1
                }
            } else {
                solution.add(-1); // If the item is not chosen, mark it as -1
            }
        }
        return solution;
    }

    private int calculateFitness(List<Integer> solution) {
        int totalValue = 0;
        int[] tempWeights = new int[capacities.length]; // Temporary weights array for this solution
    
        for (int i = 0; i < capacities.length; i++) {
            int knapsackWeight = 0;
            int knapsackValue = 0;
            for (int j = 0; j < solution.size(); j++) {
                if (solution.get(j) == i) {
                    knapsackWeight += objects[j][1];
                    if (knapsackWeight <= capacities[i]) {
                        knapsackValue += objects[j][2];
                    } else {
                        solution.set(j, -1); // Remove the item from the knapsack if it exceeds capacity
                    }
                }
            }
            totalValue += knapsackValue;
            tempWeights[i] = knapsackWeight; // Save the current weight of the knapsack
        }
        
        System.arraycopy(tempWeights, 0, currentWeights, 0, capacities.length); // Update currentWeights with tempWeights
        return totalValue;
    }
    
    

    private void mutate(List<Integer> solution) {
        for (int i = 0; i < solution.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                int originalKnapsack = solution.get(i);
                int newKnapsack = random.nextInt(capacities.length);

                if (originalKnapsack != newKnapsack && newKnapsack != -1) {
                    int originalWeight = 0;
                    int newWeight = 0;

                    for (int j = 0; j < solution.size(); j++) {
                        if (solution.get(j) == originalKnapsack) {
                            originalWeight += objects[j][1];
                        }
                        if (solution.get(j) == newKnapsack) {
                            newWeight += objects[j][1];
                        }
                    }

                    if (newWeight + objects[i][1] <= capacities[newKnapsack]) {
                        solution.set(i, newKnapsack);
                    }
                }
            }
        }
    }

    private List<Integer> crossover(List<Integer> parent1, List<Integer> parent2) {
        List<Integer> offspring = new ArrayList<>();
        double[] utilityRatio = new double[objects.length];
        for (int i = 0; i < objects.length; i++) {
            double sum = 0;
            for (int j = 0; j < capacities.length; j++) {
                sum += objects[i][2] * objects[i][1] * capacities[j];
            }
            utilityRatio[i] = sum / capacities.length;
        }
        List<Integer> sortedObjects = new ArrayList<>();
        for (int i = 0; i < objects.length; i++) {
            sortedObjects.add(i);
        }
        sortedObjects.sort((a, b) -> Double.compare(utilityRatio[b], utilityRatio[a]));

        int[] weights = new int[capacities.length]; // Array to keep track of current weights of knapsacks

        for (int i = 0; i < objects.length; i++) {
            int objectIndex = sortedObjects.get(i);
            if (objectIndex < parent1.size()) {
                int knapsackIndex = parent1.get(objectIndex);
                if (knapsackIndex != -1 && weights[knapsackIndex] + objects[objectIndex][1] <= capacities[knapsackIndex]) {
                    offspring.add(knapsackIndex);
                    weights[knapsackIndex] += objects[objectIndex][1];
                } else {
                    offspring.add(-1); // Mark as -1 if it can't be added
                }
            } else if (random.nextDouble() < 0.5 && objectIndex < parent2.size()) {
                int knapsackIndex = parent2.get(objectIndex);
                if (knapsackIndex != -1 && weights[knapsackIndex] + objects[objectIndex][1] <= capacities[knapsackIndex]) {
                    offspring.add(knapsackIndex);
                    weights[knapsackIndex] += objects[objectIndex][1];
                } else {
                    offspring.add(-1); // Mark as -1 if it can't be added
                }
            } else {
                offspring.add(-1); // Mark as -1 if it can't be added
            }
        }
        return offspring;
    }

    

    private List<List<Integer>> selectParents(List<List<Integer>> population) {
        List<List<Integer>> parents = new ArrayList<>();
        List<Double> fitnessValues = new ArrayList<>();
        List<Boolean> validSolutions = new ArrayList<>();
        for (List<Integer> solution : population) {
            int fitness = calculateFitness(solution);
            fitnessValues.add((double) fitness);
            validSolutions.add(isValidSolution(solution));
        }
        for (int i = 0; i < population.size(); i++) {
            if (!validSolutions.get(i)) {
                fitnessValues.set(i, 0.0001);
            }
        }
        for (int i = 0; i < 2; i++) {
            double totalFitness = fitnessValues.stream().mapToDouble(Double::doubleValue).sum();
            double rand = random.nextDouble() * totalFitness;
            double partialSum = 0;
            for (int j = 0; j < population.size(); j++) {
                partialSum += fitnessValues.get(j);
                if (partialSum >= rand) {
                    parents.add(population.get(j));
                    break;
                }
            }
        }
        return parents;
    }

    private boolean isValidSolution(List<Integer> solution) {
        for (int i = 0; i < capacities.length; i++) {
            int knapsackWeight = 0;
            for (int j = 0; j < solution.size(); j++) {
                if (solution.get(j) == i) {
                    knapsackWeight += objects[j][1];
                }
            }
            if (knapsackWeight > capacities[i]) {
                return false;
            }
        }
        return true;
    }
    public List<Integer> solve() {
        List<List<Integer>> population = initializePopulation();
        List<Integer> bestSolution = null;
        bestValue = Integer.MIN_VALUE; // Ensure bestValue is reset before solving
    
        for (int generation = 0; generation < maxGenerations; generation++) {
            List<List<Integer>> nextGeneration = new ArrayList<>();
            while (nextGeneration.size() < populationSize) {
                List<List<Integer>> parents = selectParents(population);
                List<Integer> parent1 = parents.get(0);
                List<Integer> parent2 = parents.get(1);
                List<Integer> offspring = crossover(parent1, parent2);
                if (!offspring.isEmpty()) {
                    mutate(offspring);
                    nextGeneration.add(offspring);
                    int fitness = calculateFitness(offspring);
                    if (fitness > bestValue) {
                        bestValue = fitness;
                        bestSolution = new ArrayList<>(offspring);
                    }
                }
            }
            population = nextGeneration;
        }
    
        // Calculate and display the final current weights
        if (bestSolution != null) {
            calculateFitness(bestSolution); // Update currentWeights based on the best solution
        }
    
        return bestSolution;
    }
    
    

    public int[] getCurrentWeights() {
        return currentWeights;
    }

    public int getBestValue() {
        return bestValue;
    }

    public static void main(String[] args) {
        int[][] objects;
        int[] capacities;
    
        ReadData solverManager = new ReadData();
        try {
            solverManager.loadFromCsv("C:/Users/nadir/OneDrive/Bureau/demo/instance_50_10.csv");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        capacities = solverManager.getCapacities();
        objects = solverManager.getObjects();
        long startTime = System.currentTimeMillis();
        GA solver = new GA(objects, capacities, 50, 300, 0.1);
        List<Integer> result = solver.solve();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Best Solution:");
        for (int i = 0; i < capacities.length; i++) {
            System.out.println("Knapsack " + (i + 1) + ":");
            for (int j = 0; j < result.size(); j++) {
                if (result.get(j) == i) {
                    System.out.println(
                            "   Object " + objects[j][0] + " - Value: " + objects[j][2] + ", Weight: " + objects[j][1]);
                }
            }
       
        }
  
    
        // Calculate and print global accuracy
        int[] currentWeights = solver.getCurrentWeights();
        boolean allWithinCapacity = true;
        double totalUtilization = 0;
        for (int i = 0; i < currentWeights.length; i++) {
            if (currentWeights[i] > capacities[i]) {
                allWithinCapacity = false;
            }
            totalUtilization += (double) currentWeights[i] / capacities[i];
        }
        if (allWithinCapacity) {
            System.out.println("Global Accuracy: 100%");
        } else {
            double accuracy = 0;
            for (int i = 0; i < currentWeights.length; i++) {
                accuracy += Math.min(1.0, (double) currentWeights[i] / capacities[i]);
            }
            accuracy = (accuracy / currentWeights.length) * 100;
            System.out.printf("Global Accuracy: %.2f%%\n", accuracy);
        }
    }
    
}
