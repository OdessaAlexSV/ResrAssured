import com.fasterxml.jackson.annotation.JsonProperty;

public class Assignment {
    @JsonProperty
    Integer id;

    @JsonProperty
    Student student;

    @JsonProperty
    AssignmentContentData content;

    @JsonProperty
    String solution;

    @JsonProperty
    Integer mark;

    public Assignment(Integer id, Student student, AssignmentContentData content, String solution, Integer mark) {
        this.id = id;
        this.student = student;
        this.content = content;
        this.solution = solution;
        this.mark = mark;
    }

    public Assignment() {
    }

    @Override
    public String toString() {
        return "GetAssignmentById{" +
                "id=" + id +
                ", student=" + student +
                ", content=" + content +
                ", solution='" + solution + '\'' +
                ", mark=" + mark +
                '}';
    }
}
