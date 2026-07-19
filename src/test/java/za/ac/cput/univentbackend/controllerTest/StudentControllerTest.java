package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.controller.StudentController;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.service.StudentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private Student inputStudent;
    private static final String STUDENT_NUMBER = "221012345";

    @BeforeEach
    void setUp() {
        inputStudent = new Student.Builder()
                .setStudentNumber(STUDENT_NUMBER)
                .setName("Sam Mokoena")
                .setEmail("sam@example.com")
                .setFaculty("Engineering")
                .setDepartment("Software Development")
                .setYearOfStudy(3)
                .setRole(RoleEnum.STUDENT)
                .build();
    }

    @Test
    void createStudent_ShouldReturn200AndServiceResult() {
        Student savedStudent = new Student.Builder().setStudentNumber(STUDENT_NUMBER).build();
        when(studentService.create(inputStudent)).thenReturn(savedStudent);

        ResponseEntity<Student> response = studentController.createStudent(inputStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(savedStudent, response.getBody());
        verify(studentService, times(1)).create(inputStudent);
    }

    @Test
    void createStudent_WhenServiceReturnsNull_CurrentlyStillReturns200() {
        // BUG FLAG (#3 above): documents current behavior. Should be 400 once fixed.
        when(studentService.create(inputStudent)).thenReturn(null);

        ResponseEntity<Student> response = studentController.createStudent(inputStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(studentService, times(1)).create(inputStudent);
    }



    @Test
    void getStudentById_WhenFound_ShouldReturn200AndServiceResult() {
        Student foundStudent = new Student.Builder().setStudentNumber(STUDENT_NUMBER).build();
        when(studentService.read(STUDENT_NUMBER)).thenReturn(foundStudent);

        ResponseEntity<Student> response = studentController.getStudentById(STUDENT_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(foundStudent, response.getBody());
        verify(studentService, times(1)).read(STUDENT_NUMBER);
    }

    @Test
    void getStudentById_WhenNotFound_CurrentlyReturns200InsteadOf404() {
        // BUG FLAG (#2 above): documents current behavior. Should be 404 once fixed.
        when(studentService.read(STUDENT_NUMBER)).thenReturn(null);

        ResponseEntity<Student> response = studentController.getStudentById(STUDENT_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(studentService, times(1)).read(STUDENT_NUMBER);
    }



    @Test
    void updateStudent_ShouldReturnServiceResult() {

        Student updatedStudent = new Student.Builder().setStudentNumber(STUDENT_NUMBER).build();
        when(studentService.update(inputStudent)).thenReturn(updatedStudent);

        ResponseEntity<Student> response = studentController.updateStudent(inputStudent);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedStudent, response.getBody(),
                "Controller should return the service's updated Student, not the raw request body - see bug #1");
        verify(studentService, times(1)).update(inputStudent);
    }



    @Test
    void deleteStudent_ShouldReturn204AndInvokeService() {
        doNothing().when(studentService).delete(STUDENT_NUMBER);

        ResponseEntity<Void> response = studentController.delete(STUDENT_NUMBER);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(studentService, times(1)).delete(STUDENT_NUMBER);
    }
}