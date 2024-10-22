package com.lxx.service;

import com.lxx.entity.Student;
import com.lxx.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    // 构建数据
    public List<Student> buildStudentList() {
        return IntStream.range(0, 100000).parallel().mapToObj(i -> {
            Student student = new Student();
            student.setName("Student" + i);
            student.setHome("Home" + i);
            return student;
        }).collect(Collectors.toList());
    }

    // 完成插入
    public void insertStudentList() {
        List<Student> students = buildStudentList();
        try {
            studentRepository.batchInsert(students);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
