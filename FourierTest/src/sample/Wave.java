package sample;

public class Wave extends ComplexNumber {
    private int waveNo;

    public Wave(double re, double im) {
        super(re, im);
    }

    public int getWaveNo() {
        return waveNo;
    }

    public void setWaveNo(int waveNo) {
        this.waveNo = waveNo;
    }
}
