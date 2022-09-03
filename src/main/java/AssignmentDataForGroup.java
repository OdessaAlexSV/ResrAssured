import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentDataForGroup {
    @JsonProperty("target_type")
    public String targetType;

    @JsonProperty("group_id")
    public Integer groupId;

    @JsonProperty("content_id")
    public Integer contentId;

    public AssignmentDataForGroup() {}

    public AssignmentDataForGroup(String targetType, Integer groupId, Integer contentId) {
        this.targetType = targetType;
        this.groupId = groupId;
        this.contentId = contentId;
    }

    @Override
    public String toString() {
        return "AssignmentForGroup{" +
                "target_type='" + targetType + '\'' +
                ", group_id=" + groupId +
                ", content_id=" + contentId +
                '}';
    }
}
