package ComplexNumber;

public class ComplexNumberTest {
    public static void main(String[] args) {
        ComplexNumber complexNumber_1 = new ComplexNumber(-2, -8);
        ComplexNumber complexNumber_2 = new ComplexNumber(1, 5);
        System.out.println("Комплексное число 1: " + complexNumber_1);
        System.out.println("Модуль комплексного числа 1: " + complexNumber_1.getMod());
        System.out.println("Комплексное число 2: " + complexNumber_2);
        System.out.println("Модуль комплексного числа 2: " + complexNumber_2.getMod());
        System.out.println("Сумма комплексных чисел: " + complexNumber_1.add(complexNumber_2));
        System.out.println("Разность комплексных чисел: " + complexNumber_1.sub(complexNumber_2));
        System.out.println("Произведение комплексных чисел: " + complexNumber_1.mul(complexNumber_2));
    }
}
