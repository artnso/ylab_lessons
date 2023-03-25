package OrgStructure;

import java.io.File;
import java.io.IOException;

public class OrgStructureTest {
    public static void main(String[] args) {
        try {
            File csvFile = new File("orgstructure.csv");
            Employee employee = new OrgStructureParserImpl().parseStructure(csvFile);
            System.out.println("Руководитель:");
            System.out.println(employee.getId() + " " + employee.getName() + ", " + employee.
                    getPosition());
            System.out.println("Подчиненные: ");
            for (Employee subordinate: employee.getSubordinate()) {
                System.out.println("\t" + subordinate.getId() + " " + subordinate.getName() + ", " + subordinate.
                        getPosition());

            }
            // Проверка актуальности заполнения данных по подчиненным
            employee = employee.getSubordinate().get(0);
            System.out.println("\nИнформация об одном из подчиненных генерального директора:");
            System.out.println(employee.getId() + " " + employee.getName() + ", " + employee.
                    getPosition());
            System.out.println("Подчиненные: ");
            for (Employee subordinate: employee.getSubordinate()) {
                System.out.println("\t" + subordinate.getId() + " " + subordinate.getName() + ", " + subordinate.
                        getPosition());

            }

        } catch (IOException ex) {
            System.out.println("Возникли проблемы с чтением файла организационной структуры");
        }
    }
}
