package com.simply.Cinema.core.movieManagement.repository;

import com.simply.Cinema.core.movieManagement.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageRepo extends JpaRepository<Language, Long> {

    // Find by language code (e.g., "en", "hi")
    Language findByCode(String code);

    List<Language> findByIsActiveTrue();

    boolean existsByCodeIgnoreCase(String code);


}
