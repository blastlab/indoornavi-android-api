package co.blastlab.indoornavi_api.algorithm.utils;

public class Matrix {
	private final int M;             // number of rows
	private final int N;             // number of columns
	public final double[][] data;   // M-by-N array

	// create matrix based on 2d array
	public Matrix(double[][] data) {
		M = data.length;
		N = data[0].length;
		this.data = new double[M][N];
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				this.data[i][j] = data[i][j];
	}

	// create matrix based on 2d array
	public Matrix(double[] data) {
		M = data.length;
		N = 1;
		this.data = new double[M][N];

		for (int i = 0; i < M; i++)
				this.data[i][0] = data[i];
	}

	// copy constructor
	private Matrix(Matrix A) { this(A.data); }

	// create M-by-N matrix of 0's
	public Matrix(int M, int N) {
		this.M = M;
		this.N = N;
		data = new double[M][N];
	}

	// create and return the transpose of the invoking matrix
	public Matrix transpose() {
		Matrix A = new Matrix(N, M);
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				A.data[j][i] = this.data[i][j];
		return A;
	}

	public Matrix add(Matrix B) {
		Matrix res = new Matrix(this.M, this.N);

		for (int i = 0; i < this.M; ++i) {
			for (int j = 0; j < B.N; ++j) {
				res.data[i][j] = this.data[i][j] + B.data[i][j];
			}
		}
		return res;
	}

	public Matrix multiply(Matrix B) {
		Matrix res = new Matrix(this.M, B.N);

		int i;
		for (i = 0; i < this.M; ++i) {
			int j;
			for (j = 0; j < B.N; ++j) {
				int k;
				double sum = 0;
				for (k = 0; k < this.N; ++k) {
					sum += this.data[i][k] * B.data[k][j];
				}
				res.data[i][j] = sum;
			}
		}

//		for(int i = 0; i < this.M; i++) {
//			for(int j = 0; j < B.N; j++) {
//				for(int k = 0; k < B.M; k++){
//					res.data[i][j] += this.data[i][k]*B.data[k][j];
//				}
//			}
//		}
		return res;
	}

	// swap rows i and j
	private void swap(int i, int j) {
		double[] temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}


	// return x = A^-1 b, assuming A is square and has full rank
	public Matrix solve(Matrix rhs) {
		if (M != N || rhs.M != N || rhs.N != 1)
			throw new RuntimeException("Illegal matrix dimensions.");

		// create copies of the data
		Matrix A = new Matrix(this);
		Matrix b = new Matrix(rhs);

		// Gaussian elimination with partial pivoting
		for (int i = 0; i < N; i++) {

			// find pivot row and swap
			int max = i;
			for (int j = i + 1; j < N; j++)
				if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
					max = j;
			A.swap(i, max);
			b.swap(i, max);

			// singular
			if (A.data[i][i] == 0.0) throw new RuntimeException("Matrix is singular.");

			// pivot within b
			for (int j = i + 1; j < N; j++)
				b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

			// pivot within A
			for (int j = i + 1; j < N; j++) {
				double m = A.data[j][i] / A.data[i][i];
				for (int k = i+1; k < N; k++) {
					A.data[j][k] -= A.data[i][k] * m;
				}
				A.data[j][i] = 0.0;
			}
		}

		// back substitution
		Matrix x = new Matrix(N, 1);
		for (int j = N - 1; j >= 0; j--) {
			double t = 0.0;
			for (int k = j + 1; k < N; k++)
				t += A.data[j][k] * x.data[k][0];
			x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
		}
		return x;

	}


}
