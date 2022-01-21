package org.lamisplus.modules.casemanagement.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lamisplus.modules.base.domain.entity.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "application_user_patient")
public class CaseManagementPatient extends ApplicationUserPatient {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "application_user_id")
    @NotNull(message = "userId cannot be null")
    private Long userId;

    @Basic
    @Column(name = "patient_id")
    @NotNull(message = "patientId cannot be null")
    private Long patientId;

    @Basic
    @Column(name = "archived")
    @JsonIgnore
    private int archived = 0;

    @Basic
    @Column(name = "organisation_unit_id", updatable = false)
    private Long organisationUnitId;

    @Basic
    @Column(name = "program_code")
    private String programCode;

    @ManyToOne
    @JoinColumn(name = "application_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonIgnore
    private User applicationUserByApplicationUserId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonIgnore
    private Patient patientByPatientId;

    @ManyToOne
    @JoinColumn(name = "organisation_unit_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    @JsonIgnore
    private OrganisationUnit organisationUnitByOrganisationUnitId;

    public CaseManagementPatient(Long userId, Long patientId){
        this.userId = userId;
        this.patientId = patientId;
    }
}
