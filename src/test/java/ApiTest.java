import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * http://www.robotdreams.karpenko.cc/docs
 */

public class ApiTest extends BaseTest {

    @BeforeMethod(groups = {"Student", "Group", "Assignment"})
    public void prepareStudentsScope() {
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
    }

    @Test(groups = "Student")
    public void testCreateStudent() {
        Faker faker = new Faker();
        String newStudentFirstName = faker.name().firstName();
        String newStudentLastName = faker.name().lastName();
        RestAssured.given()
                .body(new StudentData(newStudentFirstName, newStudentLastName))
                .post("/students")
                .then().statusCode(200);
    }

//    @Test(groups = "Student")
//    public void testSearchStudentByID() {
//        Faker faker = new Faker();
//        String newStudentFirstName = faker.name().firstName();
//        String newStudentLastName = faker.name().lastName();
//        Response studentCreateResponse = RestAssured.given()
//                .body(new StudentData(newStudentFirstName, newStudentLastName))
//                .post("/students");
//        studentCreateResponse.then().statusCode(200);
//        Student student = studentCreateResponse.as(Student.class);
//
//        Student studentGetResponse = RestAssured.get("/students/{id}", student.id).as(Student.class);
//        Assert.assertEquals(studentGetResponse.firstName, newStudentFirstName);
//        Assert.assertEquals(studentGetResponse.lastName, newStudentLastName);
//    }
//
//    @Test(groups = "Student")
//    public void testSearchStudentByFirstName() {
//        Response response = RestAssured.given()
//                .queryParam("name", "Oleksandr")
//                .when().get("/students");
//        response.then().statusCode(200);
//        Student[] searchResult = response.as(Student[].class);
//        Assert.assertEquals(searchResult.length, 1);
//        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
//        Assert.assertEquals(names, Set.of("Oleksandr"));
//    }
//
//    @Test(groups = "Student")
//    public void testSearchStudentByLastName() {
//        Response response = RestAssured.given()
//                .queryParam("last_name", "Golovach")
//                .when().get("/students");
//        response.then().statusCode(200);
//        Student[] searchResult = response.as(Student[].class);
//        Assert.assertEquals(searchResult.length, 1);
//        Set<String> names = Arrays.stream(searchResult).map(s -> s.lastName).collect(Collectors.toSet());
//        Assert.assertEquals(names, Set.of("Golovach"));
//    }
//
//    @Test(groups = "Student")
//    public void testSearchStudentsByPartOfLastName() {
//        Response response = RestAssured.given()
//                .queryParam("last_name", "ch")
//                .when().get("/students");
//        response.then().statusCode(200);
//        Student[] searchResult = response.as(Student[].class);
//        Assert.assertEquals(searchResult.length, 2);
//        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
//        Assert.assertEquals(names, Set.of("Angela", "Volodymyr"));
//    }
//
//    @Test(groups = "Student")
//    public void testUpdateStudent() {
//        Faker faker = new Faker();
//        String newStudentFirstName = faker.name().firstName();
//        String newStudentLastName = faker.name().lastName();
//        Response studentCreateResponse = RestAssured.given()
//                .body(new StudentData(newStudentFirstName, newStudentLastName))
//                .post("/students");
//        studentCreateResponse.then().statusCode(200);
//        Student student = studentCreateResponse.as(Student.class);
//
//        Student studentGetResponse = RestAssured.get("/students/{id}", student.id).as(Student.class);
//        Assert.assertEquals(studentGetResponse.firstName, newStudentFirstName);
//        Assert.assertEquals(studentGetResponse.lastName, newStudentLastName);
//
//        RestAssured.given()
//                .body(new StudentData("Newly", "Updated"))
//                .put("/students/{id}", student.id)
//                .then().statusCode(200);
//
//        studentGetResponse = RestAssured.get("/students/{id}", student.id).as(Student.class);
//        Assert.assertEquals(studentGetResponse.firstName, "Newly");
//        Assert.assertEquals(studentGetResponse.lastName, "Updated");
//    }
//
//    @Test(groups = "Student")
//    public void testGetStudentsList() {
//        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
//        Assert.assertEquals(allStudents.length, 3);
//    }

    @Test(groups = "Group")
    public void testCreateGroup() {
        System.out.print("testCreateGroup - started");
        RestAssured.given()
                .body(new StudentData("Vlad", "Sidenko"))
                .post("/students")
                .then().statusCode(200);

        List<Student> allStudents = Arrays.asList(
                RestAssured.get("/students")
                        .then().statusCode(200)
                        .extract().as(Student[].class)
        );

        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();

        Response response = RestAssured
                .given().body(new GroupData("Math", studentIds))
                .post("/groups");
        response.then().statusCode(200);
        Assert.assertEquals(response.as(Group.class).name, "Math");
        Assert.assertEquals(response.as(Group.class).students.size(), 4);

        allStudents = Arrays.asList(
                RestAssured.given()
                        .queryParam("last_name", "ch")
                        .when().get("/students")
                        .then().statusCode(200)
                        .extract().as(Student[].class)
        );

        studentIds = allStudents.stream().map(student -> student.id).toList();

        Group newGroup = RestAssured
                .given().body(new GroupData("Physics", studentIds))
                .post("/groups")
                .then().statusCode(200)
                .body("name", equalTo("Physics"))
                .body("students", hasSize(studentIds.size()))
                .extract().as(Group.class);
        Assert.assertEquals(newGroup.name, "Physics");
        Assert.assertEquals(newGroup.students.size(), 2);
        System.out.println(" / finished");
    }
//
//    @Test(groups = "Group")
//    public void testGetGroupsList() {
//        System.out.print("testGetGroupsList - started");
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        Response response = RestAssured
//                .given().body(new GroupData("Math", studentIds))
//                .post("/groups");
//        response.then().statusCode(200);
//
//        allStudents = Arrays.asList(
//                RestAssured.given()
//                        .queryParam("last_name", "ch")
//                        .when().get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        Group newGroup = RestAssured
//                .given().body(new GroupData("Physics", studentIds))
//                .post("/groups")
//                .then().statusCode(200)
//                .body("name", equalTo("Physics"))
//                .body("students", hasSize(studentIds.size()))
//                .extract().as(Group.class);
//
//        Group[] allGroups = RestAssured.get("/groups").as(Group[].class);
//        Assert.assertEquals(allGroups.length, 2);
//        System.out.println(" / finished");
//    }
//
//    @Test(groups = "Group")
//    public void testSearchGroupById() {
//        System.out.print("testSearchGroupById - started");
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        Response response = RestAssured
//                .given().body(new GroupData("Math", studentIds))
//                .post("/groups");
//        response.then().statusCode(200);
//
//        allStudents = Arrays.asList(
//                RestAssured.given()
//                        .queryParam("last_name", "ch")
//                        .when().get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        Group newGroup = RestAssured
//                .given().body(new GroupData("Physics", studentIds))
//                .post("/groups")
//                .then().statusCode(200)
//                .body("name", equalTo("Physics"))
//                .body("students", hasSize(studentIds.size()))
//                .extract().as(Group.class);
//
//
//        List<Group> allGroups = Arrays.asList(
//                RestAssured.get("/groups")
//                        .then().statusCode(200)
//                        .extract().as(Group[].class)
//        );
//
//        Map<Integer, String> groups = allGroups.stream().collect(Collectors.toMap(Group::getId, Group::getName));
//        for (Integer key : groups.keySet()) {
//            Group group = RestAssured.get("/groups/{id}", key)
//                    .then().statusCode(200)
//                    .extract().as(Group.class);
//            Assert.assertEquals(group.name, groups.get(key));
//        }
//        System.out.println(" / finished");
//    }

    @Test(groups = "Assignment")
    public void testCreateAssignmentContent() {
        Response contentCreateResponse = RestAssured.given()
                .body(new AssignmentContentData("testFirstContent"))
                .post("/content");
        contentCreateResponse.then().statusCode(200);
        AssignmentContentData content = contentCreateResponse.as(AssignmentContentData.class);
        Assert.assertEquals(content.content, "testFirstContent");

        contentCreateResponse = RestAssured.given()
                .body(new AssignmentContentData("testSecondContent"))
                .post("/content");
        contentCreateResponse.then().statusCode(200);
        content = contentCreateResponse.as(AssignmentContentData.class);
        Assert.assertEquals(content.content, "testSecondContent");
    }
//
//    @Test(groups = "Assignment")
//    public void testCreateAssignmentForStudent() {
//        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
//
//        RestAssured.given()
//                .body(new AssignmentContentData("testFirstContent"))
//                .post("/content")
//                .then().statusCode(200);
//        RestAssured.given()
//                .body(new AssignmentContentData("testSecondContent"))
//                .post("/content")
//                .then().statusCode(200);
//        AssignmentContentData[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContentData[].class);
//
//        Integer student_id;
//        Integer content_id;
//        int start = 0;
//        int end = allStudents.length / 2;
//        for (int i = 0; i < allAssignmentContent.length; i++) {
//            if (i == 1) {
//                start = allStudents.length / 2;
//                end = allStudents.length;
//            }
//            for (int j = start; j < end; j++) {
//                content_id = allAssignmentContent[i].id;
//                student_id = allStudents[j].id;
//                AssignmentDashboardForStudent assignmentDashboardForStudent = RestAssured.given()
//                        .body(new AssignmentDataForStudent("student", student_id, content_id))
//                        .post("/assignments")
//                        .then().statusCode(200)
//                        .extract().as(AssignmentDashboardForStudent.class);
//                Assert.assertEquals(assignmentDashboardForStudent.studentId, student_id);
//                Assert.assertEquals(assignmentDashboardForStudent.contentId, content_id);
//            }
//        }
//    }
//
//    @Test(groups = "Assignment")
//    public void testCreateAssignmentForGroup() {
//        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
//        Assert.assertEquals(allStudents.length, 3);
//        List<Integer> studentIds = Arrays.stream(allStudents).map(student -> student.id).toList();
//        Integer studentId = studentIds.get(0);
//
//        Response response = RestAssured
//                .given().body(new GroupData("Math", studentIds))
//                .post("/groups");
//        response.then().statusCode(200);
//        Assert.assertEquals(response.as(Group.class).name, "Math");
//        Assert.assertEquals(response.as(Group.class).students.size(), 3);
//        Integer group_id = response.as(Group.class).id;
//
//        RestAssured.given()
//                .body(new AssignmentContentData("testFirstContent"))
//                .post("/content")
//                .then().statusCode(200);
//        AssignmentContentData[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContentData[].class);
//        Integer contentId = allAssignmentContent[0].id;
//
//        AssignmentDashboardForStudent[] assignmentDashboardForStudent = RestAssured.given()
//                .body(new AssignmentDataForGroup("group", group_id, contentId))
//                .post("/assignments")
//                .then().statusCode(200)
//                .extract().as(AssignmentDashboardForStudent[].class);
//        Assert.assertEquals(assignmentDashboardForStudent[0].studentId, studentId);
//        Assert.assertEquals(assignmentDashboardForStudent[0].contentId, contentId);
//    }
//
//    @Test(groups = "Student")
//    public void testGetStudentDashboard() {
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        for (Integer i : studentIds) {
//            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
//                    .then().statusCode(200)
//                    .extract().as(StudentDashboard.class);
//
//            Student student = RestAssured.get("/students/{id}", studentDashboard.id)
//                    .then().statusCode(200)
//                    .extract().as(Student.class);
//            Assert.assertEquals(student.id, studentDashboard.id);
//            Assert.assertEquals(student.firstName, studentDashboard.first_name);
//            Assert.assertEquals(student.lastName, studentDashboard.last_name);
//
//            List<Integer> groupsIdsStudentDashboard = studentDashboard.groups.stream().map(group -> group.id).toList();
//            for (Integer j : groupsIdsStudentDashboard) {
//                Group group = RestAssured.get("groups/{id}", j)
//                        .then().statusCode(200)
//                        .extract().as(Group.class);
//                List<Student> studentsGroup = group.students.stream().toList();
//                boolean isStudentinGroup = false;
//                for (Student studentInGroup : studentsGroup) {
//                    if (Objects.equals(studentInGroup.id, studentDashboard.id)) {
//                        isStudentinGroup = true;
//                        break;
//                    }
//                }
//                Assert.assertTrue(isStudentinGroup);
//            }
//        }
//    }
//
//    @Test(groups = "Assignment")
//    public void testGetAssignment() {
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        for (Integer i : studentIds) {
//            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
//                    .then().statusCode(200)
//                    .extract().as(StudentDashboard.class);
//
//            List<StudentDashboardAssignment> assignments = studentDashboard.assignments;
//            for (StudentDashboardAssignment assignment : assignments) {
//                Assignment assignmentGetResponse = RestAssured.get("/assignments/{id}", assignment.getId())
//                        .then().statusCode(200)
//                        .extract().as(Assignment.class);
//                Assert.assertEquals(studentDashboard.id, assignmentGetResponse.student.id);
//
//                List<Integer> contentIdsStudentDashboard = studentDashboard.assignments.stream().map(assign -> assign.content.id).toList();
//
//                boolean isContentFromGetResponseInStudentDashboard = false;
//                for (Integer j : contentIdsStudentDashboard) {
//                    if (Objects.equals(j, assignmentGetResponse.content.id)) {
//                        isContentFromGetResponseInStudentDashboard = true;
//                        break;
//                    }
//                }
//                Assert.assertTrue(isContentFromGetResponseInStudentDashboard);
//            }
//        }
//    }
//
//    @Test(groups = "Assignment")
//    public void testSolutionAssignment() {
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        RestAssured
//                .given().body(new GroupData("Math", studentIds))
//                .post("/groups")
//                .then().statusCode(200);
//
//        RestAssured.given()
//                .body(new AssignmentContentData("testFirstContent"))
//                .post("/content")
//                .then().statusCode(200);
//        AssignmentContentData[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContentData[].class);
//
//        AssignmentDashboardForStudent assignmentDashboardForStudent = RestAssured.given()
//                .body(new AssignmentDataForStudent("student", studentIds.get(0), allAssignmentContent[0].id))
//                .post("/assignments")
//                .then().statusCode(200)
//                .extract().as(AssignmentDashboardForStudent.class);
//
//        for (Integer i : studentIds) {
//            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
//                    .then().statusCode(200)
//                    .extract().as(StudentDashboard.class);
//
//            Student student = RestAssured.get("/students/{id}", studentDashboard.id)
//                    .then().statusCode(200)
//                    .extract().as(Student.class);
//
//            List<Integer> groupsIdsStudentDashboard = studentDashboard.groups.stream().map(group -> group.id).toList();
//            for (Integer j : groupsIdsStudentDashboard) {
//                Group group = RestAssured.get("groups/{id}", j)
//                        .then().statusCode(200)
//                        .extract().as(Group.class);
//                List<Student> studentsGroup = group.students.stream().toList();
//                boolean isStudentinGroup = false;
//                for (Student studentInGroup : studentsGroup) {
//                    if (Objects.equals(studentInGroup.id, studentDashboard.id)) {
//                        isStudentinGroup = true;
//                        break;
//                    }
//                }
//            }
//        }
//        studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        int studentId = studentIds.get(0);
//        StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", studentId)
//                .then().statusCode(200)
//                .extract().as(StudentDashboard.class);
//
//        List<String> assignmentIds = studentDashboard.assignments.stream().map(assign -> assign.id).toList();
//        String assignmentId = assignmentIds.get(0);
//
//        JSONObject jo = new JSONObject();
//        jo.put("solution", "testSolution");
//
//        Assignment assignmentSolution = RestAssured.given()
//                .body(jo.toString())
//                .post("/assignments/{id}/solution", assignmentId)
//                .then().statusCode(200)
//                .extract().as(Assignment.class);
//
//        studentDashboard = RestAssured.get("/students/{id}/dashboard", studentId)
//                .then().statusCode(200)
//                .extract().as(StudentDashboard.class);
//
//        List<String> solutionAssignment = studentDashboard.assignments.stream().map(assign -> assign.solution).toList();
//        Assert.assertEquals(solutionAssignment.get(0), "testSolution");
//    }
//
//    @Test(groups = "Assignment")
//    public void testSolutionMark() {
//        List<Student> allStudents = Arrays.asList(
//                RestAssured.get("/students")
//                        .then().statusCode(200)
//                        .extract().as(Student[].class)
//        );
//        List<Integer> studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        RestAssured
//                .given().body(new GroupData("Math", studentIds))
//                .post("/groups")
//                .then().statusCode(200);
//
//        RestAssured.given()
//                .body(new AssignmentContentData("testFirstContent"))
//                .post("/content")
//                .then().statusCode(200);
//        AssignmentContentData[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContentData[].class);
//
//        AssignmentDashboardForStudent assignmentDashboardForStudent = RestAssured.given()
//                .body(new AssignmentDataForStudent("student", studentIds.get(0), allAssignmentContent[0].id))
//                .post("/assignments")
//                .then().statusCode(200)
//                .extract().as(AssignmentDashboardForStudent.class);
//
//        for (Integer i : studentIds) {
//            StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", i)
//                    .then().statusCode(200)
//                    .extract().as(StudentDashboard.class);
//
//            Student student = RestAssured.get("/students/{id}", studentDashboard.id)
//                    .then().statusCode(200)
//                    .extract().as(Student.class);
//
//            List<Integer> groupsIdsStudentDashboard = studentDashboard.groups.stream().map(group -> group.id).toList();
//            for (Integer j : groupsIdsStudentDashboard) {
//                Group group = RestAssured.get("groups/{id}", j)
//                        .then().statusCode(200)
//                        .extract().as(Group.class);
//                List<Student> studentsGroup = group.students.stream().toList();
//                boolean isStudentinGroup = false;
//                for (Student studentInGroup : studentsGroup) {
//                    if (Objects.equals(studentInGroup.id, studentDashboard.id)) {
//                        isStudentinGroup = true;
//                        break;
//                    }
//                }
//            }
//        }
//        studentIds = allStudents.stream().map(student -> student.id).toList();
//
//        int studentId = studentIds.get(0);
//        StudentDashboard studentDashboard = RestAssured.get("/students/{id}/dashboard", studentId)
//                .then().statusCode(200)
//                .extract().as(StudentDashboard.class);
//
//        List<String> assignmentIds = studentDashboard.assignments.stream().map(assign -> assign.id).toList();
//        String assignmentId = assignmentIds.get(0);
//
//        JSONObject jo = new JSONObject();
//        jo.put("solution", "testSolution");
//
//        RestAssured.given()
//                .body(jo.toString())
//                .post("/assignments/{id}/solution", assignmentId)
//                .then().statusCode(200);
//
//        JSONObject jo1 = new JSONObject();
//        jo1.put("mark", 5);
//
//        Assignment assignmentMark = RestAssured.given()
//                .body(jo1.toString())
//                .post("/assignments/{id}/mark", assignmentId)
//                .then().statusCode(200)
//                .extract().as(Assignment.class);
//
//        studentDashboard = RestAssured.get("/students/{id}/dashboard", studentId)
//                .then().statusCode(200)
//                .extract().as(StudentDashboard.class);
//
//        List<Integer> markAssignment = studentDashboard.assignments.stream().map(assign -> assign.mark).toList();
//        Assert.assertEquals(markAssignment.get(0), assignmentMark.mark);
//    }
}


