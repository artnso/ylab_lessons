package OrgStructure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class OrgStructureParserImpl implements OrgStructureParser {

    private Employee buildEmployee(String[] employeeInfo){
        Employee employee = new Employee();
        employee.setId(Long.parseLong(employeeInfo[0]));
        if (!employeeInfo[1].equals("")) {
            employee.setBossId(Long.parseLong(employeeInfo[1]));
        }
        employee.setName(employeeInfo[2]);
        employee.setPosition(employeeInfo[3]);
        return employee;
    }

    private void buildOrgStructure(Map<Long, Employee> orgStructure){
        Employee currentEmployee;
        Employee bossEmployee;
        Iterator<Long> keyIterator = orgStructure.keySet().iterator();
        while(keyIterator.hasNext()){
            Long id = keyIterator.next();
            currentEmployee = orgStructure.get(id);
            Long bossId = currentEmployee.getBossId();
            if (bossId == null) {
                continue;
            }
            bossEmployee = orgStructure.get(bossId);
            bossEmployee.getSubordinate().add(currentEmployee);
            currentEmployee.setBoss(bossEmployee);
        }

    }

    @Override
    public Employee parseStructure(File csvFile) throws IOException {
        Scanner scanner = new Scanner(csvFile);
        String line;
        String[] employeeInfo;
        Employee currentEmployee;
        Employee bossEmployee = null;
        Map<Long, Employee> orgStructure = new HashMap<>();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            employeeInfo = line.split(";");
            if (employeeInfo[0].equals("id")){
                continue;
            }
            currentEmployee = buildEmployee(employeeInfo);
            orgStructure.put(currentEmployee.getId(), currentEmployee);
            if (currentEmployee.getBossId() == null) {
                bossEmployee = currentEmployee;
            }
        }
        scanner.close();
        buildOrgStructure(orgStructure);
        return bossEmployee;
    }
}
