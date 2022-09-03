import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentDashboard {
    @JsonProperty
    Integer id;

    @JsonProperty
    String first_name;

    @JsonProperty
    String last_name;

    @JsonProperty
    List<Group> groups;

    @JsonProperty
    List<StudentDashboardAssignment> assignments;

    public StudentDashboard(Integer id, String first_name, String last_name, List<Group> groups, List<StudentDashboardAssignment> assignments) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.groups = groups;
        this.assignments = assignments;
    }

    public StudentDashboard() {}

    @Override
    public String toString() {
        return "StudentDashboard{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", groups=" + groups +
                ", assignments=" + assignments +
                '}';
    }
}