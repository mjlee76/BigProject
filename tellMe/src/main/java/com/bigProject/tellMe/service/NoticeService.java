package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.mapper.NoticeMapper;
import com.bigProject.tellMe.repository.NoticeRepository;
import com.bigProject.tellMe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeMapper noticeMapper;
    private final NoticeRepository noticeRepository;

    public Notice save(NoticeDTO noticeDTO) {
        Notice notice = noticeMapper.noDTOToNo(noticeDTO);
        return noticeRepository.save(notice);
    }



    // 데이터베이스에서 모든 notice 데이터를 조회하여 NoticeDTO 객체 리스트로 변환
    public List<NoticeDTO> findAll() {
        List<Notice> noticeList = (List<Notice>) noticeRepository.findAll();
        List<NoticeDTO> noticeDTOList = new ArrayList<>();

        // notice객체를 반복문으로 돌면서 noticeDTO객체로 변환하여 리스트에 저장
        for (Notice notice : noticeList) {
            noticeDTOList.add(NoticeDTO.toNoticeDTO(notice));
        }
        return noticeDTOList;
    }



    public NoticeDTO getNotice(Integer id) {
        Optional<Notice> optionalNotice = noticeRepository.findById(id);

        if (optionalNotice.isPresent()) {
            Notice notice = optionalNotice.get();

            notice.incrementViews(); // 조회수 증가 메서드 호출
            noticeRepository.save(notice); // 변경된 엔티티 저장

            // Notice를 NoticeDTO로 변환
            return NoticeDTO.toNoticeDTO(notice); // 공지사항 조회를 위한 DTO반환
        } else {
            throw new IllegalArgumentException("Notice not found with ID: " + id);
        }
    }

}
