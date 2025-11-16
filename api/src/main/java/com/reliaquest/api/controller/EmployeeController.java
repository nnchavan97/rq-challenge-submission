package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.service.IEmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for Employee API
 * @author nikhilchavan
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<EmployeeDto, CreateEmployeeRequestDto> {

    private final IEmployeeService employeeService;

    @Override
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Received API request to get All employees");
        List<EmployeeDto> employeeDtoList = employeeService.getAllEmployees();
        return new ResponseEntity<>(employeeDtoList, HttpStatus.OK);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(
            @PathVariable("searchString") String searchString) {
        log.info("Received API request to search employees by name: {}", searchString);
        List<EmployeeDto> employeeDtoList = employeeService.searchEmployeesByName(searchString);
        return new ResponseEntity<>(employeeDtoList, HttpStatus.OK);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") String id) {
        log.info("Received API request to get employee by id: {}", id);
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Received API request to get the highest salary of employee");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Received API request to get the Top 10 highest salaried employees");
        List<String> names = employeeService.getTopTenHighestEarningEmployeeNames();
        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @Override
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody CreateEmployeeRequestDto employeeInput) {
        log.info("Received API request to create employee");
        EmployeeDto employeeDto = employeeService.createEmployee(employeeInput);
        return new ResponseEntity<>(employeeDto, HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("Received API request to delete employee by id: {}", id);
        String employeeName = employeeService.deleteEmployeeById(id);
        return new ResponseEntity<>(employeeName, HttpStatus.OK);
    }
}
