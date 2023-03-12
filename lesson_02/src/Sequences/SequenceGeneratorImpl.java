package Sequences;

public class SequenceGeneratorImpl implements SequenceGenerator{

    @Override
    public void a(int n) {
        for (int i = 1; i <= n; i++){
            System.out.print(2 * i + " ");
        }
        System.out.println();
    }

    @Override
    public void b(int n) {
        for (int i = 0; i < n; i++){
            System.out.print(2 * i + 1 + " ");
        }
        System.out.println();
    }

    @Override
    public void c(int n) {
        for (int i = 1; i <= n; i++){
            System.out.print(i * i + " ");
        }
        System.out.println();
    }

    @Override
    public void d(int n) {
        for (int i = 1; i <= n; i++){
            System.out.print(i * i * i + " ");
        }
        System.out.println();
    }

    @Override
    public void e(int n) {
        int element = -1;
        for (int i = 1; i <= n; i++){
            element *= -1;
            System.out.print(element + " ");
        }
        System.out.println();
    }

    @Override
    public void f(int n) {
        int element = -1;
        for (int i = 1; i <= n; i++){
            element *= -1;
            System.out.print(i * element + " ");
        }
        System.out.println();
    }

    @Override
    public void g(int n) {
        int element = -1;
        for (int i = 1; i <= n; i++){
            element *= -1;
            System.out.print(i * i * element + " ");
        }
        System.out.println();
    }

    @Override
    public void h(int n) {
        int element = 1;
        for (int i = 1; i <= n; i++){
            if (i % 2 == 0) {
                System.out.print("0 ");
            } else {
                System.out.print(element + " ");
                element++;
            }
        }
        System.out.println();
    }

    @Override
    public void i(int n) {
        int element = 1;
        for (int i = 1; i <= n; i++){
            element *= i;
            System.out.print(element + " ");
        }
        System.out.println();
    }

    @Override
    public void j(int n) {
        int elem_2;
        int elem_1 = 0;
        int currentElement = 1;
        for (int i = 1; i <= n; i++){
            System.out.print(currentElement + " ");
            elem_2 = elem_1;
            elem_1 = currentElement;
            currentElement = elem_1 + elem_2;
        }
        System.out.println();
    }
}
