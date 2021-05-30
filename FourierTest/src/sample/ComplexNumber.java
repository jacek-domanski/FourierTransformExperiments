package sample;

import static java.lang.Math.hypot;
import static java.lang.Math.atan2;

public class ComplexNumber {
    private double re;
    private double im;

    private int waveNo;

    public static ComplexNumber multiply(ComplexNumber c1, ComplexNumber c2) {
        double re = c1.re * c2.re - c1.im * c2.im;
        double im = c1.re * c2.im + c1.im * c2.re;
        return new ComplexNumber(re, im);
    }

    public ComplexNumber(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getAmplitude() {
        return hypot(this.re, this.im);
    }

    public double getPhase() {
        return atan2(this.im, this.re);
    }

    public void add(ComplexNumber other) {
        this.re += other.re;
        this.im += other.im;
    }

    public String toString() {
        return "re:"+ String.format("%.2f", this.re) +" im:"+ String.format("%.2f", this.im);
    }

    public double getRe() {return re;}

    public void setRe(double re) {
        this.re = re;
    }

    public double getIm() {return im;}

    public void setIm(double im) {
        this.im = im;
    }

    public int getWaveNo() {
        return waveNo;
    }

    public void setWaveNo(int waveNo) {
        this.waveNo = waveNo;
    }
}
