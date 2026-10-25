package za.ac.cput.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService service;

    @Test
    void createStudentSavesAndReturnsStudent() {
        Student student = student();
        when(repository.save(student)).thenReturn(student);

        assertSame(student, service.create(student));
        verify(repository).save(student);
    }

    @Test
    void nullCreateReturnsNullWithoutSaving() {
        assertNull(service.create(null));
    }

    @Test
    void readStudentReturnsMatchingRecord() {
        Student student = student();
        when(repository.findById("student-1")).thenReturn(Optional.of(student));

        assertSame(student, service.read("student-1"));
        verify(repository).findById("student-1");
    }

    @Test
    void readMissingStudentReturnsNull() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertNull(service.read("missing"));
    }

    @Test
    void updateStudentSavesAndReturnsStudent() {
        Student student = student();
        when(repository.save(student)).thenReturn(student);

        assertSame(student, service.update(student));
        verify(repository).save(student);
    }

    @Test
    void deleteStudentDeletesById() {
        service.delete("student-1");

        verify(repository).deleteById("student-1");
    }

    private Student student() {
        return new Student.Builder()
                .setUserId("student-1")
                .setStudentNumber("221012345")
                .setRole(RoleEnum.STUDENT)
                .build();
    }
}
