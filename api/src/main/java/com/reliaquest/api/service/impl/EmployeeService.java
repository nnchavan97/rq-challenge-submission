package com.reliaquest.api.service.impl;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.external.EmployeeIntegrationService;
import com.reliaquest.api.service.IEmployeeService;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author nikhilchavan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeIntegrationService integrationService;

    @Override
    public List<EmployeeDto> getAllEmployees() {
        return integrationService.getAllEmployees();
    }

    @Override
    public EmployeeDto getEmployeeById(String id) {
        UUID uuid = getUUID(id);
        return integrationService.getEmployeeById(uuid);
    }

    private UUID getUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid employee id : " + id + ", Requires employee id in UUID format.");
        }
    }

    @Override
    public EmployeeDto createEmployee(CreateEmployeeRequestDto employeeRequestDto) {
        EmployeeDto employeeDto = integrationService.createEmployee(employeeRequestDto);
        return employeeDto;
    }

    @Override
    public String deleteEmployeeById(String id) {

        EmployeeDto employeeDto = getEmployeeById(id);
        if (Boolean.TRUE.equals(integrationService.deleteEmployeeByName(employeeDto.getName()))) {
            return employeeDto.getName();
        } else {
            throw new EmployeeNotFoundException("Employee with id: " + id + " not found");
        }
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<EmployeeDto> employeeDtoList = integrationService.getAllEmployees();
        return employeeDtoList.stream()
                .sorted(Comparator.comparing(EmployeeDto::getSalary).reversed())
                .limit(10)
                .map(EmployeeDto::getName)
                .toList();
    }

    @Override
    public List<EmployeeDto> searchEmployeesByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search string(name) cannot be empty");
        }
        List<EmployeeDto> employeeDtoList = integrationService.getAllEmployees();
        return employeeDtoList.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        List<EmployeeDto> employeeDtoList = integrationService.getAllEmployees();
        OptionalInt maxSalary =
                employeeDtoList.stream().mapToInt(EmployeeDto::getSalary).max();

        if (maxSalary.isPresent()) return maxSalary.getAsInt();
        else throw new EmployeeNotFoundException("No employee found with max salary");
    }
}
