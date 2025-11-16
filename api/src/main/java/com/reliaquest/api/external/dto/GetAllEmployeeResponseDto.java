package com.reliaquest.api.external.dto;

import com.reliaquest.api.dto.EmployeeDto;
import java.util.List;
import lombok.Data;

/**
 * @author nikhilchavan
 */
@Data
public class GetAllEmployeeResponseDto {

    private List<EmployeeDto> data;

    private String status;
}
