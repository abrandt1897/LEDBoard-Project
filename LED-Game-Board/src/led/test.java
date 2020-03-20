package led;

import java.util.Arrays;

public class test {
	
	public static int[][] rotateMatrix(int matrix[][]){
		int h = matrix.length;
		int w = matrix[0].length;
		
		int mat[][] = new int[w][h];
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				mat[x][y] = matrix[y][x];
			}
		}
		
		for (int y = 0; y < w; y++) {
			for (int x = 0; x < h / 2; x++) {
				int temp = mat[y][h-x-1];
				mat[y][h-x-1] = mat[y][x];
				mat[y][x] = temp; 
			}
		}
		
		return mat;
	}
	
	public static void main(String Args[]) {
		int t[][] = {
			{ 1, 0 },
			{ 1, 0 },
			{ 1, 1 }
		};
		
		for (int a[] : t) System.out.println(Arrays.toString(a)); System.out.println();
		
		t = rotateMatrix(t); for (int a[] : t) System.out.println(Arrays.toString(a)); System.out.println();
		
		t = rotateMatrix(t); for (int a[] : t) System.out.println(Arrays.toString(a)); System.out.println();
		
		t = rotateMatrix(t); for (int a[] : t) System.out.println(Arrays.toString(a)); System.out.println();
	
		t = rotateMatrix(t); for (int a[] : t) System.out.println(Arrays.toString(a)); System.out.println();
	}
}
