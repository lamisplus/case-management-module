package org.lamisplus.modules.casemanagement.repository;

import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseManagementPatientRepository extends JpaRepository<Patient, Long> , JpaSpecificationExecutor {


    @Query(value = "SELECT DISTINCT e.patient_id, p.* FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "WHERE (p.details ->>'firstName' ilike ?1 OR p.details ->>'lastName' ilike ?2 " +
            "OR p.details ->>'hospitalNumber' ilike ?3 OR p.details ->>'mobilePhoneNumber' ilike ?4 " +
            "OR p.details ->'gender' ->> 'display' ilike ?5 OR p.patient_number ilike ?3) AND " +
            "e.program_code = ?8 AND e.archived=?6 AND p.archived=?6 AND p.organisation_unit_id=?7 " +
            "AND e.organisation_unit_id=?7 " +
            "AND e.patient_id NOT IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?6)", nativeQuery = true)
    Page<Patient> findAllByPatientNotCaseManagedByFilteredParameters(String firstName, String lastName, String hospitalNumber, String mobilePhoneNumber,
                                                                     String gender, int archived, Long organisationUnitId, String programCode, Pageable pageable);

    @Query(value = "SELECT DISTINCT e.patient_id, p.* FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "WHERE e.program_code = ?1 AND e.archived=?2 AND p.archived=?2 AND p.organisation_unit_id=?3 " +
            "AND e.organisation_unit_id=?3 " +
            "AND e.patient_id NOT IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?2)", nativeQuery = true)
    Page<Patient> findAllByPatientsNotManaged(String programCode, int archived, Long organisationUnitId, Pageable pageable);

    Optional<Patient> findByHospitalNumberAndPatientNumberTypeAndOrganisationUnitIdAndArchived(String hospitalNumber, String patientNumberType, Long organisationUnitId, int archived);

    @Query(value = "SELECT DISTINCT e.patient_id, p.* FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "WHERE e.program_code = ?1 AND e.archived=?2 AND p.archived=?2 AND p.organisation_unit_id=?3 " +
            "AND e.organisation_unit_id=?3 " +
            "AND e.patient_id IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?2)", nativeQuery = true)
    Page<Patient> findAllByPatientsManaged(String programCode, int archived, Long organisationUnitId, Pageable pageable);

    @Query(value = "SELECT DISTINCT e.patient_id, p.* FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "WHERE (p.details ->>'firstName' ilike ?1 OR p.details ->>'lastName' ilike ?2 " +
            "OR p.details ->>'hospitalNumber' ilike ?3 OR p.details ->>'mobilePhoneNumber' ilike ?4 " +
            "OR p.details ->'gender' ->> 'display' ilike ?5 OR p.patient_number ilike ?3) AND " +
            "e.program_code = ?8 AND e.archived=?6 AND p.archived=?6 AND p.organisation_unit_id=?7 " +
            "AND e.organisation_unit_id=?7 " +
            "AND e.patient_id IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?6)", nativeQuery = true)
    Page<Patient> findAllByPatientManagedByFilteredParameters(String firstName, String lastName, String hospitalNumber, String mobilePhoneNumber,
                                                              String gender, int archived, Long organisationUnitId, String programCode, Pageable pageable);

    @Query(value = "SELECT p.identifier_number ->> 'identifier' " +
            "FROM patient, jsonb_array_elements(details ->'otherIdentifier') " +
            "WITH ordinality p(identifier_number) " +
            "WHERE patient.patient_number=?1 AND patient.patient_number_type = ?2 " +
            "AND p.identifier_number-> 'identifierType' ->> 'code' = ?3 " +
            "AND patient.archived = ?4 AND patient.organisation_unit_id = ?5", nativeQuery = true)
    Optional<String> findPatientIdentifierNumber(String hospitalNumber, String patientNumberType, String identifierCode, int archived, Long organisationUnitId);

    @Query(value = "SELECT p.identifier_number ->> 'identifier' " +
            "FROM patient, jsonb_array_elements(details ->'otherIdentifier') " +
            "WITH ordinality p(identifier_number) " +
            "WHERE patient.id=?1 " +
            "AND p.identifier_number-> 'identifierType' ->> 'code' = ?2 " +
            "AND patient.archived = ?3 AND patient.organisation_unit_id = ?4", nativeQuery = true)
    Optional<String> findPatientIdentifierNumberByPatientId(Long id, String identifierCode, int archived, Long organisationUnitId);

    @Query(value = "WITH enc AS " +
            "(SELECT * FROM " +
            "(SELECT e.patient_id, e.id, p.*,fd.encounter_id, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter DESC) rn FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "LEFT OUTER JOIN form_data fd ON fd.encounter_id = e.id " +
            "WHERE e.program_code = ?1 AND e.archived=?2 AND p.archived=?2 AND p.organisation_unit_id=?3 " +
            "AND e.organisation_unit_id=?3 AND e.form_code = '5210f079-27e9-4d01-a713-a2c400e0926c' " +
            "AND e.patient_id IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?2) " +
            ")m WHERE rn = ?4) " +
            "SELECT enc.art_status, enc.* FROM enc", nativeQuery = true)
    Page<Patient> findAllByPatientsManagedInHIV(String programCode, int archived, Long organisationUnitId, int rank, Pageable pageable);


    @Query(value = "WITH enc AS (SELECT * FROM " +
            "(SELECT e.patient_id, e.id, p.*,fd.encounter_id, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter, fd.id DESC) rn FROM encounter e " +
            "LEFT OUTER JOIN patient p ON p.id = e.patient_id " +
            "LEFT OUTER JOIN form_data fd ON fd.encounter_id = e.id " +
            "WHERE p.details ->'gender' ->> 'display' = ?1 AND p.details ->'state' ->> 'name' = ?2 " +
            "AND p.details ->'province' ->> 'name' = ?3 AND e.program_code = '0d31f6ee-571c-45b8-80d5-3f7e1d5377b7' " +
            "AND e.archived=?7 AND p.archived=?7 AND p.organisation_unit_id=?8 " +
            "AND e.organisation_unit_id=?8 AND e.form_code = '5210f079-27e9-4d01-a713-a2c400e0926c' " +
            "AND e.patient_id IN (SELECT DISTINCT patient_id FROM application_user_patient WHERE archived = ?7 " +
            "AND application_user_id=4) " +
            "AND date_part('year', AGE(NOW(), (p.details ->>'dob')\\:\\:timestamp))\\:\\:int <= ?5)m WHERE rn = ?9), " +
            "pregnancy_status AS " +
            "(SELECT * FROM " +
            "(SELECT visit_enc.patient_id, visit_fd.data -> 'pregnant' ->> 'display' AS pregnancy_status, " +
            "rank() over(PARTITION BY visit_enc.patient_id ORDER BY visit_enc.date_encounter DESC) rn " +
            "FROM encounter visit_enc " +
            "LEFT JOIN form_data visit_fd ON visit_fd.encounter_id = visit_enc.id " +
            "AND (visit_fd.data ->> 'date_visit' <> '') IS NOT TRUE  " +
            "AND (visit_fd.data ->> 'date_visit')\\:\\:date >= ?6 " +
            "AND visit_enc.form_code = '5c8741de-f722-4e0a-a505-24e039bf4340' " +
            "WHERE visit_fd.data -> 'pregnant' ->> 'display' IS NOT NULL ) ps " +
            "WHERE rn = 1 ) " +
            "SELECT * FROM enc " +
            "INNER JOIN pregnancy_status ON pregnancy_status.patient_id = enc.patient_id", nativeQuery = true)
    Page<Patient> findAllByPatientsManagedInHIVPregnantByFilteredParameters(String gender,
                                                                            String state,
                                                                            String lga,
                                                                            Long applicationUserId,
                                                                            int age,
                                                                            LocalDate dateNow,
                                                                            int archived,
                                                                            Long organisationUnit,
                                                                            int rank,
                                                                            Pageable pageable);


    //-- TO CHECK PATIENT INFO FOR ASSIGNED PATIENTS
    @Query(value = "(SELECT * FROM " +
            "(SELECT e.patient_id, p.*, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter, fd.id DESC) rn " +
            "FROM encounter e " +
            "INNER JOIN application_user_patient a ON a.patient_id = e.patient_id " +
            "AND a.archived = e.archived " +
            "INNER JOIN patient p ON p.id = e.patient_id " +
            "INNER JOIN form_data fd ON fd.encounter_id = e.id " +
            "WHERE " +
            "e.archived = 0 AND " +
            "p.archived=0 AND " +
            "p.organisation_unit_id=?1 AND " +
            "e.organisation_unit_id=?1 AND " +
            "e.program_code = '0d31f6ee-571c-45b8-80d5-3f7e1d5377b7' AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) >= ?2 AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) <= ?3) m WHERE rn = 1)", nativeQuery = true)
    List<Patient> findAllByPatientsManagedInHIVByFilteredParameters(Long organisationUnit, int ageFrom, int ageTo,
                                                                    Pageable pageable);

    //-- TO CHECK PATIENT INFO FOR UNASSIGNED PATIENTS & PREGNANT +
    @Query(value = "WITH enc as (SELECT * FROM " +
            "(SELECT e.patient_id, p.*, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter, fd.id DESC) rn " +
            "FROM encounter e " +
            "LEFT JOIN application_user_patient a ON a.patient_id = e.patient_id " +
            "AND a.archived = e.archived " +
            "INNER JOIN patient p ON p.id = e.patient_id " +
            "INNER JOIN form_data fd ON fd.encounter_id = e.id " +
            "WHERE " +
            "p.details ->'gender' ->> 'display' IN (?1) " +
            "AND " +
            "p.details ->'state' ->> 'name' IN (?2) " +
            "AND " +
            "p.details ->'province' ->> 'name' IN (?3) " +
            "AND " +
            "e.archived = 0 AND " +
            "p.archived=0 AND " +
            "p.organisation_unit_id=?4 AND " +
            "e.organisation_unit_id=?4 AND " +
            "e.form_code = '5210f079-27e9-4d01-a713-a2c400e0926c' AND " +
            "a.patient_id is null AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) >= ?5 AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) <= ?6) m WHERE rn = 1), " +
            "pregnancy_status as " +
            "(SELECT * FROM" +
            "(SELECT visit_enc.patient_id, " +
            "visit_fd.data -> 'pregnant' ->> 'display' AS pregnancy_status, " +
            "rank() over(PARTITION BY visit_enc.patient_id ORDER BY visit_enc.date_encounter DESC) rn " +
            "FROM encounter visit_enc " +
            "LEFT JOIN form_data visit_fd ON visit_fd.encounter_id = visit_enc.id " +
            "AND (visit_fd.data ->> 'date_visit' <> '') IS NOT TRUE " +
            "AND (visit_fd.data ->> 'date_visit')\\:\\:date >= ?7 " +
            "AND visit_enc.form_code = '5c8741de-f722-4e0a-a505-24e039bf4340' " +
            "WHERE visit_fd.data -> 'pregnant' ->> 'display' IS NOT NULL ) enc " +
            "WHERE rn = 1 )" +
            "SELECT * FROM enc " +
            "INNER JOIN pregnancy_status " +
            "ON pregnancy_status.patient_id = enc.patient_id ORDER BY enc.patient_id DESC", nativeQuery = true)
    List<Patient> findAllByPatientsNotManagedInHIVPregnantByFilteredParameters(List<String> gender, List<String> state,
                                                                               List<String> province, Long organisationUnit,
                                                                               int ageForm, int ageTo, LocalDate nineMonths, Pageable pageable);

    //-- TO CHECK PATIENT INFO FOR UNASSIGNED PATIENTS & NOT PREGNANT +
    @Query(value = "WITH enc as (SELECT * FROM " +
            "(SELECT e.patient_id, p.*, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter, fd.id DESC) rn " +
            "FROM encounter e " +
            "LEFT JOIN application_user_patient a ON a.patient_id = e.patient_id " +
            "AND a.archived = e.archived " +
            "INNER JOIN patient p ON p.id = e.patient_id " +
            "INNER JOIN form_data fd ON fd.encounter_id = e.id " +
            "WHERE " +
            "p.details ->'gender' ->> 'display' IN (?1) " +
            "AND " +
            "p.details ->'state' ->> 'name' IN (?2) " +
            "AND " +
            "p.details ->'province' ->> 'name' IN (?3) " +
            "AND " +
            "e.archived = 0 AND " +
            "p.archived=0 AND " +
            "p.organisation_unit_id=?4 AND " +
            "e.organisation_unit_id=?4 AND " +
            "e.program_code = '0d31f6ee-571c-45b8-80d5-3f7e1d5377b7'  AND " +
            "a.patient_id is null AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) >= ?5 AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) <= ?6) m WHERE rn = 1), " +
            "pregnancy_status as " +
            "(SELECT * FROM" +
            "(SELECT visit_enc.patient_id, " +
            "visit_fd.data -> 'pregnant' ->> 'display' AS pregnancy_status, " +
            "rank() over(PARTITION BY visit_enc.patient_id ORDER BY visit_enc.date_encounter DESC) rn " +
            "FROM encounter visit_enc " +
            "LEFT JOIN form_data visit_fd ON visit_fd.encounter_id = visit_enc.id " +
            "AND (visit_fd.data ->> 'date_visit' <> '') IS NOT TRUE " +
            "AND (visit_fd.data ->> 'date_visit')\\:\\:date >= ?7 " +
            "AND visit_enc.form_code = '5c8741de-f722-4e0a-a505-24e039bf4340' " +
            "WHERE visit_fd.data -> 'pregnant' ->> 'display' IS NOT NULL ) enc " +
            "WHERE rn = 1 )" +
            "SELECT * FROM enc " +
            "LEFT JOIN pregnancy_status ON " +
            "pregnancy_status.patient_id = enc.patient_id ORDER BY enc.patient_id DESC", nativeQuery = true)
    List<Patient> findAllByPatientsNotManagedInHIVNotPregnantByFilteredParameters(List<String> gender, List<String> state,
                                                                                  List<String> province, Long organisationUnit,
                                                                                  int ageForm, int ageTo, LocalDate nineMonths, Pageable pageable);

    //-- TO CHECK PATIENT INFO FOR ASSIGNED PATIENTS BY USER(CASE MANAGER) ID
    @Query(value = "(SELECT * FROM " +
            "(SELECT e.patient_id, p.*, " +
            "fd.data -> 'hiv_current_status' ->> 'display'  as art_status, " +
            "rank() over(PARTITION BY e.patient_id ORDER BY e.date_encounter, fd.id DESC) rn " +
            "FROM encounter e " +
            "INNER JOIN application_user_patient a ON a.patient_id = e.patient_id " +
            "AND a.archived = e.archived " +
            "INNER JOIN patient p ON p.id = e.patient_id " +
            "INNER JOIN form_data fd ON fd.encounter_id = e.id " +
            "INNER JOIN application_user u ON a.application_user_id = u.id " +
            "WHERE " +
            "e.archived = 0 AND " +
            "p.archived=0 AND " +
            "p.organisation_unit_id=?1 AND " +
            "e.organisation_unit_id=?1 AND " +
            "e.program_code = '0d31f6ee-571c-45b8-80d5-3f7e1d5377b7'  AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) >= ?2 AND " +
            "EXTRACT(YEAR from AGE(NOW(), (p.details ->> 'dob')\\:\\:date)) <= ?3 AND " +
            "u.id=?4) m WHERE rn = 1)", nativeQuery = true)
    List<Patient> findAllByPatientsManagedInHIVByFilteredParametersByApplicationUserId(Long organisationUnit, int ageFrom, int ageTo,
                                                                                       Long applicationUserId, Pageable pageable);





}