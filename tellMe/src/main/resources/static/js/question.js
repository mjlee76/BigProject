// document.addEventListener('DOMContentLoaded', function() {
//    const searchForm = document.getElementById('search-form');
//    const searchInput = document.getElementById('search-input');
//    const searchCategory = document.getElementById('search-category');
//    const postList = document.querySelector('.post-list');
//    const totalPosts = document.getElementById('total-posts');
//    const tabs = document.querySelectorAll('.tab');
//
//    // 검색 폼 제출 이벤트
//    searchForm.addEventListener('submit', function(event) {
//        event.preventDefault(); // 폼 제출 방지
//
//        const query = searchInput.value.trim().toLowerCase();
//        const category = searchCategory.value;
//
//        // 모든 데이터 필터링
//        const filteredQuestions = allQuestions.filter(question => {
//            const title = question.title.toLowerCase();
//            const author = question.userName.toLowerCase();
//            const content = question.content.toLowerCase();
//
//            if (category === 'all') {
//                return title.includes(query) || author.includes(query) || content.includes(query);
//            } else if (category === 'title') {
//                return title.includes(query);
//            } else if (category === 'author') {
//                return author.includes(query);
//            } else if (category === 'content') {
//                return content.includes(query);
//            }
//            return false;
//        });
//
//        // 필터링된 결과를 화면에 표시
//        renderPosts(filteredQuestions);
//        totalPosts.textContent = filteredQuestions.length;
//    });
//
//    // 게시물 목록을 화면에 렌더링하는 함수
//    function renderPosts(questions) {
//        postList.innerHTML = ''; // 기존 목록 초기화
//
//        questions.forEach(question => {
//            const row = document.createElement('tr');
//
//            row.innerHTML = `
//                <td>${question.id}</td>
//                <td><a href="${contextPath}complaint/question/${question.id}?page=${currentPage}">${question.title}</a></td>
//                <td>${question.userName}</td>
//                <td>${new Date(question.createDate).toLocaleDateString()}</td>
//                <td>${question.status}</td>
//                <td>${question.views}</td>
//            `;
//
//            postList.appendChild(row);
//        });
//    }
//
//    // 현재 페이지 번호 추적 변수 추가
//    let currentPage = 1; // 기본값 1로 설정
//
//    // 페이지 변경 시 currentPage 업데이트
//    function updatePage(newPage) {
//        currentPage = newPage;
//    }
//
//    let currentFilter = {
//            status: 'all',
//            searchQuery: '',
//            searchCategory: 'all'
//        };
//
//    // 탭 클릭 이벤트 핸들러
//    tabs.forEach(tab => {
//        tab.addEventListener('click', function() {
//            // 활성 탭 스타일 업데이트
//            tabs.forEach(t => t.classList.remove('active'));
//            this.classList.add('active');
//
//            // 상태 필터 업데이트
//            currentFilter.status = this.dataset.status;
//            applyFilters();
//        });
//    });
//
//    // 검색 입력 이벤트 핸들러
//    searchForm.addEventListener('submit', function(event) {
//        event.preventDefault();
//        currentFilter.searchQuery = searchInput.value.trim().toLowerCase();
//        currentFilter.searchCategory = searchCategory.value;
//        applyFilters();
//    });
//
//    // 통합 필터 적용 함수
//    function applyFilters() {
//        let filtered = allQuestions;
//
//        // 1. 상태 필터 적용
//        if (currentFilter.status !== 'all') {
//            filtered = filtered.filter(q => q.status === currentFilter.status);
//        }
//
//        // 2. 검색 필터 적용
//        if (currentFilter.searchQuery) {
//            filtered = filtered.filter(q => {
//                const title = q.title.toLowerCase();
//                const author = q.userName.toLowerCase();
//                const content = q.content.toLowerCase();
//
//                switch(currentFilter.searchCategory) {
//                    case 'title':
//                        return title.includes(currentFilter.searchQuery);
//                    case 'author':
//                        return author.includes(currentFilter.searchQuery);
//                    case 'content':
//                        return content.includes(currentFilter.searchQuery);
//                    default:
//                        return title.includes(currentFilter.searchQuery) ||
//                               author.includes(currentFilter.searchQuery) ||
//                               content.includes(currentFilter.searchQuery);
//                }
//            });
//        }
//
//        renderPosts(filtered);
//        updatePostCount(filtered.length);
//    }
//
//    // 게시물 수 업데이트 함수
//    function updatePostCount(count) {
//        document.getElementById('total-posts').textContent = count;
//        document.getElementById('total-pages').textContent = Math.ceil(count / 10);
//    }
// });
//
// document.querySelectorAll('.pagination a').forEach(link => {
//    link.addEventListener('click', function(e) {
//        e.preventDefault();
//        const page = this.getAttribute('th:href').match(/page=(\d+)/)[1];
//        updatePage(page);
//        applyFilters(); // 필터 재적용
//    });
// });

