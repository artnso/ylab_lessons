package ComplexNumber;

public class ComplexNumber {
    private double re;
    private double im;

    public ComplexNumber(double re) {
        this.re = re;
        this.im = 0;
    }

    public ComplexNumber(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getRe() {
        return re;
    }

    public double getIm() {
        return im;
    }

    public ComplexNumber add(ComplexNumber complexNumber) {
        return new ComplexNumber(this.re + complexNumber.getRe(), this.im + complexNumber.getIm());
    }

    public ComplexNumber sub(ComplexNumber complexNumber){
        return new ComplexNumber(this.re - complexNumber.getRe(), this.im - complexNumber.getIm());
    }

    public ComplexNumber mul(ComplexNumber complexNumber){
        double re = this.re*complexNumber.getRe() - this.im*complexNumber.getIm();
        double im = this.re*complexNumber.getIm() + this.im*complexNumber.getRe();
        return new ComplexNumber(re, im);
    }

    public double getMod(){
        return Math.sqrt(this.re*this.re + this.im*this.im);
    }

    @Override
    public String toString() {
        String sign = "";
        if (this.im >= 0) {
            sign = "+";
        }
        return this.re + sign + this.im + "i";
    }
}
