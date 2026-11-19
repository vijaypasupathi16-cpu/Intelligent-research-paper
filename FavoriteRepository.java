package com.researchintelligence.repository;

import com.researchintelligence.model.Favorite;
import com.researchintelligence.model.ResearchPaper;
import com.researchintelligence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    List<Favorite> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);

    Optional<Favorite> findByUserAndPaper(User user, ResearchPaper paper);

    boolean existsByUserAndPaper(User user, ResearchPaper paper);

    void deleteByUserAndPaper(User user, ResearchPaper paper);

    long countByUser(User user);
}
