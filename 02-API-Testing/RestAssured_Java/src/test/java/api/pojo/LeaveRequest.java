package api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LeaveRequest POJO - For Leave API request/response.
 *
 * @author Deep Ghevariya
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaveRequest {

    @JsonProperty("id")
    private int id;

    @JsonProperty("leaveType")
    private LeaveTypeInfo leaveType;

    @JsonProperty("employee")
    private EmployeeInfo employee;

    @JsonProperty("dates")
    private LeaveDates dates;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("status")
    private StatusInfo status;

    @JsonProperty("days")
    private double days;

    public LeaveRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LeaveTypeInfo getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveTypeInfo leaveType) { this.leaveType = leaveType; }
    public EmployeeInfo getEmployee() { return employee; }
    public void setEmployee(EmployeeInfo employee) { this.employee = employee; }
    public LeaveDates getDates() { return dates; }
    public void setDates(LeaveDates dates) { this.dates = dates; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public StatusInfo getStatus() { return status; }
    public void setStatus(StatusInfo status) { this.status = status; }
    public double getDays() { return days; }
    public void setDays(double days) { this.days = days; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeaveTypeInfo {
        @JsonProperty("id") private int id;
        @JsonProperty("name") private String name;
        public int getId() { return id; }
        public String getName() { return name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmployeeInfo {
        @JsonProperty("empNumber") private int empNumber;
        @JsonProperty("firstName") private String firstName;
        @JsonProperty("lastName") private String lastName;
        public int getEmpNumber() { return empNumber; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeaveDates {
        @JsonProperty("fromDate") private String fromDate;
        @JsonProperty("toDate") private String toDate;
        public String getFromDate() { return fromDate; }
        public String getToDate() { return toDate; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusInfo {
        @JsonProperty("id") private int id;
        @JsonProperty("name") private String name; // "Pending", "Approved", "Rejected"
        public int getId() { return id; }
        public String getName() { return name; }
    }

    @Override
    public String toString() {
        return "LeaveRequest{id=" + id + ", comment='" + comment + "', days=" + days + "}";
    }
}
