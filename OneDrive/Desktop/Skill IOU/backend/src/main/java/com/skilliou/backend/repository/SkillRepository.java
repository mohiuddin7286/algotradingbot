package com.skilliou.backend.repository;

import com.skilliou.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // We can add custom search later, e.g., findByCategory
}