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

    /**
     * 공지사항 목록을 페이지 단위로 조회하는 메서드
     *
     * @param pageable 페이지 정보(PageRequest) 객체 (페이지 번호, 정렬 방식 포함)
     * @return Page<NoticeDTO> 페이징된 공지사항 목록 (id, 제목, 작성일, 조회수 포함)
     */
    public Page<NoticeDTO> paging(Pageable pageable) {

        // ✅ 현재 요청된 페이지 번호에서 1을 뺀 값 (Spring Data JPA는 페이지 인덱스를 0부터 시작함)
        int page = pageable.getPageNumber() - 1;
        // ✅ 한 페이지에서 보여줄 공지사항 개수
        int pageLimit = 10;
        // ✅ 공지사항을 ID 기준으로 내림차순 정렬하여 페이지네이션 적용된 데이터 조회
        // PageRequest.of(페이지 번호, 페이지당 개수, 정렬 기준)
        Page<Notice> notices =
                noticeRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // ✅ 조회된 데이터를 NoticeDTO 형태로 변환하여 반환
        // Notice 엔티티의 index, title, createDate, views만 매핑하여 DTO로 변환
        Page<NoticeDTO> noticeDTOS = notices.map(notice -> new NoticeDTO(
                notice.getId(),
                notice.getTitle(),
                notice.getCreateDate(),
                notice.getViews()
        ));
        return noticeDTOS; // 변환된 공지사항 DTO 목록 반환
    }
}
