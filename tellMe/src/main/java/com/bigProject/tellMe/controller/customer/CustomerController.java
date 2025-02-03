package com.bigProject.tellMe.controller.customer;

import com.bigProject.tellMe.config.FileUpLoadUtil;
import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public String newNoticeForm(Model model) {
        NoticeDTO noticeDTO = new NoticeDTO();
        model.addAttribute("notice", noticeDTO);
        return "manager/notice_write";
    }

    @PostMapping("/create")
    public String createNotice(NoticeDTO noticeDTO, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        System.out.println("=============="+multipartFile.toString());
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            noticeDTO.setFile(fileName);
            Notice notice = noticeService.save(noticeDTO);

            String uploadDir = "tellMe/tellMe-image/notice/" + notice.getId();
            FileUpLoadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else {
            noticeDTO.setFile(null);
            noticeService.save(noticeDTO);
        }

        return "customer/notice";
    }

    // 모든 공지사항 데이터를 조회하여 뷰에 전달하는 메서드.
    @GetMapping("/notice")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<NoticeDTO> noticeList = noticeService.paging(pageable);

        int blockLimit = 5; // 화면에 보여지는 페이지 갯수
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;  // 1, 6, 11, ~
        int endPage = ((startPage + blockLimit - 1) < noticeList.getTotalPages()) ? startPage + blockLimit - 1 : noticeList.getTotalPages();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "customer/notice";
    }

    // 공시사항 제목을 클릭하여 상세페이지 표출 메서드
    @GetMapping("/notice/{id}")
    public String getNotice(@PathVariable Long id, Model model, // 경로 상의 데이터를 받아올때는 @PathVariable 사용
                            @RequestParam(required = false, defaultValue = "1")int page) { // 요청 파라미터 'page'를 받아오며, 값이 없으면 기본값 1을 사용
        NoticeDTO noticeDTO = noticeService.getNotice(id); // NoticeDTO 반환
        model.addAttribute("notice", noticeDTO); // 뷰에 NoticeDTO 전달
        model.addAttribute("page", page); // 뷰에 page 전달
        return "customer/notice-detail"; // 공지사항 상세 페이지 뷰 반환
    }

    // 특정 공지사항을 수정하기 위한 수정 폼 페이지 표출 메서드
    @GetMapping("/update/{id}")
    public String updateNoticeForm(@PathVariable Long id, Model model,
                                   @RequestParam(required = false, defaultValue = "1")int page) {
        NoticeDTO noticeDTO = noticeService.getNotice(id); // 서비스의 getNotice(id)를 호출하여 id에 해당하는 공지사항데이터를 dto에 저장
        model.addAttribute("noticeUpdate", noticeDTO); // 가져온 데이터를 Model객체에 noticeUpdate라는 이름으로 추가후 뷰에 전달
        model.addAttribute("page", page);
        return "customer/update"; // 수정 폼 페이지로 이동
    }

    // 수정 폼에서 입력된 데이터를 서버로 전송받아 공지사항을 업데이트하는 메서드
    @PostMapping("/update")
    public String updateNotice(@ModelAttribute NoticeDTO noticeDTO, Model model) { // 폼에서 전달된 데이터를 NoticeDTO로 자동 바인딩
        NoticeDTO notice = noticeService.update(noticeDTO); // 전달된 noticeDTO 정보를 사용하여 공지사항 데이터를 업데이트
        model.addAttribute("notice", notice); // 업데이트된 공지사항을 notice라는 이름으로 뷰에 전달
        return "customer/notice-detail"; // 수정 결과를 보여주는 공지사항 상세 페이지로 이동
    }

    // 공지사항 상세페이지에서 삭제
    @GetMapping("/delete/{id}")
    public String deleteNotice(@PathVariable Long id, Model model) {
        noticeService.delete(id);
        return "redirect:/customer/notice";
    }

    // delete-notices POST 요청을 받아 선택된 공지사항들을 삭제
    @PostMapping("/delete-notices")
    @ResponseBody
    public ResponseEntity<Void> deleteNotices(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids"); // JSON 객체에서 "ids" 키로 값 가져오기
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 요청 데이터가 없을 경우 400 반환
        }
        noticeService.deleteNotices(ids); // 삭제 처리
        return ResponseEntity.ok().build(); // 성공 응답 반환
    }

}
