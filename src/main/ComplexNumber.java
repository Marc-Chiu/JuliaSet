package main;

/*
 * A class to represent complex numbers and perform basic arithmetic operations on them.
 */

public class ComplexNumber {
        private double a;
        private double b;

        public ComplexNumber(double real, double imaginary) {
                this.a = real;
                this.b = imaginary;
        }

        public double getReal() {
                return a;
        }

        public double getImaginary() {
                return b;
        }

        public ComplexNumber add(ComplexNumber other) {
                double newReal = this.a + other.a;
                double newImaginary = this.b + other.b;
                return new ComplexNumber(newReal, newImaginary);
        }

        public ComplexNumber subtract(ComplexNumber other) {
                double newReal = this.a - other.a;
                double newImaginary = this.b - other.b;
                return new ComplexNumber(newReal, newImaginary);
        }

        public ComplexNumber multiply(ComplexNumber other) {
                double newReal = this.a * other.a - this.b * other.b;
                double newImaginary = this.a * other.b + this.b * other.a;
                return new ComplexNumber(newReal, newImaginary);
        }

        public ComplexNumber square() {
                return this.multiply(this);
        }

        public double magnitude() {
                return Math.sqrt(a * a + b * b);
        }

        @Override
        public String toString() {
                return a + " + " + b + "i";
        }
}