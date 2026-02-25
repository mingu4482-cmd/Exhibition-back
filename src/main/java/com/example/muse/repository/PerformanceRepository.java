package com.example.muse.repository;

import com.example.muse.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    Optional<Performance> findBySourceAndSourceId(String source, String sourceId);

    void deleteBySource(String source);

    List<Performance> findBySource(String source);  // ✅ 추가
}