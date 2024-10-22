package com.lxx.repository;

import com.lxx.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StudentRepository {

    private final DataSource dataSource;

    public void batchInsert(List<Student> students) throws SQLException {
        final String sql = "INSERT INTO student (name, home) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);

            // 关闭自动提交
            connection.setAutoCommit(false);

            for (Student student : students) {
                ps.setString(1, student.getName());
                ps.setString(2, student.getHome());
                ps.addBatch();
            }

            // 执行批处理
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}
