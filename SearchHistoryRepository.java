package com.researchintelligence.repository;

import com.researchintelligence.model.SearchHistory;
import com.researchintelligence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserOrderBySearchDateDesc(User user);

    List<SearchHistory> findTop10ByUserOrderBySearchDateDesc(User user);

    void deleteByUser(User user);

    long countByUser(User user);
}
