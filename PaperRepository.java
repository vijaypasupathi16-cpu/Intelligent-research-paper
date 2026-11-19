package com.researchintelligence.repository;

import com.researchintelligence.model.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<ResearchPaper, Long> {

    List<ResearchPaper> findByCategory(String category);

    List<ResearchPaper> findByPublicationYear(Integer publicationYear);

    @Query("SELECT DISTINCT p.category FROM ResearchPaper p ORDER BY p.category ASC")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT p.publicationYear FROM ResearchPaper p ORDER BY p.publicationYear DESC")
    List<Integer> findDistinctPublicationYears();

    @Query("SELECT p.category, COUNT(p) FROM ResearchPaper p GROUP BY p.category")
    List<Object[]> countPapersByCategory();

    @Query("SELECT p.publicationYear, COUNT(p) FROM ResearchPaper p GROUP BY p.publicationYear ORDER BY p.publicationYear ASC")
    List<Object[]> countPapersByYear();
}
