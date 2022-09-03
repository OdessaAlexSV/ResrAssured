import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDashboardAssignment {
    @JsonProperty
    String id;

    @JsonProperty
    AssignmentContentData content;

    @JsonProperty
    String solution;

    @JsonProperty
    Integer mark;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "StudentDashboardAssignment{" +
                "id='" + id + '\'' +
                ", content=" + content +
                ", solution='" + solution + '\'' +
                ", mark=" + mark +
                '}';
    }
}
