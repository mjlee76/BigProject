document.addEventListener("DOMContentLoaded", () => {
  const postList = document.querySelector(".post-list");
  const totalPostsElement = document.getElementById("total-posts");
  const totalPagesElement = document.getElementById("total-pages");
  const pagination = document.querySelector(".pagination");
  const contentArea = document.querySelector(".content-area"); // 게시글 내용 영역
  const searchInput = document.querySelector(".search-bar input"); // 검색 입력
  const searchButton = document.querySelector(".search-bar button"); // 검색 버튼
  const postsPerPage = 20;

  // 예시 게시글 데이터
  let posts = [
    {
      id: 1,
      title: "청년 지원 사업",
      author: "비공개",
      views: 3,
      content:
        "청년 지원 사업에 대한 자세한 설명입니다. 청년들에게 제공되는 혜택과 신청 절차에 대해 안내드립니다.",
      date: getCurrentDate(), // 작성 날짜를 동적으로 추가
    },
    {
      id: 2,
      title: "자유공원에 화장실이 부족하네요",
      author: "고*군",
      views: 7,
      content:
        "자유공원 방문객이 많아 화장실이 부족한 상황입니다. 화장실 추가 설치가 필요합니다.",
      date: getCurrentDate(), // 작성 날짜를 동적으로 추가
    },
    {
      id: 3,
      title: "신호등 고장",
      author: "박*영",
      views: 1,
      content:
        "신호등이 작동하지 않아 교통에 불편을 주고 있습니다. 신속한 수리가 필요합니다.",
      date: getCurrentDate(), // 작성 날짜를 동적으로 추가
    },
  ];

  let filteredPosts = posts; // 현재 표시 중인 게시글 데이터

  // 현재 날짜를 반환하는 함수
  function getCurrentDate() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0"); // 월은 0부터 시작하므로 +1 필요
    const day = String(now.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  function updatePostCount() {
    const totalPosts = filteredPosts.length;
    const totalPages = Math.ceil(totalPosts / postsPerPage);
    totalPostsElement.textContent = totalPosts;
    totalPagesElement.textContent = totalPages;
  }

  function renderPosts(page = 1) {
    postList.innerHTML = "";
    contentArea.style.display = "none"; // 게시글 내용을 숨김
    postList.style.display = "block"; // 목록을 표시

    const start = (page - 1) * postsPerPage;
    const end = start + postsPerPage;
    const currentPosts = filteredPosts.slice(start, end);

    currentPosts.forEach((post) => {
      const li = document.createElement("li");
      li.innerHTML = `
                <div class="post-title">${post.title}</div>
                <div class="post-meta">${post.date} | ${post.author} | 조회수 : ${post.views}</div>
            `;
      li.addEventListener("click", () => navigateToPost(post.id));
      postList.appendChild(li);
    });

    renderPagination(page);
  }

  function renderPagination(currentPage) {
    pagination.innerHTML = "";

    const totalPages = Math.ceil(filteredPosts.length / postsPerPage);

    const prevButton = document.createElement("button");
    prevButton.textContent = "Previous";
    prevButton.disabled = currentPage === 1;
    prevButton.addEventListener("click", () => renderPosts(currentPage - 1));
    pagination.appendChild(prevButton);

    for (let i = 1; i <= totalPages; i++) {
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

  function navigateToPost(postId) {
    const post = posts.find((p) => p.id === postId);
    if (!post) {
      alert("게시글을 찾을 수 없습니다.");
      return;
    }

    // 조회수 증가
    post.views += 1;

    // URL 변경
    const url = new URL(window.location);
    url.searchParams.set("post", post.id);
    window.history.pushState({ type: "post", postId: post.id }, "", url);

    // 게시글 내용 렌더링
    renderPostContent(post);
  }

  function renderPostContent(post) {
    contentArea.style.display = "block"; // 게시글 내용을 표시
    postList.style.display = "none"; // 목록 숨기기

    contentArea.innerHTML = `
            <h2>${post.title}</h2>
            <p>${post.content}</p>
            <p><strong>작성자:</strong> ${post.author}</p>
            <p><strong>작성일:</strong> ${post.date}</p>
            <p><strong>조회수:</strong> ${post.views}</p>
            <button id="back-to-list">목록으로 돌아가기</button>
        `;

    document.getElementById("back-to-list").addEventListener("click", () => {
      window.history.pushState({ type: "list" }, "", window.location.pathname); // URL 초기화
      renderPosts(); // 목록 표시
    });
  }

  function searchPosts() {
    const searchTerm = searchInput.value.toLowerCase(); // 입력값을 소문자로 변환
    filteredPosts = posts.filter((post) =>
      post.title.toLowerCase().includes(searchTerm)
    ); // 제목에서 검색

    // 검색 후 URL 및 상태 업데이트
    window.history.pushState(
      { type: "search", searchTerm },
      "",
      `?search=${encodeURIComponent(searchTerm)}`
    );
    updatePostCount();
    renderPosts(); // 검색된 결과 렌더링
  }

  // 검색 버튼 클릭 이벤트 추가
  searchButton.addEventListener("click", searchPosts);

  // 뒤로 가기 및 앞으로 가기 시 URL 상태 반영
  window.addEventListener("popstate", (event) => {
    if (!event.state || event.state.type === "list") {
      filteredPosts = posts; // 전체 게시글로 복원
      updatePostCount();
      renderPosts();
    } else if (event.state.type === "search") {
      const searchTerm = event.state.searchTerm || "";
      searchInput.value = searchTerm; // 검색창에 검색어 유지
      filteredPosts = posts.filter((post) =>
        post.title.toLowerCase().includes(searchTerm)
      );
      updatePostCount();
      renderPosts();
    } else if (event.state.type === "post") {
      const post = posts.find((p) => p.id === event.state.postId);
      if (post) renderPostContent(post);
    }
  });

  // 초기 로드
  updatePostCount();
  renderPosts();
});
