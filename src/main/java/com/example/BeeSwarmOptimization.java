package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

class Item {
    int weight;
    int value;
    int id;

    public Item(int id, int weight, int value) {
        this.id = id;
        this.weight = weight;
        this.value = value;
    }
}

class Knapsack {
    int capacity;
    List<Integer> items;

    public Knapsack(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public boolean addItem(int itemIndex, int[][] objects) {
        if (this.getCurrentWeight(objects) + objects[itemIndex][1] <= capacity) {
            items.add(itemIndex);
            return true;
        }
        return false;
    }

    public int getCurrentWeight(int[][] objects) {
        return items.stream().mapToInt(i -> objects[i][1]).sum();
    }

    public int getCurrentValue(int[][] objects) {
        return items.stream().mapToInt(i -> objects[i][2]).sum();
    }

    public Set<Integer> getItemIds() {
        return new HashSet<>(items);
    }
}

public class BeeSwarmOptimization {
    private int[][] objects;
    private int[] capacities;
    private List<Knapsack> knapsacks;
    private Random random = new Random();
    private int flip;
    private int maxIter;
    private int numBees;
    private List<List<Knapsack>> tabooList = new ArrayList<>();

    public BeeSwarmOptimization(int[][] objects, int[] capacities, int flip, int maxIter, int numBees) {
        this.objects = objects;
        this.capacities = capacities;
        this.flip = flip;
        this.maxIter = maxIter;
        this.numBees = numBees;
        this.knapsacks = new ArrayList<>();
        for (int capacity : capacities) {
            knapsacks.add(new Knapsack(capacity));
        }
    }

    public List<Knapsack> solve() {
        List<Knapsack> sRef = beeInit();
        int iter = 0;
        int maxChances = 10;
        int nbChances = maxChances;
        int[] currentWeights = new int[knapsacks.size()];

        while (iter < maxIter && nbChances > 0) {
            tabooList.add(cloneKnapsacks(sRef));
            List<List<Knapsack>> searchPoints = generateSearchPoints(sRef);

            List<Knapsack> sBest = null;
            int bestValue = Integer.MIN_VALUE;

            for (List<Knapsack> searchPoint : searchPoints) {
                List<Knapsack> localSearchSolution = localSearch(searchPoint);
                int currentValue = evaluateSolution(localSearchSolution);
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    sBest = localSearchSolution;
                }
            }

            int deltaF = bestValue - evaluateSolution(sRef);
            if (deltaF > 0) {
                sRef = sBest;
                nbChances = maxChances;
            } else {
                nbChances--;
                if (nbChances == 0) {
                    sRef = selectForDiversity(sRef);
                    nbChances = maxChances;
                }
            }
            iter++;
        }

       
        return sRef;
    }

    public void updateCurrentWeights(List<Knapsack> solution, int[] currentWeights) {
        for (int i = 0; i < solution.size(); i++) {
            currentWeights[i] = solution.get(i).getCurrentWeight(objects);
        }
    }

    public void printAccuracy(int[] currentWeights) {
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

    private List<Knapsack> beeInit() {
        Collections.shuffle(Arrays.asList(objects));
        List<Knapsack> initialSolution = cloneKnapsacks(knapsacks);
        Set<Integer> remainingItems = new HashSet<>();
        for (int i = 0; i < objects.length; i++) {
            remainingItems.add(i);
        }

        for (Knapsack k : initialSolution) {
            Iterator<Integer> it = remainingItems.iterator();
            while (it.hasNext()) {
                int itemIndex = it.next();
                if (k.addItem(itemIndex, objects)) {
                    it.remove();
                }
            }
        }
        return initialSolution;
    }

    private List<List<Knapsack>> generateSearchPoints(List<Knapsack> sRef) {
        List<List<Knapsack>> searchPoints = new ArrayList<>();
        int h = 0;

        while (searchPoints.size() < numBees && h < flip) {
            List<Knapsack> s = cloneKnapsacks(sRef);
            int p = 0;
            do {
                s = invertItems(s, flip * p + h);
                p++;
            } while (flip * p + h < objects.length);
            searchPoints.add(s);
            h++;
        }
        return searchPoints;
    }

    private List<Knapsack> invertItems(List<Knapsack> solution, int index) {
     

        int knapsackIdx1 = random.nextInt(solution.size());
        int knapsackIdx2 = random.nextInt(solution.size());
        if (knapsackIdx1 != knapsackIdx2) {
            Knapsack k1 = solution.get(knapsackIdx1);
            Knapsack k2 = solution.get(knapsackIdx2);
            if (!k1.items.isEmpty()) {
                int itemIndex = k1.items.get(index % k1.items.size());
                k1.items.remove(Integer.valueOf(itemIndex)); // Remove item from k1
                k2.addItem(itemIndex, objects); // Add item to k2
            }
        }
        return solution;
    }

    private List<Knapsack> localSearch(List<Knapsack> searchPoint) {
        List<Knapsack> localSolution = cloneKnapsacks(searchPoint);
        int idx1 = random.nextInt(localSolution.size());
        int idx2 = random.nextInt(localSolution.size());
        if (idx1 != idx2) {
            Knapsack k1 = localSolution.get(idx1);
            Knapsack k2 = localSolution.get(idx2);
            if (!k1.items.isEmpty()) {
                int itemIndex = k1.items.remove(random.nextInt(k1.items.size()));
                k2.addItem(itemIndex, objects);
            }
        }
        return localSolution;
    }

    private List<Knapsack> selectForDiversity(List<Knapsack> sRef) {
        int minDistance = Integer.MAX_VALUE;
        List<Knapsack> mostDiverse = null;

        for (List<Knapsack> solution : tabooList) {
            int distance = calculateHammingDistance(sRef, solution);
            if (distance < minDistance) {
                minDistance = distance;
                mostDiverse = solution;
            }
        }

        return mostDiverse != null ? mostDiverse : sRef;
    }

    private int calculateHammingDistance(List<Knapsack> s1, List<Knapsack> s2) {
        Set<Integer> s1Items = s1.stream().flatMap(k -> k.getItemIds().stream()).collect(Collectors.toSet());
        Set<Integer> s2Items = s2.stream().flatMap(k -> k.getItemIds().stream()).collect(Collectors.toSet());

        Set<Integer> union = new HashSet<>(s1Items);
        union.addAll(s2Items);
        Set<Integer> intersection = new HashSet<>(s1Items);
        intersection.retainAll(s2Items);

        return union.size() - intersection.size();
    }

    private int evaluateSolution(List<Knapsack> solution) {
        return solution.stream().mapToInt(k -> k.getCurrentValue(objects)).sum();
    }

    private List<Knapsack> cloneKnapsacks(List<Knapsack> original) {
        List<Knapsack> clone = new ArrayList<>();
        for (Knapsack k : original) {
            Knapsack newK = new Knapsack(k.capacity);
            newK.items.addAll(k.items);
            clone.add(newK);
        }
        return clone;
    }
    public static void main(String[] args) {
        int[][] objects;
        int[] capacities;
    
        ReadData solverManager = new ReadData();
        try {
            solverManager.loadFromCsv("C:/Users/nadir/OneDrive/Bureau/demo/instance_50_20.csv");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        capacities = solverManager.getCapacities();
        objects = solverManager.getObjects();
    
        // Plage de valeurs pour les paramètres
        int[] flipValues = {1,2,3, 5,6,7,8, 10,20,30,60,100,150,200};
        int[] numBeesValues = {5,8, 10, 20,15,25,50,100,500,1000,10000};
        int[] maxIterValues = {5,10,20,50, 100, 200,500,800,1000,5000,10000};
    
        // Paramètres fixes
        int fixedFlip = 10;
        int fixedNumBees = 50;
        int fixedMaxIter = 500;
    
      // Varying flip
      System.out.println("variation of Flip");
        for (int flip : flipValues) {
            runBSO(objects, capacities, flip, fixedMaxIter, fixedNumBees);
        }
    
        // Varying numBees
       System.out.println("'variation of bees'");
        for (int numBees : numBeesValues) {
            runBSO(objects, capacities, fixedFlip, fixedMaxIter, numBees);
        }
  
         //Varying maxIter
         System.out.println("variation of iTERATIONS");
        for (int maxIter : maxIterValues) {
            runBSO(objects, capacities, fixedFlip, maxIter, fixedNumBees);
        }
    }
    
    public static void runBSO(int[][] objects, int[] capacities, int flip, int maxIter, int numBees) {
        long startTime = System.currentTimeMillis();
        BeeSwarmOptimization bso = new BeeSwarmOptimization(objects, capacities, flip, maxIter, numBees);
        List<Knapsack> result = bso.solve();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
    
        System.out.println("Parameters: flip = " + flip + ", numBees = " + numBees + ", maxIter = " + maxIter);
        System.out.println("Best Value: " + result.stream().mapToInt(k -> k.getCurrentValue(objects)).sum());
        System.out.println("Execution Time: " + executionTime + " ms");
    
        // Calculer et afficher l'accuracy globale
        int[] currentWeights = result.stream().mapToInt(k -> k.getCurrentWeight(objects)).toArray();
        int usedWeight = 0;
        int totalWeight = 0;
        for (int weight : currentWeights) {
            usedWeight += weight;
        }
    
        for (int capacity : capacities) {
            totalWeight += capacity;
        }
    
        if (totalWeight > 0) {
            double accuracy = ((double) usedWeight / totalWeight) * 100;
            System.out.println("Used weight: " + usedWeight + " | Total weight: " + totalWeight);
            System.out.printf("Global Accuracy: %.2f%%\n", accuracy);
        } else {
            System.out.println("Total weight is zero, cannot compute accuracy.");
        }
        System.out.println();
    }
    
    
}

