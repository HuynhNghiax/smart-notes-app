package com.example.smartnotesbackend.repository;

import com.example.smartnotesbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Hàm tìm kiếm người dùng bằng email để phục vụ Login, Register và Kiểm tra trùng lặp
    Optional<User> findByEmail(String email);
}