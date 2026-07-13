package org.example.k_market.controller.admin.setting;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.VersionDTO;
import org.example.k_market.service.admin.VersionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/version-management")
public class VersionController {

    private final VersionService versionService;

    // build.gradle의 version 값이 빌드 시 주입됨
    @Value("${spring.application.version}")
    private String currentBuildVersion;

    // 버전관리 페이지 (목록 표시)
    @GetMapping
    public String versionManagement(Model model) {
        List<VersionDTO> versionList = versionService.findAll();
        model.addAttribute("versionList", versionList);
        model.addAttribute("currentBuildVersion", currentBuildVersion); // 등록 모달에 자동 채움용
        return "admin/setting/version";
    }

    // 버전 내역 등록 (일반 form POST) - 관리자가 수동으로 새 이력을 남길 때 사용
    @PostMapping("/register")
    public String register(@ModelAttribute VersionDTO dto, HttpSession session) {
        String loginUid = (String) session.getAttribute("loginMember");
        dto.setWriterUid(loginUid);
        versionService.register(dto);
        return "redirect:/admin/version-management";
    }

    // 선택 삭제 (일반 form POST)
    @PostMapping("/delete")
    public String delete(@RequestParam("versionNo") List<String> ids) {
        versionService.deleteAll(ids);
        return "redirect:/admin/version-management";
    }

    // 배포 시 자동 등록된 버전의 변경내역(content)을 관리자가 나중에 채워넣기 위한 수정 API (새로 추가)
    @PostMapping("/update-content")
    public String updateContent(@RequestParam("id") String id,
                                @RequestParam("content") String content) {
        versionService.updateContent(id, content);
        return "redirect:/admin/version-management";
    }
}