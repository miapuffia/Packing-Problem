package packingProblem.src;

import java.util.ArrayList;
import java.util.Collections;

public class Matrix {
	private int height, width, layers;
	private final ArrayList<ArrayList<ArrayList<Boolean>>> matrix;
	
	Matrix(int width, int height) {
		this.height = height;
		this.width = width;
		layers = 1;
		
		ArrayList<ArrayList<ArrayList<Boolean>>> matrixLayers = new ArrayList<ArrayList<ArrayList<Boolean>>>(1);
		
		ArrayList<ArrayList<Boolean>> matrixWidth = new ArrayList<ArrayList<Boolean>>(this.width);
		
		for(int i = 0; i < this.width; i++) {
			ArrayList<Boolean> matrixHeight = new ArrayList<Boolean>(Collections.nCopies(this.height, true));
			matrixWidth.add(matrixHeight);
		};
		
		matrixLayers.add(matrixWidth);
		
		matrix = matrixLayers;
	}
	
	Matrix(int width, int height, int layers) {
		this.height = height;
		this.width = width;
		this.layers = layers;
		
		ArrayList<ArrayList<ArrayList<Boolean>>> matrixLayers = new ArrayList<ArrayList<ArrayList<Boolean>>>(layers);
		
		for(int i = 0; i < this.layers; i++) {
			ArrayList<ArrayList<Boolean>> matrixWidth = new ArrayList<ArrayList<Boolean>>(this.width);
			
			for(int j = 0; j < this.width; j++) {
				ArrayList<Boolean> matrixHeight = new ArrayList<Boolean>(Collections.nCopies(this.height, true));
				matrixWidth.add(matrixHeight);
			};
			
			matrixLayers.add(matrixWidth);
		};
		
		matrix = matrixLayers;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getLayers() {
		return layers;
	}
	
	public boolean get(int layerIndex, int widthIndex, int heightIndex) {
		if(layerIndex >= layers || widthIndex >= this.width || heightIndex >= this.height) {
			return false;
		}
		
		return matrix.get(layerIndex).get(widthIndex).get(heightIndex);
	}
	
	public void set(int layerIndex, int widthIndex, int heightIndex) {
		if(layerIndex >= layers || widthIndex >= this.width || heightIndex >= this.height) {
			return;
		}
		
		matrix.get(layerIndex).get(widthIndex).set(heightIndex, false);
	}
	
	public void addLayer() {
		ArrayList<ArrayList<Boolean>> matrixWidth = new ArrayList<ArrayList<Boolean>>(width);
		
		for(int i = 0; i < width; i++) {
			ArrayList<Boolean> matrixHeight = new ArrayList<Boolean>(Collections.nCopies(height, true));
			matrixWidth.add(matrixHeight);
		};
		
		matrix.add(matrixWidth);
		
		layers++;
	}
	
	public void cutOut(int widthOffset, int heightOffset, int width, int height, int layerIndex) {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				set(layerIndex, widthOffset + i, heightOffset + j);
			}
		}
	}
	
	public ArrayList<Integer> findFit(int packageWidth, int packageHeight) {
		ArrayList<Integer> returnArrayList = new ArrayList<Integer>();
		
		if(packageWidth > width || packageHeight > height) {
			return null;
		}
		
		for(int h = 0; h < layers; h++) {
			for(int i = 0; i <= getWidth() - packageWidth; i++) {
				for(int j = 0; j <= getHeight() - packageHeight; j++) {
					for(int k = 0; k < packageWidth; k++) {
						boolean broken = false;
						
						for(int l = 0; l < packageHeight; l++) {
							if(get(h, i + k, j + l) != true) {
								broken = true;
								break;
							}
						}
						
						if(broken) {
							break;
						}
						
						if(k == packageWidth - 1) {
							returnArrayList.add(h);
							returnArrayList.add(i);
							returnArrayList.add(j);
							return returnArrayList;
						}
					}
				}
			}
			
			addLayer();
		}
		
		return returnArrayList;
	}
}