package org.example.k_market.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.RecruitDTO;
import org.example.k_market.service.admin.RecruitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecruitController {

    private final RecruitService recruitService;

    // 채용하기 목록
    @GetMapping("/admin/cs/recruit/list")
    public String list(Model model) {

        List<RecruitDTO> dtoList = recruitService.findAll();

        model.addAttribute("dtoList", dtoList);

        return "admin/cs/recruit/list";
    }

    // 채용공고 등록 처리
    @PostMapping("/admin/cs/recruit/register")
    public String register(RecruitDTO dto,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime,
                           RedirectAttributes redirectAttributes) {

        dto.setRecruitStartAt(toLocalDateTime(startDate, startTime));
        dto.setRecruitEndAt(toLocalDateTime(endDate, endTime));

        if (dto.getSellerUid() == null || dto.getSellerUid().isBlank()) {
            dto.setSellerUid("관리자");
        }

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("모집중");
        }

        dto.setCreatedAt(LocalDateTime.now());

        recruitService.register(dto);

        redirectAttributes.addFlashAttribute("message", "채용공고가 등록되었습니다.");

        return "redirect:/admin/cs/recruit/list";
    }

    // 채용공고 선택삭제
    @PostMapping("/admin/cs/recruit/delete")
    public String deleteSelected(@RequestParam(required = false) List<Integer> recruitNo,
                                 RedirectAttributes redirectAttributes) {

        recruitService.deleteSelected(recruitNo);

        redirectAttributes.addFlashAttribute("message", "선택한 채용공고가 삭제되었습니다.");

        return "redirect:/admin/cs/recruit/list";
    }

    // 채용공고 수정 화면
    @GetMapping("/admin/cs/recruit/modify")
    public String modifyForm(@RequestParam Integer id,
                             Model model) {

        RecruitDTO recruitDTO = recruitService.findById(id);

        model.addAttribute("recruit", recruitDTO);

        return "admin/cs/recruit/modify";
    }

    // 채용공고 수정 처리
    @PostMapping("/admin/cs/recruit/modify")
    public String modify(RecruitDTO dto,
                         @RequestParam(required = false) String startDate,
                         @RequestParam(required = false) String endDate,
                         @RequestParam(required = false) String startTime,
                         @RequestParam(required = false) String endTime,
                         RedirectAttributes redirectAttributes) {

        dto.setRecruitStartAt(toLocalDateTime(startDate, startTime));
        dto.setRecruitEndAt(toLocalDateTime(endDate, endTime));

        if (dto.getSellerUid() == null || dto.getSellerUid().isBlank()) {
            dto.setSellerUid("관리자");
        }

        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            dto.setStatus("모집중");
        }

        recruitService.modify(dto);

        redirectAttributes.addFlashAttribute("message", "채용공고가 수정되었습니다.");

        return "redirect:/admin/cs/recruit/list";
    }

    private LocalDateTime toLocalDateTime(String date, String time) {

        if (date == null || date.isBlank()) {
            return null;
        }

        LocalDate localDate = LocalDate.parse(date);

        LocalTime localTime;

        if (time == null || time.isBlank()) {
            localTime = LocalTime.of(0, 0);
        } else {
            localTime = LocalTime.parse(time);
        }

        return LocalDateTime.of(localDate, localTime);
    }
}