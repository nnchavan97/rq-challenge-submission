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

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(String id);

    EmployeeDto createEmployee(CreateEmployeeRequestDto employeeRequestDto);

    String deleteEmployeeById(String id);

    List<EmployeeDto> searchEmployeesByName(String name);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();
}
