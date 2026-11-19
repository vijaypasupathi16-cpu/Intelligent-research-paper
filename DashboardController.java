package com.researchintelligence.controller;

import com.researchintelligence.model.RecommendationResult;
import com.researchintelligence.model.ResearchPaper;
import com.researchintelligence.model.SearchHistory;
import com.researchintelligence.model.User;
import com.researchintelligence.service.PaperService;
import com.researchintelligence.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final PaperService paperService;
    private final RecommendationService recommendationService;

    public DashboardController(PaperService paperService, RecommendationService recommendationService) {
        this.paperService = paperService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/")
    public String landingPage(HttpSession session, Model model) {
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("totalPapers", paperService.getAllPapers().size());
        model.addAttribute("categories", paperService.getCategories());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<ResearchPaper> papers = paperService.getAllPapers();
        List<SearchHistory> recentSearches = paperService.getUserSearchHistory(user);
        long favoriteCount = paperService.getUserFavorites(user, "ALL").size();

        // Sample recommended papers feed using last search query or fallback
        String defaultQuery = !recentSearches.isEmpty() ? recentSearches.get(0).getSearchQuery() : "IoT Security Threat Detection";
        List<RecommendationResult> topRecommendations = recommendationService.recommendPapers(defaultQuery, null, null);

        if (topRecommendations.size() > 6) {
            topRecommendations = topRecommendations.subList(0, 6);
        }

        model.addAttribute("user", user);
        model.addAttribute("totalPapers", papers.size());
        model.addAttribute("totalSearches", recentSearches.size());
        model.addAttribute("favoriteCount", favoriteCount);
        model.addAttribute("recentSearches", recentSearches.stream().limit(5).toList());
        model.addAttribute("recommendedPapers", topRecommendations);
        model.addAttribute("categories", paperService.getCategories());
        model.addAttribute("activeNav", "dashboard");

        return "dashboard";
    }
}
