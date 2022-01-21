package org.lamisplus.modules.casemanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.controller.apierror.EntityNotFoundException;
import org.lamisplus.modules.base.controller.apierror.RecordExistException;
import org.lamisplus.modules.base.domain.dto.UserDTO;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.domain.mapper.*;
import org.lamisplus.modules.base.repository.ApplicationCodesetRepository;
import org.lamisplus.modules.base.repository.ApplicationUserPatientRepository;
import org.lamisplus.modules.base.repository.OrganisationUnitRepository;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.base.service.UserService;
import org.lamisplus.modules.casemanagement.domain.dto.CaseManagementPatientDTO;
import org.lamisplus.modules.casemanagement.repository.CaseManagementPatientRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.lamisplus.modules.casemanagement.domain.entity.CaseManagementPatient;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CaseManagementPatientService {
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final OrganisationUnitRepository organisationUnitRepository;
    private final ApplicationCodesetRepository applicationCodesetRepository;
    private static final int UN_ARCHIVED = 0;
    private static final int ARCHIVED = 1;
    private final ApplicationUserPatientRepository applicationUserPatientRepository;

    //Patients Not Managed
    public Page<Patient> findAllByPatientNotManagedByFilteredParameters(String programCode, String gender, String state, String lga,
                                                                        Boolean pregnant, Integer ageFrom, Integer ageTo, Pageable pageable) {
        LocalDate currentMonth = YearMonth.now().atEndOfMonth();
        LocalDate nineMonths = currentMonth.minusMonths(9);
        List<String> genders = new ArrayList<>();
        List<String> states = new ArrayList<>();
        List<String> provinces = new ArrayList<>();
        List<Patient> patients;
        if(gender == null || gender.equalsIgnoreCase("*")){
            if(pregnant){
                genders.add("Female");
            } else {
                genders = applicationCodesetRepository.findAllGender();
            }
        }else {
            genders.add(gender);
        }

        if(state == null || state.equalsIgnoreCase("*")){
            states = organisationUnitRepository.findAllState();
        }else {
            states.add(state);
        }

        if(lga == null || lga.equalsIgnoreCase("*")){
            provinces = organisationUnitRepository.findAllProvince();
        }else {
            provinces.add(lga);
        }

        if(pregnant) {
            patients = patientRepository.findAllByPatientsNotManagedInHIVPregnantByFilteredParameters(genders, states, provinces,
                    getOrganisationUnitId(), ageFrom, ageTo, nineMonths, pageable)
                    .stream()
                    .collect(Collectors.toList());
        }else {
            patients = patientRepository.findAllByPatientsNotManagedInHIVNotPregnantByFilteredParameters(genders, states, provinces,
                    getOrganisationUnitId(), ageFrom, ageTo, nineMonths, pageable)
                    .stream()
                    .collect(Collectors.toList());
        }

        LOG.info("patients size {}", patients.size());
        //LOG.info("patientList size {}", patientList.size());
        Page page = new PageImpl<Patient>(patients, pageable, pageable.getPageSize());
        LOG.info("patients page size {}", page.getContent().size());
        return page;
    }


    //Patients Managed
    public Page<Patient> findAllByPatientManagedByFilteredParameters(String gender, String state, String lga,
                                                                     Integer ageTo, Integer ageFrom, Long applicationUserId, Pageable pageable) {
        List<Patient> patients;

        if(applicationUserId == null || applicationUserId == 0) {
            patients = patientRepository.findAllByPatientsManagedInHIVByFilteredParameters(getOrganisationUnitId(), ageFrom, ageTo, pageable)
                    .stream()
                    .collect(Collectors.toList());
        } else {
            patients = patientRepository.findAllByPatientsManagedInHIVByFilteredParametersByApplicationUserId(getOrganisationUnitId(), ageFrom, ageTo, applicationUserId, pageable)
                    .stream()
                    .collect(Collectors.toList());
        }
        LOG.info("patients size {}", patients.size());
        return new PageImpl<Patient>(patients, pageable, pageable.getPageSize());
    }



    private Long getOrganisationUnitId() {
        return userService.getUserWithRoles().get().getCurrentOrganisationUnitId();
    }

    public List<CaseManagementPatient> save(CaseManagementPatientDTO caseManagementPatientDTO) {
        Long orgUnitId = userService.getUserWithRoles().get().getCurrentOrganisationUnitId();
        Long userId = caseManagementPatientDTO.getUserId();
        String programCode = caseManagementPatientDTO.getProgramCode();
        List<CaseManagementPatient> caseManagementPatients = new ArrayList<>();

        caseManagementPatientDTO.getPatientIds().forEach(patientId ->{
            applicationUserPatientRepository.findAllByPatientIdAndUserIdAndProgramCodeAndArchived(patientId, userId, programCode, UN_ARCHIVED)
                    .ifPresent(applicationUserPatient -> {
                        throw new RecordExistException(CaseManagementPatient.class,"patientId & userId:",patientId+" & " + userId +" in same program");
                    });
            CaseManagementPatient caseManagementPatient = new CaseManagementPatient(userId, patientId);
            caseManagementPatient.setOrganisationUnitId(orgUnitId);
            caseManagementPatient.setProgramCode(programCode);
            caseManagementPatient.setArchived(UN_ARCHIVED);
            caseManagementPatients.add(caseManagementPatient);
        });
        return applicationUserPatientRepository.saveAll(caseManagementPatients);
    }

    public CaseManagementPatientDTO update(Long id, CaseManagementPatientDTO caseManagementPatientDTO) {
        applicationUserPatientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(CaseManagementPatient.class,"id:",id+""));

        List<CaseManagementPatient> caseManagementPatients = new ArrayList<>();
        caseManagementPatientDTO.getPatientIds().forEach(patientId ->{
            caseManagementPatients.add(new CaseManagementPatient(caseManagementPatientDTO.getUserId(), patientId));
        });
        applicationUserPatientRepository.saveAll(caseManagementPatients);
        return caseManagementPatientDTO;
    }

    public List<CaseManagementPatient> unAssignCaseManagerToPatient(CaseManagementPatientDTO caseManagementPatientDTO) {
        Long userId = caseManagementPatientDTO.getUserId();

        List<CaseManagementPatient> caseManagementPatients = new ArrayList<>();
        caseManagementPatientDTO.getPatientIds().forEach(patientId ->{
            CaseManagementPatient caseManagementPatient = (CaseManagementPatient) applicationUserPatientRepository.findAllByPatientIdAndUserIdAndArchived(patientId, userId, UN_ARCHIVED)
                    .orElseThrow(() -> new EntityNotFoundException(CaseManagementPatient.class,"patientId & userId:",patientId+" & " + userId));
            caseManagementPatient.setArchived(ARCHIVED);
            caseManagementPatients.add(caseManagementPatient);
        });
        return applicationUserPatientRepository.saveAll(caseManagementPatients);
    }

    public Integer delete(Long id){
        CaseManagementPatient caseManagementPatient = (CaseManagementPatient) applicationUserPatientRepository.findByIdAndArchived(id, UN_ARCHIVED)
                .orElseThrow(() -> new EntityNotFoundException(CaseManagementPatient.class,"Id:",id+""));
        caseManagementPatient.setArchived(1);
        applicationUserPatientRepository.save(caseManagementPatient);
        return caseManagementPatient.getArchived();
    }

    /*private Boolean checkFilterParameters(String patientDetails, String gender, String state, String lga){
        JsonNode tree = null;
        JsonNode jsonNode;
        Boolean find = false;
        LOG.info("In checkFilterParameters......");

        try {
            tree = mapper.readTree(patientDetails).get("gender");
            jsonNode = tree.get("display");
            String gen = String.valueOf(jsonNode).replaceAll("^\"+|\"+$", "");

            tree = mapper.readTree(patientDetails).get("province");
            jsonNode = tree.get("name");
            String localGovt = String.valueOf(jsonNode).replaceAll("^\"+|\"+$", "");

            tree = mapper.readTree(patientDetails).get("state");
            jsonNode = tree.get("name");
            String st= String.valueOf(jsonNode).replaceAll("^\"+|\"+$", "");

            if(gender != "*" && gender.equalsIgnoreCase(gen)) {
                find = true;
            }else if(gender == "*"){
                find = true;
            }
            if(localGovt != "*" && localGovt.equalsIgnoreCase(lga)) {
                find = true;
            } else if(localGovt == "*"){
                find = true;
            }
            if(st != "*" && st.equalsIgnoreCase(lga)) {
                find = true;
            } else if(st == "*"){
                find = true;
            }
            return find;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }*/
}