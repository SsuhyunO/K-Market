package org.example.k_market.controller.admin.file;

import lombok.RequiredArgsConstructor;
import org.example.k_market.service.admin.AdminConfigService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalModelController {

    private final AdminConfigService adminConfigService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {

        try {
            model.addAttribute("siteConfig", adminConfigService.findById(1));
        } catch (Exception e) {
            model.addAttribute("siteConfig", null);
        }
    }
}