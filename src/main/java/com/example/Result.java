package com.example;



import java.util.List;

public class Result {
    private long executionTime;
    private double bestValue;
    private List<Integer> bestSolution;
    
    private int[] currentWeights; // Poids actuel de chaque sac � dos
    private int[] capacities; // Capacit� de chaque sac � dos

    public Result(long executionTime, double bestValue,  List<Integer> bestSolution, int[] currentWeights, int[] capacities) {
    	
    	this.executionTime = executionTime;
        this.bestValue = bestValue;
        this.bestSolution = bestSolution;
        
        this.currentWeights = currentWeights;
        this.capacities = capacities;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public double getBestValue() {
        return bestValue;
    }

    public List<Integer> getBestSolution() {
        return bestSolution;
    }

   
    public int[] getCurrentWeights() {
        return currentWeights;
    }

    public int[] getCapacities() {
        return capacities;
    }
    

}
