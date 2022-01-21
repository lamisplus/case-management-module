package org.lamisplus.modules.casemanagement.domain.mapper;

import org.lamisplus.modules.casemanagement.domain.dto.CaseManagementPatientDTO;
import org.lamisplus.modules.casemanagement.domain.entity.CaseManagementPatient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaseManagementPatientMapper {

    CaseManagementPatient toCaseManagementPatient(CaseManagementPatientDTO caseManagementPatientDTO);

    CaseManagementPatientDTO toCaseManagementPatientDTO(CaseManagementPatient caseManagementPatient);
}
