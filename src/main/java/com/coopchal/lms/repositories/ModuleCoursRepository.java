package com.coopchal.lms.repositories;

import com.coopchal.lms.models.ModuleCours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleCoursRepository extends JpaRepository<ModuleCours, Long> {
    List<ModuleCours> findByCoursId(Long coursId);
    boolean existsByCours_IdAndUrlFichier(Long coursId, String urlFichier);

}
