document.addEventListener("DOMContentLoaded", () => {
    const postList = document.querySelector(".post-list");
    const totalPostsElement = document.getElementById("total-posts");
    const totalPagesElement = document.getElementById("total-pages");
    const pagination = document.querySelector(".pagination");
    const contentArea = document.querySelector(".content-area");
    const searchForm = document.getElementById("search-form");
    const postsPerPage = 20;

    const deleteButton = document.getElementById("delete-button");
    const selectDeleteButton = document.getElementById("select-delete-button");
    const cancelButton = document.getElementById("cancel-button");

//    let posts = Array.from({ length: 89 }, (_, i) => ({
//        id: i + 1,
//        title: `게시글 제목 ${100 - i}`,
//        author: `작성자 ${100 - i}`,
//        views: Math.floor(Math.random() * 1000),
//        content: `게시글 내용 ${100 - i}`,
//        date: new Date(
//            new Date().setDate(new Date().getDate() - i)
//        ).toISOString().split("T")[0],
//        answerStatus: ["답변완료", "대기중", "이송이첩"][Math.floor(Math.random() * 3)],
//        attachments: []
//    }));

    let filteredPosts = [...posts];

    function sortPostsByDateDescending() {
        filteredPosts.sort((a, b) => new Date(b.date) - new Date(a.date));
    }

    function updatePostCount() {
        const totalPosts = filteredPosts.length;
        const totalPages = Math.ceil(totalPosts / postsPerPage);
        totalPostsElement.textContent = totalPosts;
        totalPagesElement.textContent = totalPages;
    }

    function renderPosts(page) {
        const postTableBody = document.querySelector(".post-list");
        postTableBody.innerHTML = "";

        const totalPosts = filteredPosts.length;
        const totalPages = Math.ceil(totalPosts / postsPerPage);

        const start = totalPosts - page * postsPerPage;
        const end = start + postsPerPage;
        const currentPosts = filteredPosts.slice(
            Math.max(0, start),
            Math.min(totalPosts, end)
        );

        currentPosts.forEach((post, index) => {
            const postNumber = totalPosts - (start + index);
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

        renderPagination(page, totalPages);
    }

    function renderPagination(currentPage, totalPages) {
        pagination.innerHTML = "";

        const prevButton = document.createElement("button");
        prevButton.textContent = "Previous";
        prevButton.disabled = currentPage === totalPages;
        prevButton.addEventListener("click", () => renderPosts(currentPage + 1));
        pagination.appendChild(prevButton);

        for (let i = totalPages; i >= 1; i--) {
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
        nextButton.disabled = currentPage === 1;
        nextButton.addEventListener("click", () => renderPosts(currentPage - 1));
        pagination.appendChild(nextButton);
    }

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
            <h3>첨부파일</h3>
            <ul>
                ${
                    post.attachments.length
                        ? post.attachments
                              .map(
                                  file =>
                                      `<li><a href="${file.url}" download>${file.name}</a></li>`
                              )
                              .join("")
                        : "<li>첨부파일이 없습니다.</li>"
                }
            </ul>
            <button id="back-to-list">목록으로 돌아가기</button>
        `;

        document.getElementById("back-to-list").addEventListener("click", () => {
            renderPosts(1);
        });
    }

    searchForm.addEventListener("submit", event => {
        event.preventDefault();
        const category = document.getElementById("search-category").value;
        const query = document.getElementById("search-input").value.toLowerCase();
        const answerStatus = document.getElementById("search-answer-status").value;

        filteredPosts = posts.filter(post => {
            const matchesCategory =
                category === "all"
                    ? post.title.toLowerCase().includes(query) ||
                      post.author.toLowerCase().includes(query) ||
                      post.content.toLowerCase().includes(query)
                    : post[category]?.toLowerCase().includes(query);
            const matchesAnswerStatus =
                answerStatus === "all" || post.answerStatus === answerStatus;

            return matchesCategory && matchesAnswerStatus;
        });

        sortPostsByDateDescending();
        updatePostCount();
        renderPosts(1);
    });

    deleteButton.addEventListener("click", () => {
        document.getElementById("checkbox-header").style.display = "table-cell";
        const selectAllCheckbox = document.getElementById("select-all-checkbox");

        // 기존 체크박스를 모두 삭제하고 새로 추가
        document.querySelectorAll(".post-table tbody tr").forEach(row => {
            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.className = "post-checkbox";
            const cell = document.createElement("td");
            cell.appendChild(checkbox);
            row.prepend(cell);
        });

        deleteButton.style.display = "none";
        selectDeleteButton.style.display = "inline-block";
        cancelButton.style.display = "inline-block";

        // 전체 선택 이벤트 핸들러
        selectAllCheckbox.checked = false; // 초기화
        selectAllCheckbox.addEventListener("change", () => {
            const isChecked = selectAllCheckbox.checked;
            document.querySelectorAll(".post-checkbox").forEach(checkbox => {
                checkbox.checked = isChecked; // 전체 선택 상태에 따라 개별 체크박스 설정
            });
        });
    });

    selectDeleteButton.addEventListener("click", () => {
        document.querySelectorAll(".post-checkbox:checked").forEach(checkbox => {
            const row = checkbox.closest("tr");
            const postNumber = parseInt(row.querySelector("td").textContent);
            filteredPosts.splice(filteredPosts.length - postNumber, 1);
            row.remove();
        });

        cancelSelection();
        updatePostCount();
    });

    cancelButton.addEventListener("click", cancelSelection);

    function cancelSelection() {
        document.getElementById("checkbox-header").style.display = "none";
        document.querySelectorAll(".post-table tbody tr").forEach(row => {
            if (row.firstChild.tagName === "TD") {
                row.removeChild(row.firstChild);
            }
        });

        deleteButton.style.display = "inline-block";
        selectDeleteButton.style.display = "none";
        cancelButton.style.display = "none";
    }

    sortPostsByDateDescending();
    updatePostCount();
    renderPosts(1);
});
