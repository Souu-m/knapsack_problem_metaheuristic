package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Controller implements Initializable {

   
    @FXML
    private Button btnupload, btnDfs, btnBfs;
    
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Label afterupload;

    private String csvFilePath;
    @FXML
    private Label initialLabel;
    @FXML
    private Label secondInitialLabel;
    @FXML
    private Label solutionLabel;
    @FXML
    private Label statisticsLabel;
    @FXML
    private VBox statsiticsPanel;
    @FXML
    private BorderPane borderPaneVar;
    @FXML
    private Label totalValuelabel;
    @FXML
    private Label generatedNodeslabel;
    @FXML
    private Label timeExecutionLabel;
    @FXML
    private Label solutionsFondlabel;
    @FXML
    private VBox solutionsFoundVbox;
    @FXML
    private TextField beesfeild;
    @FXML
    private TextField flipfield;
    @FXML
    private TextField iterfield;
    @FXML
    private TextField mutationfeild;
    @FXML
    private TextField iteragafeild;
    @FXML
    private TextField popfeild;



    int[][] objects;
    int[] capacities;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void handleClicks(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnupload) {

            uploadFile();
         
        }
        

        if (actionEvent.getSource() == btnDfs) {
            initialLabel.setVisible(false);
            secondInitialLabel.setVisible(false);
            ;

            statisticsLabel.setVisible(true);
            statsiticsPanel.setVisible(true);
            borderPaneVar.setVisible(true);
            solutionLabel.setVisible(true);
            solutionLabel.setText("Solution BSO");
            // Create the root node
            TreeItem<String> rootItem = new TreeItem<>("Knapsacks");
            treeView.setRoot(rootItem);
            int flip = Integer.parseInt(flipfield.getText());
            int maxIter = Integer.parseInt(iterfield.getText());
            int numBees = Integer.parseInt(beesfeild.getText());      
            System.out.println("flip"+ flip);
            long startTime = System.currentTimeMillis();      
            BeeSwarmOptimization bso = new BeeSwarmOptimization(objects, capacities, flip, maxIter, numBees);
            List<Knapsack> result = bso.solve();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Set up the TreeView
            rootItem = new TreeItem<>("Knapsacks");
            treeView.setRoot(rootItem);

            int knapsackCount = capacities.length;

            // Create parent nodes for each knapsack
            TreeItem<String>[] knapsackNodes = new TreeItem[knapsackCount];
            for (int i = 0; i < knapsackCount; i++) {
                knapsackNodes[i] = new TreeItem<>("Knapsack " + (i + 1) + " ( current weight="
                        + result.get(i).getCurrentWeight(objects) + "/ total weight=" + capacities[i] + ") ");
                rootItem.getChildren().add(knapsackNodes[i]);
            }

            // Add items to each knapsack node
            for (int i = 0; i < knapsackCount; i++) {
                Knapsack knapsack = result.get(i);
                for (int itemIndex : knapsack.items) {
                    TreeItem<String> itemNode = new TreeItem<>(
                        "Object " + (objects[itemIndex][0]) + " (Weight: " + objects[itemIndex][1] + ") (Value: " + objects[itemIndex][2] + ")");
                    knapsackNodes[i].getChildren().add(itemNode); // Add object to the i-th knapsack node
                }
            }

            treeView.setShowRoot(false); // O
            timeExecutionLabel.setText( executionTime+ "s ");
            totalValuelabel.setText(result.stream().mapToInt(k -> k.getCurrentValue(objects)).sum() + " ");
        
           // solutionsFondlabel.setText(result.getSolutionsFound() + "");
            //generatedNodeslabel.setText(result.getNodesGenerated() + "");
            //totalValuelabel.setText(result.getBestValue() + " ");
            /********************************************************** */
        }
        if (actionEvent.getSource() == btnBfs) {
            initialLabel.setVisible(false);
            secondInitialLabel.setVisible(false);
            
            statisticsLabel.setVisible(true);
            statsiticsPanel.setVisible(true);
            borderPaneVar.setVisible(true);
            solutionLabel.setVisible(true);
            solutionLabel.setText("Solution GA");
            
            // Create the root node
            TreeItem<String> rootItem = new TreeItem<>("Knapsacks");
            treeView.setRoot(rootItem);
            
            double mutation = Double.parseDouble(mutationfeild.getText());
            int maxIterga = Integer.parseInt(iteragafeild.getText());
            int pop = Integer.parseInt(popfeild.getText());
            
            long startTime = System.currentTimeMillis();
            GA solver = new GA(objects, capacities, pop, maxIterga, mutation);
            List<Integer> result = solver.solve();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Set up the TreeView
            rootItem = new TreeItem<>("Knapsacks");
            treeView.setRoot(rootItem);
            
            int knapsackCount = capacities.length;
            
            // Create parent nodes for each knapsack
            TreeItem<String>[] knapsackNodes = new TreeItem[knapsackCount];
            for (int i = 0; i < knapsackCount; i++) {
                knapsackNodes[i] = new TreeItem<>("Knapsack " + (i + 1));
                rootItem.getChildren().add(knapsackNodes[i]);
            }
            
            int[] currentWeights = new int[knapsackCount];
            
            // Add items to each knapsack node
            for (int j = 0; j < result.size(); j++) {
                int knapsackIndex = result.get(j);
                if (knapsackIndex != -1) {
                    currentWeights[knapsackIndex] += objects[j][1];
                    TreeItem<String> itemNode = new TreeItem<>(
                        "Object " + (objects[j][0]) + " (Weight: " + objects[j][1] + ") (Value: " + objects[j][2] + ")");
                    knapsackNodes[knapsackIndex].getChildren().add(itemNode);
                    
                }
            }
            
            // Display current weights and best value
            for (int i = 0; i < knapsackCount; i++) {
                knapsackNodes[i].setValue("Knapsack " + (i + 1) + " (current Weight: " + currentWeights[i]  + "/ total weight=" + capacities[i] + ") ");
            }
            
          //  bestValueLabel.setText("Best Value: " + solver.getBestValue());
            //executionTimeLabel.setText("Execution Time: " + executionTime + " ms");
            

            treeView.setShowRoot(false); // O
            timeExecutionLabel.setText( executionTime+ "s ");
            totalValuelabel.setText(solver.getBestValue()+ " ");
           // solutionsFondlabel.setText(result.getSolutionsFound() + "");
            //generatedNodeslabel.setText(result.getNodesGenerated() + "");
            //totalValuelabel.setText(result.getBestValue() + " ");
            /********************************************************** */
             
        }
 
    }

    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        // Set extension filter, for example, for CSV files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null); // Assuming you are calling within a JavaFX Application
                                                              // Thread

        if (selectedFile != null && selectedFile.exists() && selectedFile.isFile()) {

            // treeView.setVisible(true);
            // afterupload.setVisible(true);
        
       
            csvFilePath = selectedFile.getAbsolutePath(); // Store the file path for later use
            ReadData solverManager = new ReadData();
            System.out.println("File selected: " + csvFilePath);
            try {
                solverManager.loadFromCsv(csvFilePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            capacities = solverManager.getCapacities();
            objects = solverManager.getObjects();
            // You can add more logic here if needed, like reading the file to check its
            // content

            // Notify the user that the file has been successfully uploaded
            showAlert("Success", "File uploaded successfully!", Alert.AlertType.INFORMATION);
        } else {
            // Notify the user that the file selection was unsuccessful
            showAlert("Error", "Failed to upload the file.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
