package com.reliaquest.api.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.external.dto.CreateEmployeeResponseDto;
import com.reliaquest.api.external.dto.DeleteEmployeeResponseDto;
import com.reliaquest.api.external.dto.EmployeeResponseDto;
import com.reliaquest.api.external.dto.GetAllEmployeeResponseDto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author nikhilchavan
 */
@ExtendWith(MockitoExtension.class)
public class EmployeeIntegrationServiceTest {

    @Mock
    private WebClient employeeServiceExternalClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeIntegrationService employeeIntegrationService;

    private EmployeeDto employeeDto1;
    private EmployeeDto employeeDto2;
    private CreateEmployeeRequestDto createEmployeeRequestDto;
    private String validUUIDString;
    private UUID validUUID;
    private String employeeName;

    @BeforeEach
    public void setUp() {
        // Setup test data
        validUUIDString = "64550650-a3b9-4ca0-9dc2-80a940a68d50";
        validUUID = UUID.fromString(validUUIDString);
        employeeName = "Nikhil";

        employeeDto1 = new EmployeeDto();
        employeeDto1.setId(UUID.fromString("64550650-a3b9-4ca0-9dc2-80a940a68d50"));
        employeeDto1.setName("Nikhil");
        employeeDto1.setSalary(70000);

        employeeDto2 = new EmployeeDto();
        employeeDto2.setId(UUID.fromString("40fae02d-49c2-4f8c-ac23-4878de1e6f63"));
        employeeDto2.setName("Mayuri");
        employeeDto2.setSalary(60000);

        createEmployeeRequestDto = new CreateEmployeeRequestDto();
        createEmployeeRequestDto.setName("New Employee");
        createEmployeeRequestDto.setSalary(45000);
    }

    @Test
    public void testGetAllEmployeesSuccess() {
        List<EmployeeDto> employeeList = Arrays.asList(employeeDto1, employeeDto2);
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(employeeList);
        ResponseEntity<GetAllEmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        List<EmployeeDto> result = employeeIntegrationService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Nikhil", result.get(0).getName());
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testGetAllEmployeesTooManyRequests() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(new ArrayList<>());
        ResponseEntity<GetAllEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.TOO_MANY_REQUESTS);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        TooManyRequestsException exception =
                assertThrows(TooManyRequestsException.class, () -> employeeIntegrationService.getAllEmployees());
        assertEquals("Received too many requests. Please try again later.", exception.getMessage());
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testGetAllEmployeesInternalServerError() {
        GetAllEmployeeResponseDto responseDto = new GetAllEmployeeResponseDto();
        responseDto.setData(new ArrayList<>());
        ResponseEntity<GetAllEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeServiceIntegrationException exception = assertThrows(
                EmployeeServiceIntegrationException.class, () -> employeeIntegrationService.getAllEmployees());
        assertTrue(exception.getMessage().contains("Error occurred while fetching All employees data"));
        assertTrue(exception.getMessage().contains("INTERNAL_SERVER_ERROR"));
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setData(employeeDto1);
        ResponseEntity<EmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeDto result = employeeIntegrationService.getEmployeeById(validUUID);
        assertNotNull(result);
        assertEquals("Nikhil", result.getName());
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testGetEmployeeByIdNotFound() {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        ResponseEntity<EmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class, () -> employeeIntegrationService.getEmployeeById(validUUID));
        assertEquals("Employee with ID : " + validUUID + " not found.", exception.getMessage());
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testGetEmployeeByIdTooManyRequests() {
        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        ResponseEntity<EmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.TOO_MANY_REQUESTS);

        when(employeeServiceExternalClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        TooManyRequestsException exception = assertThrows(
                TooManyRequestsException.class, () -> employeeIntegrationService.getEmployeeById(validUUID));
        assertEquals("Received too many requests. Please try again later.", exception.getMessage());
        verify(employeeServiceExternalClient, times(1)).get();
    }

    @Test
    public void testCreateEmployeeSuccess() {
        CreateEmployeeResponseDto responseDto = new CreateEmployeeResponseDto();
        responseDto.setData(employeeDto1);
        ResponseEntity<CreateEmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeServiceExternalClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeDto result = employeeIntegrationService.createEmployee(createEmployeeRequestDto);

        assertNotNull(result);
        assertEquals("Nikhil", result.getName());
        verify(employeeServiceExternalClient, times(1)).post();
    }

    @Test
    public void testCreateEmployeeTooManyRequests() {
        CreateEmployeeResponseDto responseDto = new CreateEmployeeResponseDto();
        ResponseEntity<CreateEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.TOO_MANY_REQUESTS);

        when(employeeServiceExternalClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        TooManyRequestsException exception = assertThrows(
                TooManyRequestsException.class,
                () -> employeeIntegrationService.createEmployee(createEmployeeRequestDto));
        assertEquals("Received too many requests. Please try again later.", exception.getMessage());
        verify(employeeServiceExternalClient, times(1)).post();
    }

    @Test
    public void testCreateEmployeeBadRequest() {
        CreateEmployeeResponseDto responseDto = new CreateEmployeeResponseDto();
        ResponseEntity<CreateEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

        when(employeeServiceExternalClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeServiceIntegrationException exception = assertThrows(
                EmployeeServiceIntegrationException.class,
                () -> employeeIntegrationService.createEmployee(createEmployeeRequestDto));
        assertTrue(exception.getMessage().contains("BAD_REQUEST"));
        verify(employeeServiceExternalClient, times(1)).post();
    }

    @Test
    public void testDeleteEmployeeByNameSuccessReturnsTrue() {
        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(true);
        ResponseEntity<DeleteEmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeServiceExternalClient.method(any(HttpMethod.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        boolean result = employeeIntegrationService.deleteEmployeeByName(employeeName);

        assertTrue(result);
        verify(employeeServiceExternalClient, times(1)).method(HttpMethod.DELETE);
    }

    @Test
    public void testDeleteEmployeeByNameSuccessReturnsFalse() {
        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(false);
        ResponseEntity<DeleteEmployeeResponseDto> responseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(employeeServiceExternalClient.method(any(HttpMethod.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        boolean result = employeeIntegrationService.deleteEmployeeByName(employeeName);

        assertTrue(!result);
        verify(employeeServiceExternalClient, times(1)).method(HttpMethod.DELETE);
    }

    @Test
    public void testDeleteEmployeeByNameTooManyRequests() {
        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(false);
        ResponseEntity<DeleteEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.TOO_MANY_REQUESTS);

        when(employeeServiceExternalClient.method(any(HttpMethod.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        TooManyRequestsException exception = assertThrows(
                TooManyRequestsException.class, () -> employeeIntegrationService.deleteEmployeeByName(employeeName));
        assertEquals("Received too many requests. Please try again after later.", exception.getMessage());
        verify(employeeServiceExternalClient, times(1)).method(HttpMethod.DELETE);
    }

    @Test
    public void testDeleteEmployeeByNameBadRequest() {
        DeleteEmployeeResponseDto responseDto = new DeleteEmployeeResponseDto();
        responseDto.setData(false);
        ResponseEntity<DeleteEmployeeResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);

        when(employeeServiceExternalClient.method(any(HttpMethod.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.exchangeToMono(any())).thenReturn(Mono.just(responseEntity));

        EmployeeServiceIntegrationException exception = assertThrows(
                EmployeeServiceIntegrationException.class,
                () -> employeeIntegrationService.deleteEmployeeByName(employeeName));
        assertTrue(exception.getMessage().contains("BAD_REQUEST"));
        verify(employeeServiceExternalClient, times(1)).method(HttpMethod.DELETE);
    }
}
