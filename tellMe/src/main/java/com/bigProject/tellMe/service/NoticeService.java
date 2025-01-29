package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.mapper.NoticeMapper;
import com.bigProject.tellMe.repository.NoticeRepository;
import com.bigProject.tellMe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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



    public NoticeDTO getNotice(Long id) {
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

    // 수정된 공지사항 데이터가 담긴 DTO를 Entity로 변환하여 데이터베이스에 저장하고, 공지사항을 다시 조회해서 반환
    public NoticeDTO update(NoticeDTO noticeDTO) {
        Notice notice = Notice.builder()                // DTO를 entity로 바꿔준다
                .id(noticeDTO.getId())                  // DTO의 id를 가져와 entity id로 저장
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .createDate(noticeDTO.getCreateDate())
                .views(noticeDTO.getViews())
                .file(noticeDTO.getFile())
                .build();
        noticeRepository.save(notice); // 데이터베이스에 저장

        return getNotice(noticeDTO.getId());
    }

    // 전달된 ID에 해당하는 공지사항 삭제
    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    // 공지사항들 한번에 삭제: 리스트로 전달된 ID들을 하나씩 삭제
    public void deleteNotices(List<Long> ids) {

        for (Long id : ids) {
            noticeRepository.deleteById(id);
        }
    }

    public Page<NoticeDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 5; // 한 페이지에 보여줄 글 갯수
        // 한페이지당 5개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        // page 위치에 있는 값은 0부터 시작
        Page<Notice> notices =
                noticeRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 목록: index, title, createDate, views
        Page<NoticeDTO> noticeDTOS = notices.map(notice -> new NoticeDTO(
                notice.getId(),
                notice.getTitle(),
                notice.getCreateDate(),
                notice.getViews()
        ));
        return noticeDTOS;
    }
}
