package com.researchintelligence.controller;

import com.researchintelligence.model.SearchHistory;
import com.researchintelligence.model.User;
import com.researchintelligence.service.PaperService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HistoryController {

    private final PaperService paperService;

    public HistoryController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/history")
    public String showSearchHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<SearchHistory> historyList = paperService.getUserSearchHistory(user);

        model.addAttribute("user", user);
        model.addAttribute("historyList", historyList);
        model.addAttribute("activeNav", "history");

        return "history";
    }

    @PostMapping("/history/clear")
    public String clearHistory(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            paperService.clearUserHistory(user);
        }
        return "redirect:/history?cleared=true";
    }
}
