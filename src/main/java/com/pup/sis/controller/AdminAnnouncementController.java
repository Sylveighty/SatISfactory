package com.pup.sis.controller;

import com.pup.sis.entity.Announcement;
import com.pup.sis.service.AnnouncementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    public AdminAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("announcements", announcementService.findAll());
        return "admin/announcements";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String publishDate,
            RedirectAttributes redirectAttributes) {

        Announcement a = new Announcement();
        a.setTitle(title);
        a.setContent(content);
        a.setPublishDate(LocalDate.parse(publishDate));
        a.setActive(true);
        announcementService.save(a);

        redirectAttributes.addFlashAttribute("success", "Announcement posted.");
        return "redirect:/admin/announcements";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Announcement status updated.");
        return "redirect:/admin/announcements";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Announcement deleted.");
        return "redirect:/admin/announcements";
    }
}