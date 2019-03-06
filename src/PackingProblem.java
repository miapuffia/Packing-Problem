package packingProblem.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import robertHelperFunctions.QuickAlert;

public class PackingProblem extends Application {
	ArrayList<TextRectangle> containers = new ArrayList<TextRectangle>();
	ArrayList<TextRectangle> packages = new ArrayList<TextRectangle>();
	ArrayList<StackPane> innerStackPanes = new ArrayList<StackPane>();
	ArrayList<StackPane> stackPanes = new ArrayList<StackPane>();
	ArrayList<ScrollPane> scrollPanes = new ArrayList<ScrollPane>();
	TabPane tabPane = new TabPane();
	double pixelsPerCell = 20;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		containers.add(new TextRectangle("Container 1"));
		
		MenuItem menuItemFileImport = new MenuItem("Import");
		MenuItem menuItemFileExport = new MenuItem("Export");
		
		Menu menuFile = new Menu("File");
		menuFile.getItems().addAll(menuItemFileImport, menuItemFileExport);
		
		MenuItem menuItemSetContainer = new MenuItem();
		
		Menu menuSetContainer = new Menu("Set container");
		menuSetContainer.getItems().addAll(menuItemSetContainer);
		menuSetContainer.addEventHandler(Menu.ON_SHOWN, event -> menuSetContainer.hide());
		menuSetContainer.addEventHandler(Menu.ON_SHOWING, event -> menuSetContainer.fire());
		
		MenuItem menuItemAddPackage = new MenuItem();
		
		Menu menuAddPackage = new Menu("Add package");
		menuAddPackage.getItems().addAll(menuItemAddPackage);
		menuAddPackage.addEventHandler(Menu.ON_SHOWN, event -> menuAddPackage.hide());
		menuAddPackage.addEventHandler(Menu.ON_SHOWING, event -> menuAddPackage.fire());
		menuAddPackage.setDisable(true);
		
		MenuItem menuItemViewPackage = new MenuItem();
		
		Menu menuViewPackage = new Menu("View package");
		menuViewPackage.getItems().addAll(menuItemViewPackage);
		menuViewPackage.addEventHandler(Menu.ON_SHOWN, event -> menuViewPackage.hide());
		menuViewPackage.addEventHandler(Menu.ON_SHOWING, event -> menuViewPackage.fire());
		menuViewPackage.setDisable(true);
		
		MenuItem menuItemSetCellSize = new MenuItem();
		
		Menu menuSetCellSize = new Menu("Set cell size");
		menuSetCellSize.getItems().addAll(menuItemSetCellSize);
		menuSetCellSize.addEventHandler(Menu.ON_SHOWN, event -> menuSetCellSize.hide());
		menuSetCellSize.addEventHandler(Menu.ON_SHOWING, event -> menuSetCellSize.fire());
		
		MenuItem menuItemDraw = new MenuItem();
		
		Menu menuDraw = new Menu("Redraw");
		menuDraw.getItems().addAll(menuItemDraw);
		menuDraw.addEventHandler(Menu.ON_SHOWN, event -> menuDraw.hide());
		menuDraw.addEventHandler(Menu.ON_SHOWING, event -> menuDraw.fire());
		menuDraw.setDisable(true);
		
		menuItemFileImport.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Import data");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
			
			File selectedFile = fileChooser.showOpenDialog(primaryStage);
			
			if(selectedFile != null) {
				try(BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
					packages.clear();
					
					String line;
					int counter = 0;
					
					while((line = reader.readLine()) != null) {
						if(!line.contains(":")) {
							QuickAlert.show(AlertType.ERROR, "Currupt import", "The imported file is corrupted or in an incorrect format.");
							return;
						}
						
						String lineHeader = line.substring(0, line.indexOf(':'));
						
						switch(lineHeader) {
							case "containerWidth":
								int newContainerWidth = 0;
								
								try {
									newContainerWidth = Integer.parseInt(line.substring(line.indexOf(':') + 1));
								} catch(NumberFormatException | NullPointerException e2) {
									return;
								}
								
								containers.get(0).setCellWidth(newContainerWidth);
								break;
							case "containerHeight":
								int newHeight = 0;
								
								try {
									newHeight = Integer.parseInt(line.substring(line.indexOf(':') + 1));
								} catch(NumberFormatException | NullPointerException e2) {
									return;
								}
								
								containers.get(0).setCellHeight(newHeight);
								break;
							case "packageName":
								counter++;
								
								String newName = line.substring(line.indexOf(':') + 1);
								
								if(newName.equals("") || newName.isEmpty() || newName == null) {
									newName = "Package " + counter;
								}
								
								packages.add(new TextRectangle(newName));
								break;
							case "packageWidth":
								int newPackageWidth = 0;
								
								try {
									newPackageWidth = Integer.parseInt(line.substring(line.indexOf(':') + 1));
								} catch(NumberFormatException | NullPointerException e2) {
									return;
								}
								
								if(newPackageWidth <= containers.get(0).getCellWidth()) {
									packages.get(packages.size() - 1).setCellWidth(newPackageWidth);
								} else {
									packages.get(packages.size() - 1).setCellWidth(0);
								}
								
								break;
							case "packageHeight":
								int newPackageHeight = 0;
								
								try {
									newPackageHeight = Integer.parseInt(line.substring(line.indexOf(':') + 1));
								} catch(NumberFormatException | NullPointerException e2) {
									return;
								}
								
								if(newPackageHeight <= containers.get(0).getCellHeight()) {
									packages.get(packages.size() - 1).setCellHeight(newPackageHeight);
								} else {
									packages.get(packages.size() - 1).setCellHeight(0);
								}
								
								break;
							case "packageColor":
								Color newColor;
								
								try {
									newColor = Color.valueOf(line.substring(line.indexOf(':') + 1));
								} catch(IllegalArgumentException | NullPointerException iae) {
									System.out.println(line.substring(line.indexOf(':') + 1));
									newColor = Color.hsb(Math.random() * 360, ((Math.random() * 0.9) + 0.1), ((Math.random() * 0.4) + 0.6));
								}
								
								packages.get(packages.size() - 1).setCellBackgroundColor(newColor);
								break;
							default:
								System.out.println(lineHeader);
								packages.clear();
								return;
						}
					}
					
					reader.close();
					
					containers.get(0).setCellBackgroundColor(Color.LIGHTBLUE);
					
					menuAddPackage.setDisable(false);
					menuViewPackage.setDisable(false);
					menuDraw.setDisable(false);
					
					boolean badPackagesFound = false;
					
					for(int i = packages.size() - 1; i >= 0; i--) {
						if(packages.get(i).getCellWidth() == 0 || packages.get(i).getCellHeight() == 0) {
							badPackagesFound = true;
							packages.remove(i);
						}
					}
					
					if(badPackagesFound) {
						QuickAlert.show(AlertType.ERROR, "Some errors occurred", "Some packages were not imported because of incorrect data possibly.");
					} else {
						QuickAlert.show(AlertType.INFORMATION, "Import successful", "Data was imported successfully.");
					}
					
					solve();
				} catch(IOException e1) {
					return;
				}
			}
		});
		
		menuItemFileExport.setOnAction(e -> {
			if(containers.get(0).isSet()) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Export data");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
				
				File selectedFile = fileChooser.showSaveDialog(primaryStage);
				
				try {
					FileOutputStream fos = new FileOutputStream(selectedFile);
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					
					bw.write("containerWidth:" + containers.get(0).getCellWidth());
					bw.newLine();
					bw.write("containerHeight:" + containers.get(0).getCellHeight());
					bw.newLine();
					
					Color packageColor;
					
					for(int i = 0; i < packages.size(); i++) {
						bw.write("packageName:" + packages.get(i).getName());
						bw.newLine();
						bw.write("packageWidth:" + packages.get(i).getCellWidth());
						bw.newLine();
						bw.write("packageHeight:" + packages.get(i).getCellHeight());
						bw.newLine();
						
						packageColor = packages.get(i).getCellBackgroundColor();
						
						bw.write("packageColor:" +
								String.format(
									"#%02X%02X%02X",
									(int) (packageColor.getRed() * 255),
									(int) (packageColor.getGreen() * 255),
									(int) (packageColor.getBlue() * 255)
								)
						);
						bw.newLine();
					};
					
					bw.close();
					
					QuickAlert.show(AlertType.INFORMATION, "Export successful", "Data exported successfully.");
				} catch(NullPointerException | IOException e2) {
					return;
				}
			}
		});
		
		menuSetContainer.setOnAction(e -> {
			TextRectangle packageObject = createContainer(containers.get(0));
			
			if(packageObject.isSet()) {
				containers.set(0, packageObject);
				
				menuAddPackage.setDisable(false);
				menuViewPackage.setDisable(false);
				menuDraw.setDisable(false);
				
				solve();
			} else {
				QuickAlert.show(AlertType.ERROR, "Container missing information", "The container has not been set because some information was not provided.");
			}
		});
		
		menuAddPackage.setOnAction(e -> {
			TextRectangle packageObject = new TextRectangle();
			
			packageObject = createPackage("Add package", "Package", packageObject);
			
			if(packageObject.isSet()) {
				if(packageObject.getCellWidth() <= containers.get(0).getCellWidth() && packageObject.getCellHeight() <= containers.get(0).getCellHeight()) {
					packages.add(packageObject);
					
					solve();
				} else {
					QuickAlert.show(AlertType.ERROR, "Package is larger than the container", "The package has not been added because it is larger than the container.");
				}
			}
		});
		
		menuViewPackage.setOnAction(e -> {
			ListView<TextRectangle> packagesListView = new ListView<TextRectangle>();
			packagesListView.setItems(FXCollections.observableArrayList(packages));
			packagesListView.setPrefSize(200, 400);
			packagesListView.setOrientation(Orientation.VERTICAL);
			
			packagesListView.setCellFactory(new Callback<ListView<TextRectangle>, ListCell<TextRectangle>>() {
				@Override
				public ListCell<TextRectangle> call(ListView<TextRectangle> p) {
					return new ListCell<TextRectangle>() {
						private final Label nameLabel;
						private final StackPane nameStackPane;
						
						{
							setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
							nameLabel = new Label();
							nameStackPane = new StackPane(nameLabel);
							StackPane.setAlignment(nameLabel, Pos.CENTER_LEFT);
							StackPane.setMargin(nameLabel, new Insets(0, 5, 0, 5));
						}
						
						@Override
						protected void updateItem(TextRectangle item, boolean empty) {
							super.updateItem(item, empty);
							
							if(item == null || empty) {
								setGraphic(null);
							} else {
								nameLabel.setText(item.getName());
								nameStackPane.setBackground(new Background(new BackgroundFill(item.getCellBackgroundColor(), CornerRadii.EMPTY, Insets.EMPTY)));
								setGraphic(nameStackPane);
							}
						}
					};
				}
				
			});
			
			Button editPackage = new Button("Edit");
			editPackage.setDisable(true);
			
			editPackage.setOnAction(e2 -> {
				TextRectangle packageObject = packages.get(packagesListView.getSelectionModel().getSelectedIndex());
				
				packageObject = createPackage("Edit package", "Package", packageObject);
				
				if(packageObject.isSet()) {
					if(packageObject.getCellWidth() <= containers.get(0).getCellWidth() && packageObject.getCellHeight() <= containers.get(0).getCellHeight()) {
						packages.set(packagesListView.getSelectionModel().getSelectedIndex(), packageObject);
						
						packagesListView.setItems(FXCollections.observableArrayList(packages));
						
						solve();
					} else {
						QuickAlert.show(AlertType.ERROR, "Package is larger than the container", "The package has not been changed because the new size is larger than the container.");
					}
				} else {
					QuickAlert.show(AlertType.ERROR, "Package massing information", "The package has not been changed because some replacement information was not provided.");
				}
			});
			
			Button deletePackage = new Button("Delete");
			deletePackage.setDisable(true);
			
			deletePackage.setOnAction(e2 -> {
				packages.remove(packagesListView.getSelectionModel().getSelectedIndex());
				
				packagesListView.setItems(FXCollections.observableArrayList(packages));
				
				solve();
			});
			
			packagesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TextRectangle>() {
				public void changed(ObservableValue<? extends TextRectangle> ov, final TextRectangle oldvalue, final TextRectangle newvalue) {
					if(newvalue == null) {
						editPackage.setDisable(true);
						deletePackage.setDisable(true);
					} else if(newvalue.isSet()) {
						editPackage.setDisable(false);
						deletePackage.setDisable(false);
					}
				}
			});
			
			HBox packageHBox = new HBox(10, packagesListView, editPackage, deletePackage);
			
			ButtonType doneButton = new ButtonType("Done");
			
			Dialog<Void> viewPackagesDialog = new Dialog<>();
			viewPackagesDialog.setTitle("View packages");
			viewPackagesDialog.getDialogPane().getButtonTypes().addAll(doneButton);
			viewPackagesDialog.getDialogPane().setContent(packageHBox);
			viewPackagesDialog.showAndWait();
		});
		
		menuSetCellSize.setOnAction(e -> {
			Dialog<String> setCellSizeDialog = new TextInputDialog("" + pixelsPerCell);
			setCellSizeDialog.setTitle("Set grid cell size in pixels");
			setCellSizeDialog.setHeaderText("Set grid cell size in pixels:");
			Optional<String> result = setCellSizeDialog.showAndWait();
			
			if(result.isPresent()) {
				try { 
					Double.parseDouble(result.get());
				} catch(NumberFormatException | NullPointerException e2) {
					return;
				}
				
				pixelsPerCell = Double.parseDouble(result.get());
				
				menuDraw.fire();
			}
		});
		
		menuDraw.setOnAction(e -> {
			containers.forEach((n) -> n.pixelize(pixelsPerCell));
			
			packages.forEach((n) -> n.pixelize(pixelsPerCell));
			
			solve();
		});
		
		MenuBar menuBar = new MenuBar(menuFile, menuSetContainer, menuAddPackage, menuViewPackage, menuDraw, menuSetCellSize);
		
		VBox mainVBox = new VBox(menuBar, tabPane);
		VBox.setVgrow(tabPane, Priority.ALWAYS);
		
		Scene scene = new Scene(mainVBox);
		
		scene.getStylesheets().add("/packingProblem/resources/scrollBar.css");
		
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("Packing Problem");
		primaryStage.show();
	}
	
	private TextRectangle createContainer(final TextRectangle shape) {
		Label shapeWidthLabel = new Label("Container width:");
		TextField shapeWidthTextField = new TextField();
		shapeWidthTextField.setPrefWidth(50);
		Label unitsLabel1 = new Label("units");
		
		Label shapeHeightLabel = new Label("Container height:");
		TextField shapeHeightTextField = new TextField();
		shapeHeightTextField.setPrefWidth(50);
		Label unitsLabel2 = new Label("units");
		
		if(shape.isSet()) {
			shapeWidthTextField.setText("" + shape.getCellWidth());
			shapeHeightTextField.setText("" + shape.getCellHeight());
		}
		
		GridPane shapeInputsGridPane = new GridPane();
		shapeInputsGridPane.setHgap(10);
		shapeInputsGridPane.setVgap(10);
		shapeInputsGridPane.add(shapeWidthLabel, 0, 0);
		shapeInputsGridPane.add(shapeWidthTextField, 1, 0);
		shapeInputsGridPane.add(unitsLabel1, 2, 0);
		shapeInputsGridPane.add(shapeHeightLabel, 0, 1);
		shapeInputsGridPane.add(shapeHeightTextField, 1, 1);
		shapeInputsGridPane.add(unitsLabel2, 2, 1);
		
		ButtonType createContainerButton = new ButtonType("Set container", ButtonData.OK_DONE);
		
		Dialog<TextRectangle> createContainerDialog = new Dialog<>();
		createContainerDialog.setTitle("Set container");
		createContainerDialog.getDialogPane().getButtonTypes().addAll(createContainerButton);
		createContainerDialog.getDialogPane().setContent(shapeInputsGridPane);
		
		createContainerDialog.setResultConverter(new Callback<ButtonType, TextRectangle>() {
			@Override
			public TextRectangle call(ButtonType b) {
				if(b == createContainerButton) {
					try { 
						Integer.parseInt(shapeWidthTextField.getText()); 
					} catch(NumberFormatException | NullPointerException e) {
						return shape;
					}
					
					try { 
						Integer.parseInt(shapeHeightTextField.getText()); 
					} catch(NumberFormatException | NullPointerException e) {
						return shape;
					}
					
					shape.setCellWidth(Integer.parseInt(shapeWidthTextField.getText()));
					shape.setCellHeight(Integer.parseInt(shapeHeightTextField.getText()));
					shape.setCellBackgroundColor(Color.LIGHTBLUE);
					shape.pixelize(pixelsPerCell);
				}
				
				return shape;
			}
		});
		
		Platform.runLater(() -> shapeWidthTextField.requestFocus());
		
		Optional<TextRectangle> result = createContainerDialog.showAndWait();
		
		if(result.isPresent()) {
			return result.get();
		}
		
		return shape;
	}
	
	private TextRectangle createPackage(String title, String type, final TextRectangle shape) {
		Label shapeNameLabel = new Label(type + " name:");
		TextField shapeNameTextField = new TextField(shape.getName());
		shapeNameTextField.setPrefWidth(50);
		
		Label shapeWidthLabel = new Label(type + " width:");
		TextField shapeWidthTextField = new TextField();
		shapeWidthTextField.setPrefWidth(50);
		Label unitsLabel1 = new Label("units");
		
		Label shapeHeightLabel = new Label(type + " height:");
		TextField shapeHeightTextField = new TextField();
		shapeHeightTextField.setPrefWidth(50);
		Label unitsLabel2 = new Label("units");
		
		ColorPicker colorPicker;
		
		if(shape.isSet()) {
			shapeWidthTextField.setText("" + shape.getCellWidth());
			shapeHeightTextField.setText("" + shape.getCellHeight());
			colorPicker = new ColorPicker(shape.getCellBackgroundColor());
		} else {
			colorPicker = new ColorPicker(Color.hsb(Math.random() * 360, ((Math.random() * 0.9) + 0.1), ((Math.random() * 0.4) + 0.6)));
		}
		
		GridPane shapeInputsGridPane = new GridPane();
		shapeInputsGridPane.setHgap(10);
		shapeInputsGridPane.setVgap(10);
		shapeInputsGridPane.add(shapeNameLabel, 0, 0);
		shapeInputsGridPane.add(shapeNameTextField, 1, 0, 2, 1);
		shapeInputsGridPane.add(shapeWidthLabel, 0, 1);
		shapeInputsGridPane.add(shapeWidthTextField, 1, 1);
		shapeInputsGridPane.add(unitsLabel1, 2, 1);
		shapeInputsGridPane.add(shapeHeightLabel, 0, 2);
		shapeInputsGridPane.add(shapeHeightTextField, 1, 2);
		shapeInputsGridPane.add(unitsLabel2, 2, 2);
		shapeInputsGridPane.add(colorPicker, 1, 3);
		
		ButtonType createPackageButton = new ButtonType(title, ButtonData.OK_DONE);
		
		Dialog<TextRectangle> createPackageDialog = new Dialog<>();
		createPackageDialog.setTitle(title);
		createPackageDialog.getDialogPane().getButtonTypes().addAll(createPackageButton);
		createPackageDialog.getDialogPane().setContent(shapeInputsGridPane);
		
		createPackageDialog.setResultConverter(new Callback<ButtonType, TextRectangle>() {
			@Override
			public TextRectangle call(ButtonType b) {
				if(b == createPackageButton) {
					try { 
						Integer.parseInt(shapeWidthTextField.getText()); 
					} catch(NumberFormatException | NullPointerException e) {
						return shape;
					}
					
					try { 
						Integer.parseInt(shapeHeightTextField.getText()); 
					} catch(NumberFormatException | NullPointerException e) {
						return shape;
					}
					
					shape.setName(shapeNameTextField.getText());
					shape.setCellWidth(Integer.parseInt(shapeWidthTextField.getText()));
					shape.setCellHeight(Integer.parseInt(shapeHeightTextField.getText()));
					shape.setCellBackgroundColor(colorPicker.getValue());
					shape.pixelize(pixelsPerCell);
				}
				
				return shape;
			}
		});
		
		Platform.runLater(() -> shapeNameTextField.requestFocus());
		
		Optional<TextRectangle> result = createPackageDialog.showAndWait();
		
		if(result.isPresent()) {
			return result.get();
		}
		
		return shape;
	}
	
	private void newTab() {
		StackPane innerStackPane = new StackPane();
		innerStackPane.setMaxHeight(containers.get(0).getRectangle().getHeight());
		innerStackPane.setMaxWidth(containers.get(0).getRectangle().getWidth());
		
		innerStackPanes.add(innerStackPane);
		
		StackPane stackPane = new StackPane(innerStackPane);
		
		stackPanes.add(stackPane);
		
		ScrollPane scrollPane = new ScrollPane(stackPane);
		scrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setHmax(pixelsPerCell * 100);
		scrollPane.setVmax(pixelsPerCell * 100);
		scrollPane.setHvalue(scrollPane.getHmax() / 2);
		scrollPane.setVvalue(scrollPane.getVmax() / 2);
		
		scrollPanes.add(scrollPane);
		
		stackPane.setMinWidth(scrollPane.getHmax());
		stackPane.setMinHeight(scrollPane.getVmax());
		
		Tab tab = new Tab();
		tab.setClosable(false);
		tab.setText("Container " + (tabPane.getTabs().size() + 1));
		tab.setContent(scrollPanes.get(tabPane.getTabs().size()));
		
		if(containers.size() <= tabPane.getTabs().size()) {
			TextRectangle containerObject = new TextRectangle(containers.get(0).getCellWidth(), containers.get(0).getCellHeight(), "Container " + (tabPane.getTabs().size() + 1));
			containerObject.setCellBackgroundColor(containers.get(0).getCellBackgroundColor());
			containerObject.pixelize(pixelsPerCell);
			
			containers.add(containerObject);
		}
		
		innerStackPanes.get(tabPane.getTabs().size()).getChildren().add(containers.get(tabPane.getTabs().size()));
		
		tabPane.getTabs().add(tab);
	}
	
	private void solve() {
		tabPane.getTabs().clear();
		innerStackPanes.clear();
		stackPanes.clear();
		scrollPanes.clear();
		containers.subList(1, containers.size()).clear();
		Matrix matrix = new Matrix(containers.get(0).getCellWidth(), containers.get(0).getCellHeight());
		
		containers.get(0).pixelize(pixelsPerCell);
		
		newTab();
		
		if(packages.size() == 0) {
			containers.get(0).setName("Container 1");
		}
		
		ArrayList<TextRectangle> sortedPackages = sortBiggest(packages);
		
		for(int i = 0; i < sortedPackages.size(); i++) {
			TextRectangle packageObject = sortedPackages.get(i);
			StackPane.setAlignment(packageObject, Pos.TOP_LEFT);
			
			ArrayList<Integer> fitPosition = matrix.findFit(packageObject.getCellWidth(), packageObject.getCellHeight());
			
			System.out.println(fitPosition);
			
			if(fitPosition == null) {
				return;
			}
			
			if(fitPosition.get(0) > 0) {
				ArrayList<Integer> fitPosition2 = matrix.findFit(packageObject.getCellHeight(), packageObject.getCellWidth());
				
				if(fitPosition2 != null && fitPosition2.get(0) < fitPosition.get(0)) {
					fitPosition = fitPosition2;
					
					packageObject.rotate();
				}
			}
			
			if(fitPosition.size() == 3) {
				matrix.cutOut(fitPosition.get(1), fitPosition.get(2), packageObject.getCellWidth(), packageObject.getCellHeight(), fitPosition.get(0));
				
				packageObject.setOffsets(fitPosition.get(1), fitPosition.get(2));
				packageObject.pixelize(pixelsPerCell);
				
				if(fitPosition.get(0) > tabPane.getTabs().size() - 1) {
					newTab();
				}
				
				containers.get(fitPosition.get(0)).setName("");
				
				innerStackPanes.get(fitPosition.get(0)).getChildren().add(packageObject);
			}
		}
	}
	
	private ArrayList<TextRectangle> sortBiggest(ArrayList<TextRectangle> packages) {
		ArrayList<TextRectangle> sortedPackages = new ArrayList<TextRectangle>(packages.size());
		
		for(int h = 0; h < packages.size(); h++) {
			int largestSide = 0;
			int index = 0;
			
			for(int i = 0; i < packages.size(); i++) {
				if(packages.get(i).getCellWidth() > largestSide && !sortedPackages.contains(packages.get(i))) {
					largestSide = packages.get(i).getCellWidth();
					index = i;
				}
				
				if(packages.get(i).getCellHeight() > largestSide && !sortedPackages.contains(packages.get(i))) {
					largestSide = packages.get(i).getCellHeight();
					index = i;
				}
			}
			
			sortedPackages.add(packages.get(index));
		}
		
		return sortedPackages;
	}
}