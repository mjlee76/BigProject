# git 프로젝트 시작하기

1. 프로젝트 폴더 만들기 ex) Project

2. github 레포지토리 만들기 ex) Project
-> readme파일 생성 체크

3. 프로젝트 폴더안에서 터미널 실행

4. 현재 프젝폴더 기준 git저장소 생성 : .git폴더 생김
```bash
git init
```

5. git remote add origin https://github.com/[본인 계정명]/[연결할 레포지토리].git
-> 현재 프젝폴더와 레포지토리 연결

6. 원격저장소에 있는 main브랜치를 로컬로 가져오기(현행화)
```bash
git fetch
```

7. 로컬의 main브랜치 연결
```bash 
git checkout main
```

8. 프로젝트 폴더 내에 소스코드 파일 붙여넣기

9. git status
-> changes 확인

10. git add ~
-> changes 중 선택하거나 전체(.) add

11. git commit -m"[커밋메시지]"
-> 커밋메시지 입력

12. git push
-> 레포에 push하기

13. git checkout -b minjun
-> minjun 브랜치 생성

14. (minjun브랜치에서) git pull origin main
-> main 브랜치 내용 pull

15. (minjun브랜치에서) 소스코드 수정

16. (minjun브랜치에서) git add, git commit, git push || 소스컨드롤 탭에서 진행 
-> minjun 브랜치에 commit & push

17. (minjun브랜치에서) git pull origin main
-> main에 push전에 main브랜치 pull하기

17-1. conflict 있을 시 
-> Head(내 소스코드) +==구분자=== (main 소스코드) 
-> 둘중 선택 or 둘다 선택후 직접수정
-> minjun 브랜치에 commit & push

17-2. conflict 없을 시
-> minjun 브랜치에 commit & push

18. git checkout main
-> main브랜치 이동

19. (main 브랜치에서) git merge minjun
-> main브랜치에 minjun브랜치 병합

20. (main 브랜치에서) git push
-> 최종 병합본 main에 push