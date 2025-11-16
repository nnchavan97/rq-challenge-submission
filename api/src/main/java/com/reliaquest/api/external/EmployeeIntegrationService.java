package com.reliaquest.api.external;

import com.reliaquest.api.dto.CreateEmployeeRequestDto;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.external.dto.*;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

/**
 * This class contains the methods which make calls to external employee service
 * @author nikhilchavan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeIntegrationService {

    private final WebClient employeeServiceExternalClient;

    @Retryable(
            retryFor = {TooManyRequestsException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000))
    public List<EmployeeDto> getAllEmployees() {

        log.info("Integration service : Calling get all employees");
        try {
            ResponseEntity<GetAllEmployeeResponseDto> responseDto = employeeServiceExternalClient
                    .get()
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(GetAllEmployeeResponseDto.class))
                    .block();
            HttpStatus status = HttpStatus.valueOf(responseDto.getStatusCode().value());
            switch (status) {
                case OK:
                    GetAllEmployeeResponseDto allEmployeeResponseDto = responseDto.getBody();
                    log.info(
                            "Successfully fetched {} employee records from external api",
                            allEmployeeResponseDto.getData().size());
                    return allEmployeeResponseDto.getData();
                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException("Received too many requests. Please try again later.");
                default:
                    log.error("Error occurred while fetching All employees data. Status code returned: {}", status);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. " + "Status code returned: " + status);
            }
        } catch (WebClientException wce) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred while connecting to external service. Please try again later.");
        }
    }

    @Retryable(
            retryFor = {TooManyRequestsException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000))
    public EmployeeDto getEmployeeById(UUID id) {

        log.info("Integration service : Calling get employee by id");
        try {
            ResponseEntity<EmployeeResponseDto> employeeResponseDto = employeeServiceExternalClient
                    .get()
                    .uri("/{id}", id)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(EmployeeResponseDto.class))
                    .block();

            HttpStatus status =
                    HttpStatus.valueOf(employeeResponseDto.getStatusCode().value());

            switch (status) {
                case OK:
                    log.info("Successfully fetched employee data with id : {}", id);
                    return employeeResponseDto.getBody().getData();
                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException("Received too many requests. Please try again later.");
                case NOT_FOUND:
                    throw new EmployeeNotFoundException("Employee with ID : " + id + " not found.");
                default:
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. " + "Status code returned: " + status);
            }
        } catch (WebClientException wce) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred while connecting to external service. Please try again later.");
        }
    }

    @Retryable(
            retryFor = {TooManyRequestsException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000))
    public EmployeeDto createEmployee(CreateEmployeeRequestDto employeeRequestDto) {

        log.info("Integration service : Calling create employee api");
        try {
            ResponseEntity<CreateEmployeeResponseDto> responseEntity = employeeServiceExternalClient
                    .post()
                    .body(Mono.just(employeeRequestDto), CreateEmployeeRequestDto.class)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(CreateEmployeeResponseDto.class))
                    .block();

            HttpStatus status =
                    HttpStatus.valueOf(responseEntity.getStatusCode().value());

            switch (status) {
                case OK:
                    return responseEntity.getBody().getData();
                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException("Received too many requests. Please try again later.");
                default:
                    log.error("Error occurred while fetching employees data. Status code returned: {}", status);
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. " + "Status code returned: " + status);
            }
        } catch (WebClientException wce) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred while connecting to external service. Please try again later.");
        }
    }

    @Retryable(
            retryFor = {TooManyRequestsException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 20000))
    public boolean deleteEmployeeByName(String name) {

        log.info("Integration service : calling api to delete employee with name");
        try {
            DeleteEmployeeRequestDto deleteEmployeeRequestDto = new DeleteEmployeeRequestDto();
            deleteEmployeeRequestDto.setName(name);
            ResponseEntity<DeleteEmployeeResponseDto> responseEntity = employeeServiceExternalClient
                    .method(HttpMethod.DELETE)
                    .body(Mono.just(deleteEmployeeRequestDto), DeleteEmployeeRequestDto.class)
                    .exchangeToMono(clientResponse -> clientResponse.toEntity(DeleteEmployeeResponseDto.class))
                    .block();

            HttpStatus status =
                    HttpStatus.valueOf(responseEntity.getStatusCode().value());

            switch (status) {
                case OK:
                    return responseEntity.getBody().isData();
                case TOO_MANY_REQUESTS:
                    throw new TooManyRequestsException("Received too many requests. Please try again after later.");
                default:
                    throw new EmployeeServiceIntegrationException(
                            "Error occurred while fetching All employees data. " + "Status code returned: " + status);
            }
        } catch (WebClientException wce) {
            throw new EmployeeServiceIntegrationException(
                    "Error occurred while connecting to external service. Please try again later.");
        }
    }
}
