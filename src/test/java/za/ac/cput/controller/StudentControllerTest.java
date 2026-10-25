package za.ac.cput.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.service.StudentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock
    private StudentService service;

    @InjectMocks
    private StudentController controller;

    @Test
    void createStudentReturnsServiceResult() {
        Student input = student("student-1");
        Student created = student("student-1");
        when(service.create(input)).thenReturn(created);

        ResponseEntity<Student> response = controller.createStudent(input);

        assertEquals(200, response.getStatusCode().value());
        assertSame(created, response.getBody());
        verify(service).create(input);
    }

    @Test
    void getStudentByIdReturnsServiceResult() {
        Student found = student("student-1");
        when(service.read("student-1")).thenReturn(found);

        ResponseEntity<Student> response = controller.getStudentById("student-1");

        assertEquals(200, response.getStatusCode().value());
        assertSame(found, response.getBody());
        verify(service).read("student-1");
    }

    @Test
    void deleteStudentReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete("student-1");

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete("student-1");
    }

    private Student student(String id) {
        return new Student.Builder()
                .setUserId(id)
                .setRole(RoleEnum.STUDENT)
                .setName("Test Student")
                .build();
    }
}
