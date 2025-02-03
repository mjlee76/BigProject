document.addEventListener("DOMContentLoaded", () => {
    const postList = document.querySelector(".post-list");
    const totalPostsElement = document.getElementById("total-posts");
    const totalPagesElement = document.getElementById("total-pages");
    const pagination = document.querySelector(".pagination");
    const contentArea = document.querySelector(".content-area");
    const searchForm = document.getElementById("search-form");
    const searchInput = document.getElementById("search-input");
    const searchCategory = document.getElementById("search-category");
    const tabs = document.querySelectorAll(".tab");
    const postsPerPage = 20;

    let posts = Array.from({ length: 89 }, (_, i) => ({
        id: i + 1,
        title: `게시글 제목 ${100 - i}`,
        author: `작성자 ${100 - i}`,
        views: Math.floor(Math.random() * 1000),
        content: `게시글 내용 ${100 - i}`,
        date: new Date(new Date().setDate(new Date().getDate() - i))
            .toISOString().split("T")[0],
        answerStatus: ["답변완료", "처리중", "접수중"][Math.floor(Math.random() * 3)],
        attachments: []
    }));

    let filteredPosts = [...posts];
    let currentCategory = "all"; // 현재 선택된 카테고리

    function sortPostsByDateDescending() {
        filteredPosts.sort((a, b) => new Date(b.date) - new Date(a.date));
    }

    function updatePostCount() {
        totalPostsElement.textContent = filteredPosts.length;
        totalPagesElement.textContent = Math.ceil(filteredPosts.length / postsPerPage);
    }

    function renderPosts(page) {
        const postTableBody = document.querySelector(".post-list");
        postTableBody.innerHTML = "";

        const totalPosts = filteredPosts.length;
        const totalPages = Math.ceil(totalPosts / postsPerPage);

        const start = (page - 1) * postsPerPage;
        const end = start + postsPerPage;
        const currentPosts = filteredPosts.slice(start, end);

        if (currentPosts.length === 0) {
            postTableBody.innerHTML = `<tr><td colspan="6">검색 결과가 없습니다.</td></tr>`;
        } else {
            currentPosts.forEach((post, index) => {
                const postNumber = start + index + 1;
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${postNumber}</td>
                    <td class="post-title">
                        <a href="javascript:void(0);" onclick="navigateToPost(${post.id})">${post.title}</a>
                    </td>
                    <td>${post.author}</td>
                    <td>${post.date}</td>
                    <td class="answer-status ${
                        post.answerStatus === "답변완료"
                            ? "completed"
                            : post.answerStatus === "이송이첩"
                            ? "transferred"
                            : "pending"
                    }">${post.answerStatus}</td>
                    <td>${post.views}</td>
                `;
                postTableBody.appendChild(row);
            });
        }

        renderPagination(page, totalPages);
    }

    function renderPagination(currentPage, totalPages) {
        pagination.innerHTML = "";

        const prevButton = document.createElement("button");
        prevButton.textContent = "Previous";
        prevButton.disabled = currentPage === 1;
        prevButton.addEventListener("click", () => renderPosts(currentPage - 1));
        pagination.appendChild(prevButton);

        const maxVisibleButtons = 5;
        const startPage = Math.max(1, currentPage - Math.floor(maxVisibleButtons / 2));
        const endPage = Math.min(totalPages, startPage + maxVisibleButtons - 1);

        for (let i = startPage; i <= endPage; i++) {
            const pageButton = document.createElement("button");
            pageButton.textContent = i;
            if (i === currentPage) {
                pageButton.classList.add("current");
            } else {
                pageButton.addEventListener("click", () => renderPosts(i));
            }
            pagination.appendChild(pageButton);
        }

        const nextButton = document.createElement("button");
        nextButton.textContent = "Next";
        nextButton.disabled = currentPage === totalPages;
        nextButton.addEventListener("click", () => renderPosts(currentPage + 1));
        pagination.appendChild(nextButton);
    }

    function filterPosts(category) {
        currentCategory = category;

        if (category === "all") {
            filteredPosts = [...posts];
        } else {
            filteredPosts = posts.filter(post => post.answerStatus === category);
        }

        applySearchFilter();
    }

    function applySearchFilter() {
        const query = searchInput.value.trim().toLowerCase();
        const category = searchCategory.value;

        filteredPosts = filteredPosts.filter(post => {
            const matchesCategory =
                category === "all" ||
                (post[category] && post[category].toLowerCase().includes(query));

            const matchesQuery =
                query === "" ||
                post.title.toLowerCase().includes(query) ||
                post.author.toLowerCase().includes(query) ||
                post.content.toLowerCase().includes(query);

            return matchesCategory && matchesQuery;
        });

        sortPostsByDateDescending();
        updatePostCount();
        renderPosts(1);
    }

    tabs.forEach(tab => {
        tab.addEventListener("click", function () {
            const category = this.getAttribute("data-category");
            tabs.forEach(tab => tab.classList.remove("active"));
            this.classList.add("active");
            filterPosts(category);
        });
    });

    searchForm.addEventListener("submit", (event) => {
        event.preventDefault();
        applySearchFilter();
    });

    function navigateToPost(postId) {
        const post = posts.find(p => p.id === postId);
        if (!post) {
            alert("게시글을 찾을 수 없습니다.");
            return;
        }

        post.views += 1;

        const url = new URL(window.location);
        url.searchParams.set("post", post.id);
        window.history.pushState({ type: "post", postId: post.id }, "", url);

        renderPostContent(post);
    }

    function renderPostContent(post) {
        contentArea.style.display = "block";
        const postTableBody = document.querySelector(".post-list");
        postTableBody.style.display = "none";

        contentArea.innerHTML = `
            <h2>${post.title}</h2>
            <p>${post.content}</p>
            <p><strong>작성자:</strong> ${post.author}</p>
            <p><strong>작성일:</strong> ${post.date}</p>
            <p><strong>답변여부:</strong> ${post.answerStatus}</p>
            <p><strong>조회수:</strong> ${post.views}</p>
            <button id="back-to-list">목록으로 돌아가기</button>
        `;

        document.getElementById("back-to-list").addEventListener("click", () => {
            renderPosts(1);
        });
    }

    sortPostsByDateDescending();
    updatePostCount();
    renderPosts(1);
});
