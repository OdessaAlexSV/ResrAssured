import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Group {
    @JsonProperty
    Integer id;

    @JsonProperty
    String name;

    @JsonProperty
    List<Student> students;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + students +
                '}';
    }
}
