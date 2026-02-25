package com.example.muse.service;

import com.example.muse.domain.Performance;
import com.example.muse.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    // ✅ source + sourceId로 upsert (기본 구현)
    @Transactional
    public void upsert(Performance p) {
        performanceRepository.findBySourceAndSourceId(p.getSource(), p.getSourceId())
                .ifPresentOrElse(existing -> {
                    existing.setTitle(p.getTitle());
                    existing.setStartDate(p.getStartDate());
                    existing.setEndDate(p.getEndDate());
                    existing.setVenueName(p.getVenueName());
                    existing.setArea(p.getArea());
                    existing.setGenre(p.getGenre());
                    existing.setPosterUrl(p.getPosterUrl());
                    performanceRepository.save(existing);
                }, () -> performanceRepository.save(p));
    }
    // 전체 조회
    public List<Performance> findAll() {
        return performanceRepository.findAll();
    }

    // 단건 조회
    public Performance findById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance not found"));
    }

    // source로 조회
    public List<Performance> findBySource(String source) {
        return performanceRepository.findBySource(source);
    }


    // ✅ 재적재용 삭제( KOPIS만 지우기)
    @Transactional
    public void deleteBySource(String source) {
        performanceRepository.deleteBySource(source);
    }

}