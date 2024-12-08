package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadData {
    private int[][] objects;
    private int[] capacities;

    // Loads objects and capacities from a CSV file
    public void loadFromCsv(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingObjects = false;
            int objCount = 0;
            int capCount = 0;

            // First pass to count objects and capacities
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                switch (line) {
                    case "Capacites":
                        readingObjects = false;
                        break;
                    case "ID,Poids,Valeur":
                        readingObjects = true;
                        break;
                    default:
                        if (readingObjects) objCount++;
                        else capCount++;
                        break;
                }
            }

            objects = new int[objCount][3];
            capacities = new int[capCount];

            // Second pass to load data
            br.close();
            try (BufferedReader br2 = new BufferedReader(new FileReader(filename))) {
                int objIndex = 0;
                int capIndex = 0;
                readingObjects = false;
                while ((line = br2.readLine()) != null) {
                    if (line.isEmpty() || line.equals("ID,Poids,Valeur") || line.equals("Capacites")) {
                        readingObjects = line.equals("ID,Poids,Valeur");
                        continue;
                    }

                    String[] parts = line.split(",");
                    if (readingObjects) {
                        objects[objIndex][0] = objIndex + 1; // Add ID as the first column
                        objects[objIndex][1] = Integer.parseInt(parts[1]); // Weight
                        objects[objIndex][2] = Integer.parseInt(parts[2]); // Value
                        objIndex++;
                    } else {
                        capacities[capIndex] = Integer.parseInt(parts[0]);
                        capIndex++;
                    }
                }
            }
        }
    }

    public int[] getCapacities() {
        return capacities;
    }

    public int[][] getObjects() {
        return objects;
    }

    public static void main(String[] args) {
        ReadData readData = new ReadData();
        try {
            readData.loadFromCsv("data.csv");
            int[] capacities = readData.getCapacities();
            int[][] objects = readData.getObjects();

            System.out.println("Capacities:");
            for (int capacity : capacities) {
                System.out.println(capacity);
            }

            System.out.println("Objects:");
            for (int[] obj : objects) {
                System.out.println("ID: " + obj[0] + ", Weight: " + obj[1] + ", Value: " + obj[2]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
