package com.researchintelligence.controller;

import com.researchintelligence.model.ResearchPaper;
import com.researchintelligence.model.SearchHistory;
import com.researchintelligence.model.User;
import com.researchintelligence.service.PaperService;
import com.researchintelligence.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PaperService paperService;
    private final UserService userService;

    public AdminController(PaperService paperService, UserService userService) {
        this.paperService = paperService;
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        List<ResearchPaper> papers = paperService.getAllPapers();
        List<User> users = userService.getAllUsers();
        List<SearchHistory> allSearchHistory = paperService.getAllSearchHistories();
        Map<String, Object> analytics = paperService.getSystemAnalytics();

        model.addAttribute("user", user);
        model.addAttribute("papers", papers);
        model.addAttribute("users", users);
        model.addAttribute("searchHistory", allSearchHistory);
        model.addAttribute("analytics", analytics);
        model.addAttribute("activeNav", "admin");

        return "admin";
    }

    @PostMapping("/paper/save")
    public String savePaper(@ModelAttribute ResearchPaper paper,
                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        paperService.savePaper(paper);
        return "redirect:/admin?success=paper_saved";
    }

    @PostMapping("/paper/delete/{id}")
    public String deletePaper(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        paperService.deletePaper(id);
        return "redirect:/admin?success=paper_deleted";
    }
}
