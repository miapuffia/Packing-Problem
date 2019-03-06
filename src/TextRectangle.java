package packingProblem.src;

import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class TextRectangle extends StackPane {
	private final Rectangle shape;
	private final Label nameLabel = new Label();
	private int cellWidth, cellHeight;
	private Color cellBackgroundColor;
	private int widthOffset = 0;
	private int heightOffset = 0;
	
	TextRectangle() {
		cellWidth = 0;
		cellHeight = 0;
		shape = new Rectangle();
		nameLabel.setText("");
		nameLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		getChildren().addAll(shape, nameLabel);
	}
	
	TextRectangle(String name) {
		cellWidth = 0;
		cellHeight = 0;
		shape = new Rectangle();
		nameLabel.setText(name);
		nameLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		getChildren().addAll(shape, nameLabel);
	}
	
	TextRectangle(int width, int height) {
		cellWidth = width;
		cellHeight = height;
		shape = new Rectangle();
		nameLabel.setText("");
		nameLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		getChildren().addAll(shape, nameLabel);
	}
	
	TextRectangle(int width, int height, String name) {
		cellWidth = width;
		cellHeight = height;
		shape = new Rectangle();
		nameLabel.setText(name);
		nameLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		getChildren().addAll(shape, nameLabel);
	}
	
	public String getName() {
		return nameLabel.getText();
	}
	
	public void setName(String name) {
		nameLabel.setText(name);
	}
	
	public int getCellWidth() {
		return cellWidth;
	}
	
	public void setCellWidth(int width) {
		cellWidth = width;
	}
	
	public int getCellHeight() {
		return cellHeight;
	}
	
	public void setCellHeight(int height) {
		cellHeight = height;
	}
	
	public void setCellSize(int width, int height) {
		cellWidth = width;
		cellHeight = height;
	}
	
	public Rectangle getRectangle() {
		return shape;
	}
	
	public boolean isSet() {
		if(cellWidth != 0 && cellHeight != 0 && shape.getFill() != null) {
			return true;
		}
		
		return false;
	}
	
	public Color getCellBackgroundColor() {
		return cellBackgroundColor;
	}
	
	public void setCellBackgroundColor(Color color) {
		cellBackgroundColor = color;
	}
	
	public void setOffsets(int widthOffset, int heightOffset) {
		this.widthOffset = widthOffset;
		this.heightOffset = heightOffset;
	}
	
	public void rotate() {
		int widthTemp = cellWidth;
		cellWidth = cellHeight;
		cellHeight = widthTemp;
	}
	
	public void pixelize(double base) {
		shape.setWidth(cellWidth * base);
		shape.setHeight(cellHeight * base);
		
		setMaxWidth(shape.getWidth());
		setMaxHeight(shape.getHeight());
		
		StackPane.setMargin(this, new Insets(heightOffset * base, 0, 0, widthOffset * base));
		
		Canvas canvas = new Canvas(base, base);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		gc.setStroke(Color.BLACK);
		gc.setFill(getCellBackgroundColor());
		gc.fillRect(0, 0, base, base);
		gc.strokeRect(0.5, 0.5, base, base);
		
		Image image = canvas.snapshot(new SnapshotParameters(), null);
		ImagePattern pattern = new ImagePattern(image, 0, 0, base, base, false);
		
		shape.setFill(pattern);
	}
}