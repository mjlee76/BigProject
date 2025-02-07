//document.addEventListener("DOMContentLoaded", () => {
//    const menuItems = document.querySelectorAll(".menu-item");
//    const sections = document.querySelectorAll(".content-section");
//
//    // 메뉴 클릭 시 활성화
//    menuItems.forEach(item => {
//        item.addEventListener("click", () => {
//            menuItems.forEach(i => i.classList.remove("active"));
//            item.classList.add("active");
//
//            const target = item.getAttribute("data-target");
//            sections.forEach(section => {
//                section.classList.remove("active");
//                if (section.id === target) section.classList.add("active");
//            });
//        });
//    });
//
//    // 프로필 수정 버튼 클릭 시 알림
//    document.querySelectorAll(".edit-btn").forEach(button => {
//        button.addEventListener("click", () => {
//            alert("수정 기능은 아직 구현되지 않았습니다.");
//        });
//    });
//});
//
//document.addEventListener("DOMContentLoaded", () => {
//    const profileUpload = document.getElementById("profile-upload");
//    const profilePic = document.getElementById("profile-pic");
//
//    // 프로필 사진 변경 기능
//    profileUpload.addEventListener("change", (event) => {
//        const file = event.target.files[0];
//        if (file) {
//            const reader = new FileReader();
//            reader.onload = (e) => {
//                profilePic.src = e.target.result;
//                localStorage.setItem("profileImage", e.target.result); // 변경된 이미지 저장
//            };
//            reader.readAsDataURL(file);
//        }
//    });
//
//    // 페이지 로드 시 저장된 프로필 이미지 불러오기
//    const savedProfileImage = localStorage.getItem("profileImage");
//    if (savedProfileImage) {
//        profilePic.src = savedProfileImage;
//    }
//});
//
//document.addEventListener("DOMContentLoaded", () => {
//    const postList = document.querySelector(".post-list");
//    const totalPostsElement = document.getElementById("total-posts");
//    const totalPagesElement = document.getElementById("total-pages");
//    const pagination = document.querySelector(".pagination");
//    const contentArea = document.querySelector(".content-area");
//    const searchForm = document.getElementById("search-form");
//    const searchInput = document.getElementById("search-input");
//    const searchCategory = document.getElementById("search-category");
//    const tabs = document.querySelectorAll(".tab");
//    const postsPerPage = 10;
//
////    let posts = Array.from({ length: 89 }, (_, i) => ({
////        id: i + 1,
////        title: `게시글 제목 ${100 - i}`,
////        views: Math.floor(Math.random() * 1000),
////        content: `게시글 내용 ${100 - i}`,
////        date: new Date(new Date().setDate(new Date().getDate() - i))
////            .toISOString().split("T")[0],
////        answerStatus: ["답변완료", "처리중", "접수중"][Math.floor(Math.random() * 3)],
////        attachments: []
////    }));
//
//    let filteredPosts = [...posts];
//    let currentCategory = "all"; // 현재 선택된 카테고리
//
//    function sortPostsByDateDescending() {
//        filteredPosts.sort((a, b) => new Date(b.date) - new Date(a.date));
//    }
//
//    function updatePostCount() {
//        totalPostsElement.textContent = filteredPosts.length;
//        totalPagesElement.textContent = Math.ceil(filteredPosts.length / postsPerPage);
//    }
//
//    function renderPosts(page) {
//        const postTableBody = document.querySelector(".post-list");
//        postTableBody.innerHTML = "";
//
//        const totalPosts = filteredPosts.length;
//        const totalPages = Math.ceil(totalPosts / postsPerPage);
//
//        const start = (page - 1) * postsPerPage;
//        const end = start + postsPerPage;
//        const currentPosts = filteredPosts.slice(start, end);
//
//        if (currentPosts.length === 0) {
//            postTableBody.innerHTML = `<tr><td colspan="6">검색 결과가 없습니다.</td></tr>`;
//        } else {
//            currentPosts.forEach((post, index) => {
//                const postNumber = filteredPosts.length - (start + index);
//                const row = document.createElement("tr");
//                row.innerHTML = `
//                    <td>${postNumber}</td>
//                    <td class="post-title">
//                        <a href="javascript:void(0);" onclick="navigateToPost(${post.id})">${post.title}</a>
//                    </td>
//
//                    <td>${post.date}</td>
//                    <td class="answer-status ${
//                        post.answerStatus === "답변완료"
//                            ? "completed"
//                            : post.answerStatus === "이송이첩"
//                            ? "transferred"
//                            : "pending"
//                    }">${post.answerStatus}</td>
//                    <td>${post.views}</td>
//                `;
//                postTableBody.appendChild(row);
//            });
//        }
//
//        renderPagination(page, totalPages);
//    }
//
//    function renderPagination(currentPage, totalPages) {
//        pagination.innerHTML = "";
//
//        const prevButton = document.createElement("button");
//        prevButton.textContent = "Previous";
//        prevButton.disabled = currentPage === 1;
//        prevButton.addEventListener("click", () => renderPosts(currentPage - 1));
//        pagination.appendChild(prevButton);
//
//        const maxVisibleButtons = 5;
//        const startPage = Math.max(1, currentPage - Math.floor(maxVisibleButtons / 2));
//        const endPage = Math.min(totalPages, startPage + maxVisibleButtons - 1);
//
//        for (let i = startPage; i <= endPage; i++) {
//            const pageButton = document.createElement("button");
//            pageButton.textContent = i;
//            if (i === currentPage) {
//                pageButton.classList.add("current");
//            } else {
//                pageButton.addEventListener("click", () => renderPosts(i));
//            }
//            pagination.appendChild(pageButton);
//        }
//
//        const nextButton = document.createElement("button");
//        nextButton.textContent = "Next";
//        nextButton.disabled = currentPage === totalPages;
//        nextButton.addEventListener("click", () => renderPosts(currentPage + 1));
//        pagination.appendChild(nextButton);
//    }
//
//    let searchResults = [...posts]; // 검색 결과 데이터 (카테고리 필터와 별개)
//
//    // 검색 실행
//    function applySearchFilter() {
//        const query = searchInput.value.trim().toLowerCase();
//        const category = searchCategory.value;
//
//        // 검색어 없이 검색하면 전체 데이터 복원
//        if (query === "") {
//            searchResults = [...posts]; // 검색 결과를 전체 데이터로 복원
//            filterPosts(currentCategory, false); // 현재 선택된 카테고리 필터 적용
//            return;
//        }
//
//        // 검색어를 기준으로 전체 데이터에서 필터링
//        searchResults = posts.filter(post => {
//            const matchesCategory =
//                category === "all" ||
//                (post[category] && post[category].toLowerCase().includes(query));
//
//            const matchesQuery =
//                post.title.toLowerCase().includes(query) ||
//                post.content.toLowerCase().includes(query);
//
//            return matchesCategory && matchesQuery;
//        });
//
//        // 검색 후 현재 카테고리에 따라 필터링 적용
//        filterPosts(currentCategory, false);
//    }
//
//    // 카테고리 필터링
//    function filterPosts(category, resetSearch = true) {
//        currentCategory = category;
//
//        // 검색된 결과에서 카테고리 필터링 수행
//        filteredPosts = searchResults.filter(post =>
//            category === "all" || post.answerStatus === category
//        );
//
//        sortPostsByDateDescending();
//        updatePostCount();
//        renderPosts(1);
//    }
//
//    // 카테고리 버튼 클릭 이벤트
//    tabs.forEach(tab => {
//        tab.addEventListener("click", function () {
//            const category = this.getAttribute("data-category");
//            tabs.forEach(tab => tab.classList.remove("active"));
//            this.classList.add("active");
//
//            filterPosts(category, false); // 현재 검색 결과에서 필터링
//        });
//    });
//
//    // 검색 이벤트
//    searchForm.addEventListener("submit", (event) => {
//        event.preventDefault();
//        applySearchFilter();
//    });
//
//
//    function navigateToPost(postId) {
//        const post = posts.find(p => p.id === postId);
//        if (!post) {
//            alert("게시글을 찾을 수 없습니다.");
//            return;
//        }
//
//        post.views += 1;
//
//        const url = new URL(window.location);
//        url.searchParams.set("post", post.id);
//        window.history.pushState({ type: "post", postId: post.id }, "", url);
//
//        renderPostContent(post);
//    }
//
//    function renderPostContent(post) {
//        contentArea.style.display = "block";
//        const postTableBody = document.querySelector(".post-list");
//        postTableBody.style.display = "none";
//
//        contentArea.innerHTML = `
//            <h2>${post.title}</h2>
//            <p>${post.content}</p>
//
//            <p><strong>작성일:</strong> ${post.date}</p>
//            <p><strong>답변여부:</strong> ${post.answerStatus}</p>
//            <p><strong>조회수:</strong> ${post.views}</p>
//            <button id="back-to-list">목록으로 돌아가기</button>
//        `;
//
//        document.getElementById("back-to-list").addEventListener("click", () => {
//            renderPosts(1);
//        });
//    }
//
//    sortPostsByDateDescending();
//    updatePostCount();
//    renderPosts(1);
//});