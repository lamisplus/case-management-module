package org.lamisplus.modules.casemanagement.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CaseManagementPatientDTO {
    private Long id;
    private Long UserId;
    @NotNull(message = "programCode cannot be empty")
    private String programCode;
    private List<Long> patientIds;
    private Integer archived;
}
