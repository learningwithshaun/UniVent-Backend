package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 **/

@Entity
@Table(name = "students")
public class Student extends User {
    private String studentNumber;
    private String faculty;
    private String department;
    private int yearOfStudy;

    public Student() {
        setRole(RoleEnum.STUDENT);
    }

    private Student(Builder builder) {
        setUserId(builder.userId);
        setName(builder.name);
        setEmail(builder.email);
        setPasswordHash(builder.passwordHash);
        setPhoneNumber(builder.phoneNumber);
        setRole(builder.role != null ? builder.role : RoleEnum.STUDENT);
        setEnabled(builder.enabled);
        setCreatedAt(builder.createdAt);
        this.studentNumber = builder.studentNumber;
        this.faculty = builder.faculty;
        this.department = builder.department;
        this.yearOfStudy = builder.yearOfStudy;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentNumber='" + studentNumber + '\'' +
                ", faculty='" + faculty + '\'' +
                ", department='" + department + '\'' +
                ", yearOfStudy=" + yearOfStudy +
                '}';
    }

    public static class Builder {
        private String userId;
        private String name;
        private String email;
        private String passwordHash;
        private String phoneNumber;
        private RoleEnum role;
        private boolean enabled = true;
        private LocalDateTime createdAt;
        private String studentNumber;
        private String faculty;
        private String department;
        private int yearOfStudy;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setRole(RoleEnum role) {
            this.role = role;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setStudentNumber(String studentNumber) {
            this.studentNumber = studentNumber;
            return this;
        }

        public Builder setFaculty(String faculty) {
            this.faculty = faculty;
            return this;
        }

        public Builder setDepartment(String department) {
            this.department = department;
            return this;
        }

        public Builder setYearOfStudy(int yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
            return this;
        }

        public Student build() {
            return new Student(this);
        }
    }
}
