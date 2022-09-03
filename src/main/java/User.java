import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("username")
    public String name;

    @JsonProperty
    public String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
