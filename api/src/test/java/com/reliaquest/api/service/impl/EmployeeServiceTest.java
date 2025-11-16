package com.reliaquest.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.external.EmployeeIntegrationService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author nikhilchavan
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeIntegrationService integrationService;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto employeeDto1;
    private EmployeeDto employeeDto2;
    private CreateEmployeeRequestDto createEmployeeRequestDto;
    private List<EmployeeDto> employeeDtoList;
    private String validUUIDString;
    private UUID validUUID;

    @BeforeEach
    public void setUp() {
        // Setup test data
        validUUIDString = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        validUUID = UUID.fromString(validUUIDString);

        employeeDto1 = new EmployeeDto();
        employeeDto1.setId(UUID.fromString("64550650-a3b9-4ca0-9dc2-80a940a68d50"));
        employeeDto1.setName("Nikhil");
        employeeDto1.setSalary(70000);

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
        when(integrationService.getAllEmployees()).thenReturn(employeeDtoList);
        List<EmployeeDto> result = employeeService.getAllEmployees();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetAllEmployeesIntegrationServiceThrowsException() {
        when(integrationService.getAllEmployees()).thenThrow(new RuntimeException("Integration service error"));
        assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees());
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        when(integrationService.getEmployeeById(validUUID)).thenReturn(employeeDto1);
        EmployeeDto result = employeeService.getEmployeeById(validUUIDString);
        assertNotNull(result);
        assertEquals("Nikhil", result.getName());
        verify(integrationService, times(1)).getEmployeeById(validUUID);
    }

    @Test
    public void testGetEmployeeByIdInvalidUUIDFormat() {
        String invalidUUID = "abcd";
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(invalidUUID));
        assertEquals(
                "Invalid employee id : " + invalidUUID + ", Requires employee id in UUID format.",
                exception.getMessage());
        verify(integrationService, never()).getEmployeeById(any(UUID.class));
    }

    @Test
    public void testGetEmployeeByIdIntegrationServiceThrowsException() {
        when(integrationService.getEmployeeById(validUUID))
                .thenThrow(new RuntimeException("Integration service error"));
        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(validUUIDString));
        verify(integrationService, times(1)).getEmployeeById(validUUID);
    }

    @Test
    public void testCreateEmployeeSuccess() {
        when(integrationService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenReturn(employeeDto1);
        EmployeeDto result = employeeService.createEmployee(createEmployeeRequestDto);
        assertNotNull(result);
        assertEquals("Nikhil", result.getName());
        verify(integrationService, times(1)).createEmployee(createEmployeeRequestDto);
    }

    @Test
    public void testCreateEmployeeIntegrationServiceThrowsException() {
        when(integrationService.createEmployee(any(CreateEmployeeRequestDto.class)))
                .thenThrow(new RuntimeException("Integration service error"));
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(createEmployeeRequestDto));
        verify(integrationService, times(1)).createEmployee(createEmployeeRequestDto);
    }

    @Test
    public void testDeleteEmployeeByIdSuccess() {
        when(integrationService.getEmployeeById(validUUID)).thenReturn(employeeDto1);
        when(integrationService.deleteEmployeeByName("Nikhil")).thenReturn(true);
        String result = employeeService.deleteEmployeeById(validUUIDString);
        assertNotNull(result);
        assertEquals("Nikhil", result);
        verify(integrationService, times(1)).getEmployeeById(validUUID);
        verify(integrationService, times(1)).deleteEmployeeByName("Nikhil");
    }

    @Test
    public void testDeleteEmployeeByIdDeleteReturnsFalse() {
        when(integrationService.getEmployeeById(validUUID)).thenReturn(employeeDto1);
        when(integrationService.deleteEmployeeByName("Nikhil")).thenReturn(false);
        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(validUUIDString));
        assertEquals("Employee with id: " + validUUIDString + " not found", exception.getMessage());
        verify(integrationService, times(1)).getEmployeeById(validUUID);
        verify(integrationService, times(1)).deleteEmployeeByName("Nikhil");
    }

    @Test
    public void testDeleteEmployeeByIdGetEmployeeByIdThrowsException() {
        when(integrationService.getEmployeeById(validUUID))
                .thenThrow(new EmployeeNotFoundException("Employee not found"));
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(validUUIDString));
        verify(integrationService, times(1)).getEmployeeById(validUUID);
        verify(integrationService, never()).deleteEmployeeByName(anyString());
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesSuccess() {
        List<EmployeeDto> employees = Arrays.asList(employeeDto1, employeeDto2);
        when(integrationService.getAllEmployees()).thenReturn(employees);
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nikhil", result.get(0));
        assertEquals("Mayuri", result.get(1));
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNamesIntegrationServiceThrowsException() {
        when(integrationService.getAllEmployees())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        assertThrows(
                EmployeeServiceIntegrationException.class,
                () -> employeeService.getTopTenHighestEarningEmployeeNames());
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testSearchEmployeesByNameSuccess() {
        when(integrationService.getAllEmployees()).thenReturn(employeeDtoList);
        List<EmployeeDto> result = employeeService.searchEmployeesByName("Nikhil");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nikhil", result.get(0).getName());
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testSearchEmployeesByNameIntegrationServiceThrowsException() {
        when(integrationService.getAllEmployees())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        assertThrows(EmployeeServiceIntegrationException.class, () -> employeeService.searchEmployeesByName("Nikhil"));
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetHighestSalaryOfEmployees_Success() {
        when(integrationService.getAllEmployees()).thenReturn(employeeDtoList);
        Integer result = employeeService.getHighestSalaryOfEmployees();
        assertNotNull(result);
        assertEquals(Integer.valueOf(70000), result);
        verify(integrationService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetHighestSalaryOfEmployeesIntegrationServiceThrowsException() {
        when(integrationService.getAllEmployees())
                .thenThrow(new EmployeeServiceIntegrationException("Integration service failed"));
        assertThrows(EmployeeServiceIntegrationException.class, () -> employeeService.getHighestSalaryOfEmployees());
        verify(integrationService, times(1)).getAllEmployees();
    }
}
