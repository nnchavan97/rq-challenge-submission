package com.reliaquest.api.service;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import java.util.List;

/**
 * Business logic for Employee related APIs
 *
 * @author nikhilchavan
 */
public interface IEmployeeService {

    /**
     * Retrieves all employees.
     */
    List<EmployeeDto> getAllEmployees();

    /**
     * Retrieves an employee by ID.
     *
     * @param id employee ID
     * @return EmployeeDto containing employee details if found
     */
    EmployeeDto getEmployeeById(String id);

    /**
     * Creates a new employee.
     *
     * @param employeeRequestDto employee creation input
     * @return created employee details
     */
    EmployeeDto createEmployee(CreateEmployeeRequestDto employeeRequestDto);

    /**
     * Deletes an employee by ID.
     *
     * @param id employee ID
     * @return Employee Name if deletion succeeded
     */
    String deleteEmployeeById(String id);

    /**
     * Finds employees with matching names.
     *
     * @param name name keyword
     */
    List<EmployeeDto> searchEmployeesByName(String name);

    /**
     * Gets the highest salary among employees.
     */
    Integer getHighestSalaryOfEmployees();

    /**
     * Gets the names of the top ten highest-earning employees.
     */
    List<String> getTopTenHighestEarningEmployeeNames();
}
