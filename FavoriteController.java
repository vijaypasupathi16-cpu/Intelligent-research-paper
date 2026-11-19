package com.researchintelligence.controller;

import com.researchintelligence.model.Favorite;
import com.researchintelligence.model.User;
import com.researchintelligence.service.PaperService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FavoriteController {

    private final PaperService paperService;

    public FavoriteController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/favorites")
    public String showFavorites(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Favorite> favorites = paperService.getUserFavorites(user, status);

        model.addAttribute("user", user);
        model.addAttribute("favorites", favorites);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activeNav", "favorites");

        return "favorites";
    }

    @PostMapping("/api/favorites/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleFavorite(@RequestParam("paperId") Long paperId,
                                                              @RequestParam(value = "status", defaultValue = "FAVORITE") String status,
                                                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            response.put("message", "User not logged in.");
            return ResponseEntity.status(401).body(response);
        }

        boolean added = paperService.toggleFavorite(user, paperId, status);
        response.put("success", true);
        response.put("added", added);
        response.put("message", added ? "Added to your research collection!" : "Removed from your research collection.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/favorites/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStatus(@RequestParam("paperId") Long paperId,
                                                            @RequestParam("status") String status,
                                                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        paperService.updateFavoriteStatus(user, paperId, status);
        response.put("success", true);
        response.put("message", "Reading status updated to " + status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/favorites/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFavorite(@RequestParam("paperId") Long paperId,
                                                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        paperService.removeFavorite(user, paperId);
        response.put("success", true);
        response.put("message", "Removed from favorites.");
        return ResponseEntity.ok(response);
    }
}
