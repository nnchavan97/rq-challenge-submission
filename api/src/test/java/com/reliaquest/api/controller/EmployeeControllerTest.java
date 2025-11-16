package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.service.IEmployeeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author nikhilchavan
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private IEmployeeService employeeService;

    private EmployeeDto employeeDto1;
    private EmployeeDto employeeDto2;
    private CreateEmployeeRequestDto createEmployeeRequestDto;
    private List<EmployeeDto> employeeDtoList;

    @BeforeEach
    public void setup() {
        setupTestData();
    }

    public void setupTestData() {
        // Setup test data
        employeeDto1 = new EmployeeDto();
        employeeDto1.setId(UUID.fromString("64550650-a3b9-4ca0-9dc2-80a940a68d50"));
        employeeDto1.setName("Nikhil");
        employeeDto1.setSalary(50000);

        employeeDto2 = new EmployeeDto();
        employeeDto2.setId(UUID.fromString("40fae02d-49c2-4f8c-ac23-4878de1e6f63"));
        employeeDto2.setName("Mayuri");
        employeeDto2.setSalary(60000);

        employeeDtoList = Arrays.asList(employeeDto1, employeeDto2);

        createEmployeeRequestDto = new CreateEmployeeRequestDto();
        createEmployeeRequestDto.setName("New Employee");
        createEmployeeRequestDto.setSalary(45000);
    }

    @Test
    public void testGetAllEmployeesSuccess() {

        when(employeeService.getAllEmployees()).thenReturn(employeeDtoList);
        ResponseEntity<List<EmployeeDto>> responseEntity = employeeController.getAllEmployees();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        verify(employeeService).getAllEmployees();
    }

    @Test
    public void testGetAllEmployeesEmptyList() {

        when(employeeService.getAllEmployees()).thenReturn(new ArrayList<>());
        ResponseEntity<List<EmployeeDto>> responseEntity = employeeController.getAllEmployees();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isEmpty());
        verify(employeeService).getAllEmployees();
    }

    @Test
    public void testGetAllEmployeesIntegrationServiceException() {

        when(employeeService.getAllEmployees())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        try {
            ResponseEntity<List<EmployeeDto>> responseEntity = employeeController.getAllEmployees();
        } catch (EmployeeServiceIntegrationException e) {
            assertEquals("Integration service failed", e.getMessage());
        }
        verify(employeeService).getAllEmployees();
    }

    @Test
    public void testGetAllEmployeesTooManyRequestException() {

        when(employeeService.getAllEmployees()).thenThrow(new TooManyRequestsException("Rate limit exceeded"));
        try {
            ResponseEntity<List<EmployeeDto>> responseEntity = employeeController.getAllEmployees();
        } catch (TooManyRequestsException e) {
            assertEquals("Rate limit exceeded", e.getMessage());
        }
        verify(employeeService).getAllEmployees();
    }

    @Test
    public void testGetEmployeesByNameSearchSuccess() {
        String searchString = "Nikhil";
        List<EmployeeDto> searchResults = Arrays.asList(employeeDto1);
        when(employeeService.searchEmployeesByName(searchString)).thenReturn(searchResults);

        ResponseEntity<List<EmployeeDto>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Nikhil", response.getBody().get(0).getName());
        verify(employeeService).searchEmployeesByName(searchString);
    }

    @Test
    public void testGetEmployeesByNameSearchIntegrationServiceException() {
        String searchString = "Nikhil";
        when(employeeService.searchEmployeesByName(searchString))
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        try {
            ResponseEntity<List<EmployeeDto>> response = employeeController.getEmployeesByNameSearch(searchString);
        } catch (EmployeeServiceIntegrationException e) {
            assertEquals("Integration service failed", e.getMessage());
        }
        verify(employeeService).searchEmployeesByName(searchString);
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        String id = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        when(employeeService.getEmployeeById(id)).thenReturn(employeeDto1);
        ResponseEntity<EmployeeDto> response = employeeController.getEmployeeById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nikhil", response.getBody().getName());
        verify(employeeService).getEmployeeById(id);
    }

    @Test
    public void testGetEmployeeByIdEmployeeNotFoundException() {
        String id = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        when(employeeService.getEmployeeById(id)).thenThrow(new EmployeeNotFoundException("Employee not found"));
        try {
            employeeController.getEmployeeById(id);
        } catch (EmployeeNotFoundException ex) {
            assertEquals("Employee not found", ex.getMessage());
        }
        verify(employeeService).getEmployeeById(id);
    }

    @Test
    public void testGetEmployeeByIdNullId() {
        String id = null;
        when(employeeService.getEmployeeById(id)).thenThrow(new IllegalArgumentException("Employee ID cannot be null"));
        try {
            employeeController.getEmployeeById(id);
        } catch (IllegalArgumentException ex) {
            assertEquals("Employee ID cannot be null", ex.getMessage());
        }
        verify(employeeService).getEmployeeById(id);
    }

    @Test
    public void testGetHighestSalaryOfEmployeesSuccess() {
        Integer highestSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(highestSalary, response.getBody());
        verify(employeeService).getHighestSalaryOfEmployees();
    }

    @Test
    public void testGetHighestSalaryOfEmployeesEmployeeServiceIntegrationException() {
        when(employeeService.getHighestSalaryOfEmployees())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service error"));
        try {
            employeeController.getHighestSalaryOfEmployees();
        } catch (EmployeeServiceIntegrationException ex) {
            assertEquals("Integration service error", ex.getMessage());
        }
        verify(employeeService).getHighestSalaryOfEmployees();
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesSuccess() {
        List<String> topNames = Arrays.asList("Nikhil", "Mayuri", "Ram", "Shubham", "Abhijit");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topNames);
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().size());
        assertEquals("Nikhil", response.getBody().get(0));
        verify(employeeService).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesEmployeeServiceIntegrationException() {
        when(employeeService.getTopTenHighestEarningEmployeeNames())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        try {
            employeeController.getTopTenHighestEarningEmployeeNames();
        } catch (EmployeeServiceIntegrationException ex) {
            assertEquals("Integration service failed", ex.getMessage());
        }
        verify(employeeService).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    public void testCreateEmployeeSuccess() {
        when(employeeService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenReturn(employeeDto1);
        ResponseEntity<EmployeeDto> response = employeeController.createEmployee(createEmployeeRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nikhil", response.getBody().getName());
        verify(employeeService).createEmployee(createEmployeeRequestDto);
    }

    @Test
    public void testCreateEmployeeEmployeeServiceIntegrationException() {
        when(employeeService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        try {
            employeeController.createEmployee(createEmployeeRequestDto);
        } catch (EmployeeServiceIntegrationException ex) {
            assertEquals("Integration service failed", ex.getMessage());
        }
        verify(employeeService).createEmployee(createEmployeeRequestDto);
    }

    @Test
    public void testCreateEmployeeIllegalArgumentException() {
        CreateEmployeeRequestDto invalidRequest = new CreateEmployeeRequestDto();
        invalidRequest.setName("");
        invalidRequest.setSalary(-1000);
        when(employeeService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid employee data"));
        try {
            employeeController.createEmployee(invalidRequest);
        } catch (IllegalArgumentException ex) {
            assertEquals("Invalid employee data", ex.getMessage());
        }
        verify(employeeService).createEmployee(invalidRequest);
    }

    @Test
    public void testCreateEmployeeTooManyRequestsException() {
        when(employeeService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenThrow(new TooManyRequestsException("Too many create requests"));
        try {
            employeeController.createEmployee(createEmployeeRequestDto);
        } catch (TooManyRequestsException ex) {
            assertEquals("Too many create requests", ex.getMessage());
        }
        verify(employeeService).createEmployee(createEmployeeRequestDto);
    }

    @Test
    public void testDeleteEmployeeByIdSuccess() {
        String id = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        String deletedEmployeeName = "Nikhil";
        when(employeeService.deleteEmployeeById(id)).thenReturn(deletedEmployeeName);
        ResponseEntity<String> response = employeeController.deleteEmployeeById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(deletedEmployeeName, response.getBody());
        verify(employeeService).deleteEmployeeById(id);
    }

    @Test
    public void testDeleteEmployeeByIdEmployeeNotFoundException() {
        String id = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        when(employeeService.deleteEmployeeById(id)).thenThrow(new EmployeeNotFoundException("Employee not found"));
        try {
            employeeController.deleteEmployeeById(id);
        } catch (EmployeeNotFoundException ex) {
            assertEquals("Employee not found", ex.getMessage());
        }
        verify(employeeService).deleteEmployeeById(id);
    }
}
