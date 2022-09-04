import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class AsyncApiTest extends BaseTest {

    @BeforeMethod
    public void preparation() {
        RestAssured.given()
                .body(new StudentData("Oleksandr", "Svidersky"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Angela", "Potapchuk"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Volodymyr", "Golovach"))
                .post("/students")
                .then().statusCode(200);
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        List<Integer> studentIds = Arrays.stream(allStudents).map(student -> student.id).toList();

        Group group = RestAssured.given()
                .body(new GroupData("Math", studentIds))
                .post("/groups")
                .then().statusCode(200)
                .body("name", equalTo("Math"))
                .body("students", hasSize(3))
                .extract().as(Group.class);

        AssignmentContentData content1 = RestAssured.given()
                .body(new AssignmentContentData("Solve the riddle 1"))
                .when().post("/content")
                .then().statusCode(200).extract().as(AssignmentContentData.class);

        RestAssured.given()
                .body(new AssignmentDataForGroup("group", group.id, content1.id))
                .post("/assignments")
                .then().statusCode(200);

        AssignmentContentData content2 = RestAssured.given()
                .body(new AssignmentContentData("Solve the riddle 2"))
                .when().post("/content")
                .then().statusCode(200).extract().as(AssignmentContentData.class);

        RestAssured.given()
                .body(new AssignmentDataForGroup("group", group.id, content2.id))
                .post("/assignments")
                .then().statusCode(200);

        AssignmentContentData content3 = RestAssured.given()
                .body(new AssignmentContentData("Solve the riddle 3"))
                .when().post("/content")
                .then().statusCode(200).extract().as(AssignmentContentData.class);

        RestAssured.given()
                .body(new AssignmentDataForGroup("group", group.id, content3.id))
                .post("/assignments")
                .then().statusCode(200);
    }

    @Test
    public void testStudentDashboardWithoutSolution() {
        System.out.print("testStudentDashboardWithoutSolution - started");
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        List<Integer> studentIds = Arrays.stream(allStudents).map(student -> student.id).toList();
        for (Integer i : studentIds) {
            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
                    .then().statusCode(200)
                    .extract().as(StudentDashboard.class);

            List<Integer> marks = studentDashboard.assignments.stream().map(a -> a.mark).toList();
            List<Integer> expectedMarks = new ArrayList<>();
            expectedMarks.add(null);
            expectedMarks.add(null);
            expectedMarks.add(null);
            Assert.assertEquals(marks, expectedMarks);

            RestAssured.post("students/{id}/year_end", i).then().statusCode(201);

            Awaitility.await("Waiting for all marks to be set")
                    .ignoreException(AssertionError.class)
                    .atMost(Durations.ONE_MINUTE)
                    .pollDelay(3, SECONDS)
                    .pollInterval(fibonacci(SECONDS))

                    .until(() -> verifyMarksOnDashboard(i, List.of(1, 1, 1)));
        }
        System.out.println(" / finished");
    }

    @Test
    public void testStudentDashboardWithSolution() {
        System.out.print("testStudentDashboardWithSolution - started");
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        List<Integer> studentIds = Arrays.stream(allStudents).map(student -> student.id).toList();
        for (Integer i : studentIds) {
            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
                    .then().statusCode(200)
                    .extract().as(StudentDashboard.class);
            List<String> assignmentIds = studentDashboard.assignments.stream().map(a->a.id).toList();

            for (String id : assignmentIds) {
                JSONObject jo = new JSONObject();
                jo.put("solution", "testSolution" + id);
                RestAssured.given()
                        .body(jo.toString())
                        .post("/assignments/{id}/solution", id)
                        .then().statusCode(200);
            }

            List<Integer> marks = studentDashboard.assignments.stream().map(a -> a.mark).toList();
            List<Integer> expectedMarks = new ArrayList<>();
            expectedMarks.add(null);
            expectedMarks.add(null);
            expectedMarks.add(null);
            Assert.assertEquals(marks, expectedMarks);

            RestAssured.post("students/{id}/year_end", i).then().statusCode(201);

            Awaitility.await("Waiting for all marks to be set")
                    .ignoreException(AssertionError.class)
                    .atMost(Durations.ONE_MINUTE)
                    .pollDelay(3, SECONDS)
                    .pollInterval(fibonacci(SECONDS))

                    .until(() -> verifyMarksOnDashboard(i, List.of(5, 5, 5)));

        }
        System.out.println(" / finished");
    }

    private boolean verifyMarksOnDashboard(Integer studentId, List<Integer> expectedMarks) {
        StudentDashboard newDashboard = RestAssured.get("students/{id}/dashboard", studentId).as(StudentDashboard.class);
        List<Integer> marks = newDashboard.assignments.stream().map(a -> a.mark).collect(Collectors.toList());
        Assert.assertEquals(marks, expectedMarks, "Marks are set");
        return true;
    }
}

