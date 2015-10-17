package com.jashn.app;
import android.util.Log;
/**
 * Created by macpro on 16/10/15.
 */
/******************************************************************************
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im fields after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 ******************************************************************************/

public class Complex {
    private final double re;   // the real part
    private final double im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(){
        re = 0.0;
        im = 0.0;
    }
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }
    public double getRe(){
        return re;
    }

    public double getIm(){
        return im;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude and angle/phase/argument
    public double abs()   { return Math.hypot(re, im); }  // Math.sqrt(re*re + im*im)
    public double phase() { return Math.atan2(im, re); }  // between -pi and pi

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }
    public static Complex subtract (Complex a, Complex b){
        Complex c = new Complex();
        double real=a.getRe()-b.getRe();
        double imag=a.getIm()-b.getIm();
        return (new Complex(real, imag));
    }

    public static Complex divide(Complex a, Complex b){

        double denom = 0.0D, ratio = 0.0D;
        if(a.isZero()){
            if(b.isZero()){
                return (new Complex(Double.NaN,Double.NaN));
            }
            else{
                return (new Complex(0.0D,0.0D));
            }
        }
        else{
            if(Math.abs(b.getRe())>=Math.abs(b.getIm())){
                ratio=b.getIm()/b.getRe();
                denom=b.getRe()+b.getIm()*ratio;
                double real=(a.getRe()+a.getIm()*ratio)/denom;
                double imag=(a.getIm()-a.getRe()*ratio)/denom;
                return (new Complex(real, imag));
            }
            else{
                ratio=b.getRe()/b.getIm();
                denom=b.getRe()*ratio+b.getIm();
                double real=(a.getRe()*ratio+a.getIm())/denom;
                double imag=(a.getIm()*ratio-a.getRe())/denom;
                return (new Complex(real, imag));

            }
        }
    }

    public boolean isZero(){
        boolean test = false;
        if(Math.abs(this.re)==0.0D && Math.abs(this.im)==0.0D)test = true;
        return test;
    }

    public static Complex multiply(Complex a, Complex b){


        double re=a.re*b.re-a.im*b.im;
        double im=a.re*b.im+a.im*b.re;
        Complex c = new Complex(re, im);
        return c;
    }

    public static Complex multiply(double a, Complex b){
        Complex c = new Complex();

        double real=a*b.getRe();
        double imag=a*b.getIm();
        return (new Complex(real, imag));
    }

    public static Complex add(Complex a, Complex b){
        double real=a.re+b.re;
        double imag=a.im+b.im;
        Complex c = new Complex(real, imag);

        return c;
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Log.d("complex", "here");
        Log.d("complex", "here");
        Complex a = this;
        if(b == null)
        {
            return new Complex(0.0, 0.0);
        }
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // scalar multiplication
    // return a new object whose value is (this * alpha)
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() {  return new Complex(re, -im); }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }

    // return a / b
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() {
        return sin().divides(cos());
    }



    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        double real = a.re + b.re;
        double imag = a.im + b.im;
        Complex sum = new Complex(real, imag);
        return sum;
    }


}

