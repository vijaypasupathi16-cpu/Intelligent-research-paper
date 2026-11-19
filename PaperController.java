package com.researchintelligence.controller;

import com.researchintelligence.model.*;
import com.researchintelligence.service.PaperService;
import com.researchintelligence.service.RecommendationService;
import com.researchintelligence.service.SummaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PaperController {

    private final PaperService paperService;
    private final RecommendationService recommendationService;
    private final SummaryService summaryService;

    public PaperController(PaperService paperService,
                           RecommendationService recommendationService,
                           SummaryService summaryService) {
        this.paperService = paperService;
        this.recommendationService = recommendationService;
        this.summaryService = summaryService;
    }

    @GetMapping("/explorer")
    public String explorer(@RequestParam(value = "query", required = false, defaultValue = "") String query,
                           @RequestParam(value = "category", required = false) String category,
                           @RequestParam(value = "year", required = false) Integer year,
                           HttpSession session,
                           Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        List<String> categories = paperService.getCategories();
        List<Integer> years = paperService.getPublicationYears();

        List<RecommendationResult> results = new ArrayList<>();
        SemanticInsight insight = null;

        if (query != null && !query.trim().isEmpty()) {
            results = recommendationService.recommendPapers(query.trim(), category, year);
            insight = recommendationService.generateSemanticInsight(query.trim(), results);

            // Record search history for logged in user
            if (user != null) {
                paperService.recordSearch(user, query.trim(), results.size());
            }
        } else {
            // Default view: show all papers with zero initial query score or sample query
            List<ResearchPaper> allPapers = paperService.getAllPapers();
            for (ResearchPaper paper : allPapers) {
                if ((category == null || category.isEmpty() || paper.getCategory().equalsIgnoreCase(category)) &&
                    (year == null || year == 0 || paper.getPublicationYear().equals(year))) {
                    String summary = summaryService.generateExtractiveSummary(paper.getAbstractText(), paper.getKeywords(), Collections.emptyList());
                    results.add(new RecommendationResult(paper, 0.0, Collections.emptyList(), Collections.emptyList(), "Browse database paper record.", summary));
                }
            }
        }

        // Get user favorites map for quick UI star state
        Set<Long> favoritePaperIds = new HashSet<>();
        if (user != null) {
            List<Favorite> favorites = paperService.getUserFavorites(user, "ALL");
            for (Favorite f : favorites) {
                favoritePaperIds.add(f.getPaper().getId());
            }
        }

        model.addAttribute("query", query);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedYear", year);
        model.addAttribute("categories", categories);
        model.addAttribute("years", years);
        model.addAttribute("results", results);
        model.addAttribute("insight", insight);
        model.addAttribute("favoritePaperIds", favoritePaperIds);
        model.addAttribute("activeNav", "explorer");

        return "explorer";
    }

    @GetMapping("/paper-details/{id}")
    public String paperDetails(@PathVariable("id") Long id,
                               @RequestParam(value = "query", required = false, defaultValue = "") String query,
                               HttpSession session,
                               Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        Optional<ResearchPaper> paperOpt = paperService.getPaperById(id);

        if (paperOpt.isEmpty()) {
            return "redirect:/explorer";
        }

        ResearchPaper paper = paperOpt.get();

        // Calculate score & summary relative to optional query context
        RecommendationResult detailResult = null;
        if (query != null && !query.trim().isEmpty()) {
            List<RecommendationResult> recs = recommendationService.recommendPapers(query.trim(), null, null);
            for (RecommendationResult r : recs) {
                if (r.getPaper().getId().equals(id)) {
                    detailResult = r;
                    break;
                }
            }
        }

        if (detailResult == null) {
            String summary = summaryService.generateExtractiveSummary(paper.getAbstractText(), paper.getKeywords(), Collections.emptyList());
            detailResult = new RecommendationResult(paper, 0.95, Arrays.asList(paper.getCategory().split("\\s+")), Arrays.asList(paper.getKeywords().split(",\\s*")), "Selected paper record from corpus database.", summary);
        }

        boolean isFav = paperService.isFavorite(user, paper);

        // Find related papers in same category
        List<ResearchPaper> relatedPapers = paperService.getAllPapers().stream()
                .filter(p -> !p.getId().equals(id) && p.getCategory().equalsIgnoreCase(paper.getCategory()))
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("paper", paper);
        model.addAttribute("result", detailResult);
        model.addAttribute("isFavorite", isFav);
        model.addAttribute("relatedPapers", relatedPapers);
        model.addAttribute("activeNav", "explorer");

        return "paper-details";
    }

    @GetMapping("/compare")
    public String comparePapers(@RequestParam(value = "ids", required = false) String idsParam,
                                HttpSession session,
                                Model model) {
        List<ResearchPaper> selectedPapers = new ArrayList<>();

        if (idsParam != null && !idsParam.trim().isEmpty()) {
            String[] ids = idsParam.split(",");
            for (String idStr : ids) {
                try {
                    Long id = Long.parseLong(idStr.trim());
                    paperService.getPaperById(id).ifPresent(selectedPapers::add);
                } catch (NumberFormatException ignored) {}
            }
        }

        // If no papers selected yet, pick top 2 default papers for demonstration
        if (selectedPapers.isEmpty()) {
            List<ResearchPaper> all = paperService.getAllPapers();
            if (all.size() >= 2) {
                selectedPapers.add(all.get(0));
                selectedPapers.add(all.get(1));
            }
        }

        List<ResearchPaper> allPapers = paperService.getAllPapers();

        model.addAttribute("selectedPapers", selectedPapers);
        model.addAttribute("allPapers", allPapers);
        model.addAttribute("activeNav", "compare");

        return "compare";
    }

    @GetMapping("/timeline")
    public String researchTimeline(HttpSession session, Model model) {
        List<ResearchPaper> allPapers = paperService.getAllPapers();

        // Group papers by publication year in descending order
        Map<Integer, List<ResearchPaper>> timelineMap = allPapers.stream()
                .collect(Collectors.groupingBy(
                        ResearchPaper::getPublicationYear,
                        TreeMap::new,
                        Collectors.toList()
                ));

        // Descending order of years
        NavigableMap<Integer, List<ResearchPaper>> descendingTimeline = ((TreeMap<Integer, List<ResearchPaper>>) timelineMap).descendingMap();

        model.addAttribute("timelineMap", descendingTimeline);
        model.addAttribute("totalPapers", allPapers.size());
        model.addAttribute("activeNav", "timeline");

        return "timeline";
    }
}
