package za.ac.cput.univentbackend.serviceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.service.StudentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private final String STUDENT_NUMBER = "221012345";

    @BeforeEach
    void setUp() {

        student = new Student.Builder()
                .setUserId("u-001")
                .setName("Sam Mokoena")
                .setEmail("samndlela@gmail.com")
                .setPasswordHash("hashed-password")
                .setPhoneNumber("0821234567")
                .setRole(RoleEnum.STUDENT)
                .setStudentNumber(STUDENT_NUMBER)
                .setFaculty("Engineering")
                .setDepartment("Software Development")
                .setYearOfStudy(3)
                .build();
    }

    @Test
    void create_WithValidStudent_ShouldSaveAndReturnStudent() {
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.create(student);

        assertNotNull(result);
        assertEquals(student, result);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void create_WithNullStudent_ShouldReturnNullAndNotTouchRepository() {
        Student result = studentService.create(null);

        assertNull(result);

        verify(studentRepository, never()).save(any());
    }


    @Test
    void read_WithExistingId_ShouldReturnStudent() {
        when(studentRepository.findById(STUDENT_NUMBER)).thenReturn(Optional.of(student));

        Student result = studentService.read(STUDENT_NUMBER);

        assertNotNull(result);
        assertEquals(student, result);
        verify(studentRepository, times(1)).findById(STUDENT_NUMBER);
    }

    @Test
    void read_WithNonExistentId_ShouldReturnNull() {
        when(studentRepository.findById(STUDENT_NUMBER)).thenReturn(Optional.empty());

        Student result = studentService.read(STUDENT_NUMBER);

        assertNull(result);
        verify(studentRepository, times(1)).findById(STUDENT_NUMBER);
    }


    @Test
    void update_ShouldSaveAndReturnStudent() {
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.update(student);

        assertNotNull(result);
        assertEquals(student, result);
        verify(studentRepository, times(1)).save(student);
    }


    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        doNothing().when(studentRepository).deleteById(STUDENT_NUMBER);

        studentService.delete(STUDENT_NUMBER);

        verify(studentRepository, times(1)).deleteById(STUDENT_NUMBER);
    }


}
