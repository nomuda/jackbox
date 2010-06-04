package some.legacy;

import java.util.*;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<Employee>();
        for(int i = 0; i < 10; i++) {
            var employee = new Employee();
            employee.setId(i);
            employee.setSalary((i + 1.0)*1000);
            employee.setName("Employee number " + i);
            employees.add(employee);
        }
        return employees;
    }
    
}
