package com.example.Docker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Docker.model.Valentine;

public interface ValentineRepository extends JpaRepository<Valentine, Long> {
}
