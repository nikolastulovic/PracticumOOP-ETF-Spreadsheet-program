package application;
	
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import greske.*;
import sveKlase.*;

public class Main extends Application {
	
	private static int NUM_ROWS = 50;
    private static int NUM_COLUMNS = 27;
    private Table table=Table.getInstance();
    private GridPane gridPane;
    private Stage primaryStage;
    private boolean keyPressed = false;
    
    // Variables for column resizing
    private double initialClickX;
    private double initialColumnWidth;
    private int columnIndex;

    @Override
    public void start(Stage primaryStage) {
    	
    	this.primaryStage = primaryStage;
    	
    	gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        // setting the table
        setTable();

        // Create the menu bar and add the menus
        MenuBar menuBar = createMenuBar();
        
        // Create a ScrollPane and set the gridPane as its content
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        // Create the root layout and set the menu bar at the top
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(scrollPane);
        
       
        // Create the scene and set it in the primary stage
        Scene scene = new Scene(root, 1500, 810);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MyExcel");
        
        // Fix the width of the window
        primaryStage.setMinWidth(1500);
        primaryStage.setMaxWidth(1500);
        
        // Set the close request event handler
        primaryStage.setOnCloseRequest(this::handleCloseRequest);
        
        
        primaryStage.show();
    }
    
    //changing width of columns
    
    private void handleColumnResizeStart(MouseEvent event) {
        initialClickX = event.getX();
        TextField textField = (TextField) event.getSource();
        columnIndex = GridPane.getColumnIndex(textField);
        initialColumnWidth = textField.getWidth();

        // Adjust the width of all cells in the column
        List<Node> cells = new ArrayList<>();
        for (Node node : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex != null && colIndex == columnIndex) {
                cells.add(node);
            }
        }
        for (Node cell : cells) {
            TextField cellTextField = (TextField) cell;
            cellTextField.setPrefWidth(initialColumnWidth);
        }
    }
    
    private void handleColumnResize(MouseEvent event) {
        double deltaX = event.getX() - initialClickX;
        double newWidth = initialColumnWidth + deltaX;
        TextField textField = (TextField) event.getSource();
        textField.setPrefWidth(newWidth);

        // Adjust the width of all cells in the column
        List<Node> cells = new ArrayList<>();
        for (Node node : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex != null && colIndex == columnIndex) {
                cells.add(node);
            }
        }
        for (Node cell : cells) {
            TextField cellTextField = (TextField) cell;
            cellTextField.setPrefWidth(newWidth);
        }
    }
    
    private void handleCloseRequest(WindowEvent event) {
        event.consume(); // Consume the event to prevent the window from closing immediately

        // Create the popup window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

        Label label = new Label("Da li zelite da zavrsite rad?");
        Button yesButton = new Button("Da");
        Button noButton = new Button("Ne");

        yesButton.setOnAction(e -> {
        	
        	//checking if its needed to save
        	if(!table.isSaved()) saveWindow();
        	
        	// Close the main window
            primaryStage.close();
            popupStage.close();
        });

        noButton.setOnAction(e -> {
            // Close the popup window
            popupStage.close();
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(yesButton, noButton);
        
        VBox popupContent = new VBox(10);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.getChildren().addAll(label, buttonBox);

        Scene popupScene = new Scene(popupContent, 300, 150);
        popupStage.setScene(popupScene);
        popupStage.setTitle("Potvrda");
        popupStage.show();
    }

    
    //table setup, adding textfields and their values
    private void setTable()
    {
    	gridPane.getChildren().clear();
    	for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                TextField textField = new TextField();
                if(col==0) textField.setPrefWidth(60);
                else textField.setPrefWidth(80);
                if(col==0 || row==0) textField.setEditable(false);
                
                // Setting initial cell values
                if(col==0 && row>0) textField.setText(Integer.toString(row));
                else textField.setText(table.getCellDisplay(row, col));
                
                if (textField.getText().equals("ERROR")) {
                    textField.setStyle("-fx-text-fill: red;");
                } else {
                    textField.setStyle("-fx-text-fill: black;");
                }
                
                final int currentRow = row;
                final int currentCol = col;
                
                if(col!=0&& row!=0)
                {
                	textField.setOnMouseClicked(event -> {
                        // Change the value of the TextField when it is clicked
                		if (event.getButton() == MouseButton.PRIMARY) {
                			textField.setText(table.getCellValue(currentRow, currentCol));
                		}
                		else event.consume();
                    });
                	if(row!=0 && col!=0) 
                		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue && keyPressed) {
                        	//textField.setText(table.getCellValue(currentRow, currentCol));
                        	handleCellChange(currentRow, currentCol, textField.getText());
                        	textField.setText(table.getCellDisplay(currentRow, currentCol));
                        	if (textField.getText().equals("ERROR")) {
                                textField.setStyle("-fx-text-fill: red;");
                            } else {
                                textField.setStyle("-fx-text-fill: black;");
                            }
                        	
                        	keyPressed=false;
                        }
                        else if(!newValue) textField.setText(table.getCellDisplay(currentRow, currentCol));
                    });
                }
                textField.setOnKeyPressed(event -> {
                	keyPressed=true;
                });
                //right click
                textField.setOnContextMenuRequested(event -> {
                    int columnIndex = GridPane.getColumnIndex(textField);
                    int rowIndex = GridPane.getRowIndex(textField);
                    formatWindow(rowIndex,columnIndex);
                });

                // Create context menu
                ContextMenu contextMenu = new ContextMenu();
                MenuItem menuItem = new MenuItem("Some Action");
                contextMenu.getItems().add(menuItem);
                textField.setContextMenu(contextMenu);
                
                GridPane.setRowIndex(textField, row);
                GridPane.setColumnIndex(textField, col);
                gridPane.getChildren().add(textField);
               
                
                // Add event handler for column resizing
                if (col > 0 && row==0) {
                    textField.setOnMousePressed(this::handleColumnResizeStart);
                    textField.setOnMouseDragged(this::handleColumnResize);
                }
            }
        }
    }
    
    private void handleCellChange(int row, int col, String newValue) {
    	try {
    		table.changeValue(row, col, newValue);
			table.setSaved(false);
		} catch (Exception e) {
			showError(e.getMessage());
		} 
    }
    
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Greska");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    
    private void showHelp() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pomoc");
        alert.setHeaderText("Informacije o programu");
        alert.setContentText("Fajl->Novi - Kreiranje novog fajla \n"
        					+ "Fajl->Otvori - Otvaranje postojeceg fajla \n"
        					+ "Fajl->Sacuvaj - Cuvanje trenutnog fajla \n"
        					+ "Editovanje: Undo, Redo \n"
        					+ "Pritiskom i prevlacenjem zaglavlja kolone menja se sirina \n"
        					+ "Desni klik na celiju otvara opcije formatiranja");
        alert.showAndWait();
    }
    
    private void openWindow() {
    	
        Stage popOutStage = new Stage();
        popOutStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interactions with the main window

        Label label = new Label("Unesite ime fajla");
        label.setAlignment(Pos.CENTER); // Horizontally center the label

        TextField textField = new TextField();

        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            String file = textField.getText();
           	
            try {
				loadSaveProcedure(file,"load");
				popOutStage.close();
				setTable();
			} catch (Exception e) {
				showError(e.getMessage());
			} 
            
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER); // Horizontally center the contents of the VBox
        root.getChildren().addAll(label, textField, okButton);

        popOutStage.setScene(new Scene(root));
        popOutStage.setTitle("Open");
        popOutStage.showAndWait();
    }
    
    private void saveWindow()
    {
    	Stage popOutStage = new Stage();
        popOutStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interactions with the main window

        Label label = new Label("Da li zelite da sacuvate rad?");
        label.setAlignment(Pos.CENTER); // Horizontally center the label

        TextField textField = new TextField();

        Button okButton = new Button("Sacuvaj");
        okButton.setOnAction(event -> {
            String file = textField.getText();
           	
            try {
				loadSaveProcedure(file,"save");
				popOutStage.close();
				table.setSaved(true);
				setTable();
			} catch (Exception e) {
				showError(e.getMessage());
			} 
            
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER); // Horizontally center the contents of the VBox
        root.getChildren().addAll(label, textField, okButton);

        popOutStage.setScene(new Scene(root));
        popOutStage.setTitle("Open");
        popOutStage.showAndWait();
    }
    
    private void loadSaveProcedure(String file,String loadsave) throws GImeFajla, NumberFormatException, GNepoznatFormat, GNemaRedo, GNemaUndo, GNeodgovarajucaVrednost, GPostavljanjeDecimala, GNeuspeloOtvaranje, GNepoznataKomanda
    {
    	Pattern pattern = Pattern.compile("([^.]+)");
    	Matcher matcher = pattern.matcher(file);
    	
    	List<String> matches = new ArrayList<>();
    	while (matcher.find()) {
    	    matches.add(matcher.group());
    	}
    	if (!matches.isEmpty())
    	{
    		String s=matches.get(1);
    		if (matches.get(1).equals("csv"))
    		{
    			table.perform(loadsave + "-" + matches.get(0) + "-csv");
    		}
    		else if (matches.get(1).equals("json"))
    		{
    			table.perform(loadsave + "-" + matches.get(0) + "-json");
    		}
    		else throw new GImeFajla();
    	}
    	else throw new GImeFajla();
    }
   
    private void undoWindow()
    {
    	try {
			table.undo();
			setTable();
		} catch (GNemaUndo e) {
			showError(e.getMessage());
		}
		table.setSaved(false);
    }
    
    private void redoWindow()
    {
    	try {
			table.redo();
			setTable();
		} catch (GNemaRedo e) {
			showError(e.getMessage());
		}
		table.setSaved(false);
    }
    
    private void newWindow()
    {
    	saveWindow();
    	table.clearTable();
    	setTable();
    }
    
    private void formatWindow(int rowIndex, int columnIndex)
    {
    	Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

        VBox popupContent = new VBox();
        HBox radios=new HBox();
        popupContent.setSpacing(10);
        popupContent.setPadding(new Insets(10));
        
        Label brDec=new Label("Broj decimala:");

        ToggleGroup formatToggleGroup = new ToggleGroup();
        
        CheckBox checkBox = new CheckBox("Primeniti za celu kolonu");
        
        TextField valueTextField = new TextField();
        valueTextField.setDisable(true);
        
        RadioButton textRadioButton = new RadioButton("Text");
        textRadioButton.setToggleGroup(formatToggleGroup);
        textRadioButton.setOnAction(event -> valueTextField.setDisable(true));
        textRadioButton.setSelected(true);

        RadioButton numberRadioButton = new RadioButton("Number");
        numberRadioButton.setToggleGroup(formatToggleGroup);
        numberRadioButton.setOnAction(event -> valueTextField.setDisable(!numberRadioButton.isSelected()));

        RadioButton dateRadioButton = new RadioButton("Date");
        dateRadioButton.setOnAction(event -> valueTextField.setDisable(true));
        dateRadioButton.setToggleGroup(formatToggleGroup);
    
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            String selectedFormat = "";
            if (textRadioButton.isSelected()) {
                selectedFormat = "text";
            } else if (numberRadioButton.isSelected()) {
                selectedFormat = "number";
            } else if (dateRadioButton.isSelected()) {
                selectedFormat = "date";
            }

            String value = valueTextField.getText();
            if(value.isEmpty()) value="0";
            boolean wholeCol=checkBox.isSelected();
            
            // Change to selected format
            if(!wholeCol) table.changeFormat(rowIndex, columnIndex, selectedFormat);
            else table.changeFormat(-1, columnIndex, selectedFormat);
            
            if(selectedFormat.equals("number"))
            {
            	int decimals=Integer.parseInt(value);
            	try {
					if(!wholeCol) table.changeDecimals(rowIndex, columnIndex, decimals);
					else table.changeDecimals(-1, columnIndex, decimals);
				} catch (GPostavljanjeDecimala e) {
					showError(e.getMessage());
				}
            }
            
            setTable();
            
            popupStage.close();
        });
        radios.getChildren().addAll(textRadioButton, numberRadioButton, dateRadioButton);
        radios.setAlignment(Pos.CENTER);
        popupContent.getChildren().addAll(radios, brDec, valueTextField, checkBox, okButton);
        popupContent.setAlignment(Pos.CENTER);
        
        Scene popupScene = new Scene(popupContent, 250, 200);
        popupStage.setScene(popupScene);
        popupStage.setTitle("Format Selection");
        popupStage.showAndWait();
    }
    
    private MenuBar createMenuBar() {
        // Create the menus
        Menu fileMenu = new Menu("Fajl");
        Menu editMenu = new Menu("Editovanje");
        Menu helpMenu = new Menu("Pomoc");

        // Create the menu items
        MenuItem newMenuItem = new MenuItem("Novi");
        newMenuItem.setOnAction(event -> newWindow());
        MenuItem openMenuItem = new MenuItem("Otvori");
        openMenuItem.setOnAction(event -> openWindow());
        MenuItem saveMenuItem = new MenuItem("Sacuvaj");
        saveMenuItem.setOnAction(event -> saveWindow());

        MenuItem undoMenuItem = new MenuItem("Undo");
        undoMenuItem.setOnAction(event -> undoWindow());
        MenuItem redoMenuItem = new MenuItem("Redo");
        redoMenuItem.setOnAction(event -> redoWindow());
        
        MenuItem aboutMenuItem = new MenuItem("O programu");
        aboutMenuItem.setOnAction(event -> showHelp());
        
        // Add the menu items to the respective menus
        fileMenu.getItems().addAll(newMenuItem, openMenuItem, saveMenuItem);
        editMenu.getItems().addAll(undoMenuItem, redoMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);

        // Create the menu bar and add the menus
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        return menuBar;
    }
    
	public static void main(String[] args) {
		launch(args);
	}
}
