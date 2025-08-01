package com.coopchal.lms.repositories;

import com.coopchal.lms.models.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursRepository extends JpaRepository<Cours, Long> {
}
