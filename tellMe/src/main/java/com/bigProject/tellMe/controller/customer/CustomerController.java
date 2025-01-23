package com.bigProject.tellMe.controller.customer;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final NoticeService noticeService;

    @GetMapping("/service")
    public String customerService() {
        return "customer/service";
    }

    @GetMapping("/write")
    public String newNoticeForm() {
        return "customer/write";
    }

    @PostMapping("/create")
    public String createNotice(NoticeDTO noticeDTO) {
        noticeDTO.setCreateDate(LocalDateTime.now());
        noticeDTO.setViews(0);

        Notice saved = noticeService.save(noticeDTO);
        return "redirect:/customer/notice";
    }

    // 모든 공지사항 데이터를 조회하여 뷰에 전달하는 메서드.
    @GetMapping("/notice")
    public String findAll(Model model) {
        List<NoticeDTO> noticeDTOList = noticeService.findAll();
        model.addAttribute("noticeList", noticeDTOList); // "noticeList"라는 이름으로 DTO 리스트를 모델에 추가
        return "customer/notice";
    }

//    // 클릭 시 공지사항을 조회하는 요청이 Controller 로 전달되고, NoticeService.getNotice 호출
//    @GetMapping("/notice/{id}")
//    @ResponseBody
//    public String getNotice(@PathVariable Integer id, Model model) { // 경로 상의 데이터를 받아올때는 @PathVariable 사용
//        Notice notice = noticeService.getNotice(id); // 조회수 증가 포함
//        model.addAttribute("notice", notice);
//        return "customer/notice-detail"; // 공지사항 상세 페이지 뷰 반환
//    }
    @GetMapping("/notice/{id}")
    public String getNotice(@PathVariable Long id, Model model) { // 경로 상의 데이터를 받아올때는 @PathVariable 사용
        NoticeDTO noticeDTO = noticeService.getNotice(id); // NoticeDTO 반환
        model.addAttribute("notice", noticeDTO); // 뷰에 NoticeDTO 전달
        return "customer/notice-detail"; // 공지사항 상세 페이지 뷰 반환
    }

    @GetMapping("/update/{id}")
    public String updateNotice(@PathVariable Long id, Model model) {
        NoticeDTO noticeDTO = noticeService.getNotice(id);
        model.addAttribute("noticeUpdate", noticeDTO);
        return "customer/update";
    }

}
