package api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Employee POJO - Request/Response model for Employee API.
 *
 * @author Deep Ghevariya
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @JsonProperty("empNumber")
    private int empNumber;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("jobTitleName")
    private String jobTitleName;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("workEmail")
    private String workEmail;

    @JsonProperty("mobilePhone")
    private String mobilePhone;

    @JsonProperty("status")
    private String status;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("nationalityId")
    private int nationalityId;

    // ════════════════════════════════════════════════
    //  CONSTRUCTORS
    // ════════════════════════════════════════════════

    public Employee() {}

    public Employee(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    // ════════════════════════════════════════════════
    //  Builder pattern
    // ════════════════════════════════════════════════

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Employee emp = new Employee();

        public Builder firstName(String v)   { emp.firstName = v; return this; }
        public Builder middleName(String v)  { emp.middleName = v; return this; }
        public Builder lastName(String v)    { emp.lastName = v; return this; }
        public Builder employeeId(String v)  { emp.employeeId = v; return this; }
        public Builder workEmail(String v)   { emp.workEmail = v; return this; }
        public Builder mobilePhone(String v) { emp.mobilePhone = v; return this; }
        public Builder status(String v)      { emp.status = v; return this; }
        public Builder gender(String v)      { emp.gender = v; return this; }
        public Builder dateOfBirth(String v) { emp.dateOfBirth = v; return this; }
        public Employee build() { return emp; }
    }

    // ════════════════════════════════════════════════
    //  GETTERS / SETTERS
    // ════════════════════════════════════════════════

    public int getEmpNumber() { return empNumber; }
    public void setEmpNumber(int empNumber) { this.empNumber = empNumber; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getJobTitleName() { return jobTitleName; }
    public void setJobTitleName(String jobTitleName) { this.jobTitleName = jobTitleName; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getWorkEmail() { return workEmail; }
    public void setWorkEmail(String workEmail) { this.workEmail = workEmail; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Employee{" +
                "empNumber=" + empNumber +
                ", employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
