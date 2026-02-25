package com.example.muse.repository;

import com.example.muse.domain.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository
        extends JpaRepository<Exhibition, Long> {
}
