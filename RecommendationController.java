package com.researchintelligence.controller;

import com.researchintelligence.model.Favorite;
import com.researchintelligence.model.RecommendationResult;
import com.researchintelligence.model.SearchHistory;
import com.researchintelligence.model.User;
import com.researchintelligence.service.PaperService;
import com.researchintelligence.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class RecommendationController {

    private final PaperService paperService;
    private final RecommendationService recommendationService;

    public RecommendationController(PaperService paperService, RecommendationService recommendationService) {
        this.paperService = paperService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations")
    public String personalizedRecommendations(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<SearchHistory> userHistory = paperService.getUserSearchHistory(user);
        List<Favorite> userFavorites = paperService.getUserFavorites(user, "ALL");

        // Synthesize seed query from user's search history or saved favorites
        StringBuilder seedQuery = new StringBuilder();
        if (!userHistory.isEmpty()) {
            for (int i = 0; i < Math.min(3, userHistory.size()); i++) {
                seedQuery.append(userHistory.get(i).getSearchQuery()).append(" ");
            }
        } else if (!userFavorites.isEmpty()) {
            for (Favorite fav : userFavorites) {
                seedQuery.append(fav.getPaper().getKeywords()).append(" ");
            }
        } else {
            seedQuery.append("Cybersecurity IoT Threat Detection Network Security");
        }

        List<RecommendationResult> recommendations = recommendationService.recommendPapers(seedQuery.toString().trim(), null, null);

        // Filter out papers user has already favorited or limit feed
        model.addAttribute("user", user);
        model.addAttribute("recommendations", recommendations);
        model.addAttribute("historyCount", userHistory.size());
        model.addAttribute("favoriteCount", userFavorites.size());
        model.addAttribute("seedQuery", seedQuery.toString().trim());
        model.addAttribute("activeNav", "recommendations");

        return "recommendations";
    }
}
