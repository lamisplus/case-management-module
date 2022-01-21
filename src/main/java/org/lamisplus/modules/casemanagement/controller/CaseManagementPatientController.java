package org.lamisplus.modules.casemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.controller.apierror.IllegalTypeException;
import org.lamisplus.modules.base.domain.dto.*;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.service.PatientService;
import org.lamisplus.modules.base.util.PaginationUtil;
import org.lamisplus.modules.casemanagement.domain.dto.CaseManagementPatientDTO;
import org.lamisplus.modules.casemanagement.domain.entity.CaseManagementPatient;
import org.lamisplus.modules.casemanagement.service.CaseManagementPatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/case-management-patients")
public class CaseManagementPatientController {
    private final CaseManagementPatientService caseManagementPatientService;
    private final PatientService patientService;



    @GetMapping("/{programCode}/{managed}/programs")
    public ResponseEntity<List<PatientDTO>> getPatientsNotManagedByProgramCode(@PathVariable String programCode,
                                                                               @PathVariable Boolean managed,
                                                                               @RequestParam (required = false, defaultValue = "*")  String gender,
                                                                               @RequestParam (required = false, defaultValue = "*") String state,
                                                                               @RequestParam (required = false, defaultValue = "*")String lga,
                                                                               @RequestParam (required = false, defaultValue = "0")Long applicationUserId,
                                                                               @RequestParam (required = false, defaultValue = "0")Integer ageFrom,
                                                                               @RequestParam (required = false, defaultValue = "200")Integer ageTo,
                                                                               @RequestParam (required = false, defaultValue = "false") Boolean pregnant,
                                                                               @PageableDefault(value = 100) Pageable pageable) {
        Page<Patient> page;
        if((ageFrom instanceof Integer && ageTo instanceof Integer) == true && ageFrom > ageTo){
            throw new IllegalTypeException(Patient.class, "Age", "not valid");
        }
        if(ageFrom > 0 && ageTo == 200){
            ageTo = ageFrom;
        }
        if(ageFrom == null && ageTo == null){
            ageTo = 200;
            ageFrom = 0;
        }
        if(((pregnant instanceof Boolean) == true && pregnant) && gender.equalsIgnoreCase("Male")){
            throw new IllegalTypeException(Patient.class, "Male & Pregnant", "not valid");
        }

        if(managed) {
            page = caseManagementPatientService.findAllByPatientManagedByFilteredParameters(gender, state, lga, ageTo, ageFrom, applicationUserId, pageable);
        } else {
            page = caseManagementPatientService.findAllByPatientNotManagedByFilteredParameters(programCode, gender, state, lga, pregnant, ageFrom, ageTo, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(patientService.getAllPatients(page), headers, HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<List<CaseManagementPatient>> save(@Valid @RequestBody CaseManagementPatientDTO caseManagementPatientDTO) {
        return ResponseEntity.ok(caseManagementPatientService.save(caseManagementPatientDTO));

    }

    @PostMapping("/unssign")
    public ResponseEntity<List<CaseManagementPatient>> unAssignCaseManagerToPatient (@Valid @RequestBody CaseManagementPatientDTO caseManagementPatientDTO) {
        return ResponseEntity.ok(caseManagementPatientService.unAssignCaseManagerToPatient(caseManagementPatientDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<CaseManagementPatientDTO> update(@PathVariable Long id, @RequestBody CaseManagementPatientDTO caseManagementPatientDTO) {
        return ResponseEntity.ok(caseManagementPatientService.update(id, caseManagementPatientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(@PathVariable Long id) {
        return ResponseEntity.ok(caseManagementPatientService.delete(id));
    }
}
