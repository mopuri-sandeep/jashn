/* ComplexMatrix.java

   September, 1998

   Author: Bryan Lewis
   Kent State University 
   Department of Mathematics & Computer Science
   mail: blewis@mcs.kent.edu
   url: http://www.mcs.kent.edu/~blewis/
   
   ComplexMatrix is a small complex-valued
   matrix class emphasizing size over efficiency.
   An easy to use parsing constructor can generate
   a matrix from flexibly formatted text input.

   Note that "ComplexMatrix" represents an arbitrary 
   array class including row and column vectors.
   
   This software is public domain and can be used, 
   modified and distributed freely.

   Needs the following objects:
   java.util.Vector,java.lang.Math, Complex
*/
package com.jashn.app;

import java.util.Vector;
import java.lang.Math;

public class ComplexMatrix {

    public int rows, columns;
    public Complex[][] element;	// the array containing the matrix

    public ComplexMatrix() {
        // Create a zero 1x1 matrix
        rows = 1;
        columns = 1;
        element = new Complex[rows][columns];
        element[0][0] = new Complex(0.0,0.0);
    }

    public ComplexMatrix(int r, int c) {
        //  creates a zero r by c matrix
        int i,k;
        rows = r;
        columns = c;
        element = new Complex[rows][columns];
        for(i=0;i<rows;i++){
            for(k=0;k<columns;k++){
                element[i][k] = new Complex(0.0,0.0);
            }
        }
    }

    public ComplexMatrix(double d) {
        //  creates a 1x1 matrix with real part d
        rows = 1;
        columns = 1;
        element = new Complex[1][1];
        element[0][0]=new Complex(d, 0.0);
    }

    public ComplexMatrix(Complex z) {
        //  creates a 1x1 matrix z
        rows = 1;
        columns = 1;
        element = new Complex[1][1];
        element[0][0]=new Complex(z.getRe(), z.getIm());
    }

    public ComplexMatrix(double re, double im) {
        //  creates a 1x1 matrix with entry (re,im)
        rows = 1;
        columns = 1;
        element = new Complex[1][1];
        element[0][0] = new Complex(re,im);
    }

    public ComplexMatrix(ComplexMatrix m) {
        //  creates a new replicate of m
        rows = m.rows;
        columns = m.columns;
        element = new Complex[rows][columns];
        int i, j;
        for (i=0; i<rows; i++) {
            for (j=0; j<columns; j++) {
                element[i][j]=new Complex(m.element[i][j].getRe(), m.element[i][j].getIm());
            }
        }
    }

    public ComplexMatrix(String s) {
    /* Creates a ComplexMatrix object and 
       parses the string s into the element array.
       The string format can be like Matlab or typical 
       delimited ascii data as follows:
       columns separated by tabs or commas
       rows separated by newline or semicolon
       For example, 
         A=new ComplexMatrix("1+3i, 3; 2, -i");
       would create the complex-valued matrix:

	     1+3i     3 
	     2       -i 
           
       Short rows are filled with zeros.

       This method can parse the usual complex notation, 
       e.g., 1+3.2i.  NOT OK: (1,3.2) nor 1+3.2*i
    */

        Vector row = new Vector();	// data will be assembled into these vectors
        Vector col = new Vector();	// and then transferred
        // into the array element[][].
        s = s + " ;";
        int i = s.length();
        int j;
        Complex c;
        int rowCounter = 0;
        int colCounter = 0;
        String sData = new String(); // will hold each element during parsing
        double tre, tim;
        char sChar;
        for (j = 0; j<i; j++) {
            sChar = s.charAt(j);
            // check for a delimiter...
            if ( (sChar==',')|| ( (int) sChar==9)||
                    (sChar == ';')||( (int) sChar == 13)||( (int) sChar == 10) ) {
                // See if the string in sData represents a number...
                try {
                    boolean testSpace = true;
                    int ii;
                    for(ii=0;ii<sData.length();ii++){
                        testSpace=testSpace&&(sData.charAt(ii)==' '); }
                    if(testSpace==false){
	    /* This will generate an exception if sData does not
	       represent a complex number */
//                        c=new Complex(sData);
                        col.addElement(sData); 	// append column element as string
                    }
                    sData = new String();	// wipe out contents of string
                }
                catch (Exception e) {
                    // non-numeric stuff...
                    sData = new String();	// wipe out contents of string
                }
                if ( ( (sChar == ';')||( (int) sChar == 13)||( (int) sChar == 10) ) &&
                        !col.isEmpty() ) {
                    row.addElement(col); // append row (a vector of column elements)
                    rowCounter = rowCounter + 1;
                    sData = new String();		// wipe out contents of string
                    colCounter = col.size();
                    col = new Vector();	// wipe out the column vector
                }
            }
            // build up data...
            else {
                if ((Character.isDigit(sChar))||(sChar=='.')||
                        (sChar=='-')||(sChar=='+')||(sChar=='i')) {
                    // allow only numerical characters
                    sData = sData + sChar;	// append to string
                }
            }
        }
        rows = rowCounter;
        columns = colCounter;
        element = new Complex[rows][columns];
        for (j=0; j<rows; j++) {
            col = (Vector)row.elementAt(j);
            for (i=0; i<col.size(); i++) {
//                c = new Complex((String)col.elementAt(i));
//                element[j][i] = new Complex(c);
            }
        }
    }

    public static ComplexMatrix identity(int n) {
        // returns an nxn complex identity
        ComplexMatrix c=new ComplexMatrix(n,n);
        int i, j;
        for (i=0; i<n; i++) {
            for (j=0; j<n; j++) {
                if(i==j){
                    c.element[i][j]=new Complex(1.0,0.0); }
                else{
                    c.element[i][j]=new Complex(0.0,0.0); }
            }
        }
        return c;
    }

    public static ComplexMatrix rand(int m, int n) {
        // returns an mxn matrix with random complex entries
        ComplexMatrix c=new ComplexMatrix(m,n);
        int i, j;
        double x,y;
        for (i=0; i<m; i++) {
            for (j=0; j<n; j++) {
                x=Math.random();
                y=Math.random();
                c.element[i][j]=new Complex(x,y);
            }
        }
        return c;
    }

    public ComplexMatrix transpose() {
        // returns the transpose of this matrix object
        ComplexMatrix t = new ComplexMatrix(columns, rows);
        int i, j;
        for (i = 0; i<rows; i++) {
            for (j = 0; j<columns; j++) {
                t.element[j][i] = new Complex(this.element[i][j].getRe(), this.element[i][j].getIm());
            }
        }
        return t;
    }

    public ComplexMatrix conjugate() {
        // returns the conjugate of this matrix object
        ComplexMatrix t = new ComplexMatrix(rows, columns);
        int i, j;
        for (i = 0; i<rows; i++) {
            for (j = 0; j<columns; j++) {
                t.element[i][j] = this.element[i][j].conjugate();
            }
        }
        return t;
    }

    public static ComplexMatrix add(ComplexMatrix m1, ComplexMatrix m2){
        // Return the ComplexMatrix m = m1 + m2
        ComplexMatrix m=new ComplexMatrix(m1.rows,m1.columns);
        if ((m1.rows == m2.rows)&&(m1.columns==m2.columns)) {
            int i,j;
            for (i=0; i<m.rows; i++) {
                for (j=0; j<m.columns; j++) {
                    m.element[i][j]=new Complex(Complex.add(m1.element[i][j],m2.element[i][j]).getRe(), Complex.add(m1.element[i][j],m2.element[i][j]).getIm());
                }
            }
        }
        return m;
    }

    public static ComplexMatrix subtract(ComplexMatrix m1, ComplexMatrix m2){
        // Return the difference m1-m2
        ComplexMatrix m=new ComplexMatrix(m1.rows,m1.columns);
        if ((m1.rows == m2.rows)&&(m1.columns==m2.columns)) {
            int i,j;
            for (i=0; i<m.rows; i++) {
                for (j=0; j<m.columns; j++) {
                    m.element[i][j]=new Complex(Complex.subtract(m1.element[i][j], m2.element[i][j]).getRe(), Complex.subtract(m1.element[i][j], m2.element[i][j]).getIm());
                }
            }
        }
        return m;
    }

    public static ComplexMatrix multiply(double d, ComplexMatrix m1){
        // Return the matrix m = d*m1
        ComplexMatrix m=new ComplexMatrix(m1.rows,m1.columns);
        int i,j;
        for (i=0; i<m.rows; i++) {
            for (j=0; j<m.columns; j++) {
                m.element[i][j]=new Complex(Complex.multiply(d, m1.element[i][j]).getRe(), Complex.multiply(d, m1.element[i][j]).getIm());
            }
        }
        return m;
    }

    public static ComplexMatrix multiply(Complex c, ComplexMatrix m1){
        // Return the matrix m = c*m1
        ComplexMatrix m=new ComplexMatrix(m1.rows,m1.columns);
        int i,j;
        Complex z;
        for (i=0; i<m.rows; i++) {
            for (j=0; j<m.columns; j++) {
                z=m1.element[i][j];
                m.element[i][j]=new Complex(Complex.multiply(c,z).getRe(),Complex.multiply(c,z).getIm());
            }
        }
        return m;
    }

    public static ComplexMatrix multiply(ComplexMatrix m1, ComplexMatrix m2){
     /* Matrix-Matrix or Matrix-vector product
	returns m=m1*m2
	m1 can be a 1x1 Matrix for scalar-Matrix product
     */
        ComplexMatrix m = new ComplexMatrix();
        if (m1.columns == m2.rows) {
            // matrix product
            Complex sum = new Complex();
            Complex z = new Complex();
            int k = 0;
            m = new ComplexMatrix(m1.rows,m2.columns);
            int i,j;
            for (i=0; i<m.rows; i++) {
                for (k=0; k<m2.columns; k++) {
                    for (j=0; j<m1.columns; j++) {
                        z = Complex.multiply(m1.element[i][j], m2.element[j][k]);
                        sum = Complex.add(sum,z);
                    }
                    m.element[i][k] = sum;
                    sum = new Complex();
                }
            }
        }
        else if ((m1.columns == 1)&&(m1.rows == 1)) {
            // scalar-vector product
            m = new ComplexMatrix(m2.rows,m2.columns);
            int i,j;
            for (i=0; i<m.rows; i++) {
                for (j=0; j<m.columns; j++) {
                    m.element[i][j] = Complex.multiply(m1.element[0][0], m2.element[i][j]);
                }
            }
        }
        return m;
    }

    public static ComplexMatrix divide(ComplexMatrix m1, ComplexMatrix m2) {
    /* Returns m1/m2. If m2 is a 1x1 matrix, then this is
       just matrix/scalar division. If m2 is a square, invertible
       matrix and m1 is a vector (n.b. a matrix with one column), 
       divide returns inverse(m2)*m1, using the modified
       Gram-Schmidt QR algorithm.
    */
        ComplexMatrix m = new ComplexMatrix(0);
        if ((m2.columns == 1)&&(m2.rows == 1)) {
            // NOT DONE YET
        }
        else if ((m2.columns == m2.rows)&&
                (m1.columns == 1)&&(m1.rows == m2.rows)) {
      /* Solve a general, dense, non-singular linear 
	 system Ax=b via QR-mgs, where A=m2, b=m1, and x is returned. */
            m=new ComplexMatrix(m2.rows,1);
            ComplexMatrix Q=m2.Q();
            ComplexMatrix R=multiply(Q.conjugate().transpose(),m2);
            ComplexMatrix b=multiply(Q.conjugate().transpose(),m1);
            int i,j;
            Complex sum = new Complex();
            m.element[m.rows-1][0] =
                    Complex.divide(b.element[m.rows-1][0],R.element[m.rows-1][m.rows-1]);
            i=m.rows-1;
            while(i >= 0) {
                sum = new Complex();
                j = m.rows-1;
                while(j>=i+1) {
                    sum = Complex.add(sum,Complex.multiply(R.element[i][j],m.element[j][0]));
                    j--;
                }
                m.element[i][0] =
                        Complex.divide(Complex.subtract(b.element[i][0],sum),R.element[i][i]);
                i--;
            }
        }
        return m;
    }

    public ComplexMatrix sub(int r1, int r2, int c1, int c2) {
        // returns the submatrix (r1:r2,c1:c2) (Moeler notation)
        // requires r2>=r1, c2>=c1
        ComplexMatrix A = new ComplexMatrix(r2 - r1 + 1, c2 - c1 + 1);
        int i, j;
        for (i = r1; i<=r2; i++) {
            for (j = c1; j<=c2; j++) {
                A.element[i - r1][j - c1] = this.element[i][j];
            }
        }
        return A;
    }

    public ComplexMatrix diag() {
        // return the diagonal of this matrix
        ComplexMatrix A = new ComplexMatrix(rows,1);
        int i;
        for(i=0;i<rows;i++) {
            A.element[i][0]=element[i][i];
        }
        return A;
    }

    public ComplexMatrix Q() {
    /* Modified Gram-Schmidt procedure
       Returns Q s.t. Q'M=R, R upper triangular, Q'Q=I
       where ' indicates conjugate transpose
    */
        ComplexMatrix A=new ComplexMatrix(this);
        ComplexMatrix Q=new ComplexMatrix(rows,columns);
        ComplexMatrix x,y,w,z;
        int k,j,i;
        for(k=0;k<columns;k++){
            x=A.sub(0,rows-1,k,k);
            x=multiply(1/x.norm(),x);
            for(i=0;i<rows;i++){Q.element[i][k]=x.element[i][0];}
            for(j=k+1;j<columns;j++){
                x=A.sub(0,rows-1,j,j);
                w=new ComplexMatrix(Q.sub(0,rows-1,k,k));
                z=multiply(w.conjugate().transpose(),x);
                y=multiply((Complex)z.element[0][0],Q.sub(0,rows-1,k,k));
                x=subtract(x,y);
                for(i=0;i<rows;i++){A.element[i][j]=x.element[i][0];}
            }
        }
        return Q;
    }

    public ComplexMatrix appendCols(ComplexMatrix x){
        // append the column vectors in x to this matrix
        ComplexMatrix M=new ComplexMatrix(rows,columns+x.columns);
        int i,j;
        for(i=0;i<rows;i++){
            for(j=0;j<columns;j++){
                M.element[i][j]=new Complex(this.element[i][j].getRe(),this.element[i][j].getIm() );
            }
            for(j=0;j<x.columns;j++){
                M.element[i][columns+j]=new Complex(x.element[i][j].getRe(),x.element[i][j].getIm() );
            }
        }
        return M;
    }

    public ComplexMatrix permute(int a1, int a2, char c) {
    /*	Returns a permuted matrix according  code c
	where c is in {'c', 'r'} for columns or rows and
	a1, a2 represent the columns/rows to swap
    */
        ComplexMatrix p = new ComplexMatrix(this);
        int i, j;
        if (c == 'r') {
            for (i=0; i<columns; i++) {
                p.element[a1][i] = this.element[a2][i];
                p.element[a2][i] = this.element[a1][i];
            }
        }
        else if (c == 'c') {
            for (i=0; i<rows; i++) {
                p.element[i][a1] = this.element[i][a2];
                p.element[i][a2] = this.element[i][a1];
            }
        }
        return p;
    }

    public double norm() {
    /* returns the Frobenius norm (Matrix), or Euclidean norm (Vector)
       This is the default norm for a Matrix object. Use the Norm
       class for different norms.
    */
        double l = 0;
        int i, j;
        for (i = 0; i<rows; i++) {
            for (j = 0; j<columns; j++) {
                l = l + Math.pow(this.element[i][j].abs(),2);
            }
        }
        l = Math.pow(l, 0.5);
        return l;
    }

    public Complex max() {
    /* returns the element of largest modulus */
        Complex m = this.element[0][0];
        int i,j;
        for (i = 0; i<rows; i++) {
            for (j = 0; j<columns; j++) {
                if(this.element[i][j].abs() > m.abs()){
                    m = this.element[i][j];
                }
            }
        }
        return m;
    }

    public Complex sum() {
        // returns the sum of all the elements in the matrix or vector
        Complex s = new Complex();
        int i, j;
        for (i = 0; i<rows; i++) {
            for (j = 0; j<columns; j++) {
                s = Complex.add(s, this.element[i][j]);
            }
        }
        return s;
    }

    public ComplexMatrix eig(double tol, int iterMax) {
    /* A wierd, inefficient, but simple root 
       finder using the Schur decomposition and simple 
       vector iteration. tol is a stopping criterion for
       an iteration which otherwise terminates after
       iterMax steps. eig returns a column vector of
       the eigenvalues.
    */

        ComplexMatrix S = identity(rows);
        ComplexMatrix X,Y,Q,E;
        ComplexMatrix z = new ComplexMatrix();
        int i,j,k;
        for(j=0;j<rows;j++){
            X=new ComplexMatrix(multiply(this,S));
            X=new ComplexMatrix(multiply(S.conjugate().transpose(), X));
            Y=new ComplexMatrix(X.sub(j,rows-1,j,rows-1));
            if(Y.rows>1){ z=Y.hybrid(tol,iterMax); }
            else{ z=new ComplexMatrix(Y); }
            z=multiply(1/z.norm(),z);
            X=new ComplexMatrix(z);
            X=X.appendCols(rand(z.rows,z.rows-1));
            Q=new ComplexMatrix(X.Q());
            E=new ComplexMatrix(identity(rows));
            for(i=j;i<rows;i++){
                for(k=j;k<rows;k++){
                    E.element[i][k]=new Complex(Q.element[i-j][k-j].getRe(),Q.element[i-j][k-j].getIm() );
                }
            }
            S=new ComplexMatrix(multiply(S,E));
        }
        X=new ComplexMatrix(multiply(this,S));
        X=new ComplexMatrix(multiply(S.conjugate().transpose(),X));
        z=new ComplexMatrix(rows,1);
        for(j=0;j<rows;j++){
            z.element[j][0]=new Complex(X.element[j][j].getRe(),X.element[j][j].getIm() );
        }
        return z;
    }

    public ComplexMatrix hybrid(double tol, int iterMax) {
    /* A hybrid vector iteration method for finding an eigenvector
       corresponding to the eigenvalue of biggest modulus in the
       spectrum of this matrix. tol is a tolerance for terminating 
       iteration which otherwise terminates after iterMax steps.
    */

        int iter = 0;
        double r = Math.pow(10,10);
        ComplexMatrix w,x,l;
        ComplexMatrix z = new ComplexMatrix(rows,1);
        z.element[0][0] = new Complex(1.0,1.0);
        if(rows>1){
            z.element[1][0] = new Complex(1.0,-1.0);
        }
        z=multiply(1/z.norm(),z);
        while((r>tol)&&(iter<iterMax)){
            // run simple vector iteration for a while
            w=new ComplexMatrix(z);
            z=new ComplexMatrix(multiply(this,z));
            z=new ComplexMatrix(multiply(1/z.norm(),z));
            x=subtract(z,w);
            r=x.norm();
            iter++;
        }
        if(r>tol){
            // run some inverse iteration steps if needed...
            iter=0;
            w=new ComplexMatrix(multiply(this, z));
            l=new ComplexMatrix(multiply(z.conjugate().transpose(),w));
            ComplexMatrix B=
                    new ComplexMatrix(multiply((Complex)l.element[0][0],identity(rows)));
            B=new ComplexMatrix(subtract(B,this));
            while((r>tol)&&(iter<iterMax)){
                w=new ComplexMatrix(z);
                z=new ComplexMatrix(divide(w,B));
                z=multiply(1/z.norm(),z);
                x=subtract(z,w);
                r=x.norm();
                iter++;
            }
        }
        return z;
    }

    public String toString(int d) {
    /*Return a string representation of this matrix with 'd'
      displayed digits*/
        String newln = System.getProperty("line.separator");
        String outPut = new String();
        Complex c;
        int i, k;
        for (i=0; i<rows; i++) {
            for (k=0; k<columns; k++) {
                c=this.element[i][k];
                outPut = outPut  + (char) 9;
            }
            outPut = outPut + newln;
        }
        return outPut;
    }

    public String toString() {
    /*Return a string representation of this matrix with
      6 displayed digits*/
        String outPut = this.toString(6);
        return outPut;
    }

    public String toStringXY(int d) {
    /*Return a string representation of this matrix with 'd'
      displayed digits usin ordered pair format (real,imag)*/
        String newln = System.getProperty("line.separator");
        String outPut = new String();
        return outPut;
    }

}


