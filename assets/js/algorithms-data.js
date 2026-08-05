/*
 * 13개 강좌의 단일 데이터 소스.
 * 랜딩 페이지 카드, 강의 페이지의 이전/다음 링크, 진행률 계산이 모두 이 배열을 사용한다.
 * 새 강의를 추가할 때는 docs/adding-lessons.md 절차에 따라 이 파일과 data/algorithms.json을 함께 수정한다.
 */
/*
 * 분류 이름 → 색 키. common.css의 [data-cat] 표와 짝을 이룬다.
 * 여기에 없는 분류는 색이 붙지 않고 중립색으로 남는다(조용히 깨지지 않는다).
 * 새 분류를 만들 때는 이 표와 common.css의 --cat-* 토큰을 함께 늘린다.
 */
window.CATEGORY_KEYS = {
    "알고리즘 기초": "basics",
    "자료구조": "structure",
    "정렬": "sorting",
    "탐색": "search",
    "그래프": "graph",
    "설계 기법": "design",
    "프로젝트": "project",
    /* 보충 자료 전용. 13강 커리큘럼의 7개 학습 영역이 아니라서 랜딩의
       영역 칩에는 나타나지 않는다(칩은 ALGORITHMS에서만 만들어진다).
       색을 따로 준 이유는 그 반대다 — "이건 강의가 아니다"를 색으로 먼저
       읽히게 하려는 것. */
    "자바 문법": "lang"
};

window.ALGORITHMS = [
    {
        order: 1,
        id: "algorithm-basics",
        title: "알고리즘과 효율적인 문제 해결",
        englishTitle: "Algorithms and Efficient Problem Solving",
        category: "알고리즘 기초",
        difficulty: "초급",
        examples: 9,
        language: "Java",
        description:
            "알고리즘의 의미와 문제 해결 과정, 시간 복잡도와 Big-O를 학습하고 두 가지 데이터 처리 방식의 실행 횟수를 직접 비교합니다.",
        path: "algorithms/01-algorithm-basics.html"
    },
    {
        order: 2,
        id: "arrays-and-lists",
        title: "배열과 리스트를 활용한 데이터 처리",
        englishTitle: "Data Processing with Arrays and Lists",
        category: "자료구조",
        difficulty: "초급",
        examples: 10,
        language: "Java",
        description:
            "배열과 ArrayList를 순회하며 합계, 평균, 최댓값, 조건 검색, 빈도 계산을 구현하고 성적 분석 프로그램을 완성합니다.",
        path: "algorithms/02-arrays-and-lists.html"
    },
    {
        order: 3,
        id: "brute-force-string-hash",
        title: "완전 탐색과 문자열·해시 처리",
        englishTitle: "Brute Force, Strings, and Hashing",
        category: "알고리즘 기초",
        difficulty: "초급",
        examples: 10,
        language: "Java",
        description:
            "모든 경우를 확인하는 완전 탐색과 문자열 처리, HashMap과 HashSet을 이용한 빈도 분석을 학습하고 단어 분석 프로그램을 만듭니다.",
        path: "algorithms/03-brute-force-string-hash.html"
    },
    {
        order: 4,
        id: "sorting-algorithms",
        title: "정렬 알고리즘과 객체 정렬",
        englishTitle: "Sorting Algorithms and Object Sorting",
        category: "정렬",
        difficulty: "초급",
        examples: 11,
        language: "Java",
        description:
            "선택·버블·삽입 정렬의 동작 원리를 단계별로 구현하고 Comparable과 Comparator로 상품 목록을 다중 기준 정렬합니다.",
        path: "algorithms/04-sorting-algorithms.html"
    },
    {
        order: 5,
        id: "search-algorithms",
        title: "순차 탐색과 이진 탐색",
        englishTitle: "Linear Search and Binary Search",
        category: "탐색",
        difficulty: "초급",
        examples: 11,
        language: "Java",
        description:
            "순차 탐색과 이진 탐색의 범위 축소 과정을 비교하고 도서·재고 검색 프로그램으로 정렬된 데이터의 힘을 확인합니다.",
        path: "algorithms/05-search-algorithms.html"
    },
    {
        order: 6,
        id: "stack-and-queue",
        title: "스택과 큐를 활용한 작업 처리",
        englishTitle: "Task Processing with Stacks and Queues",
        category: "자료구조",
        difficulty: "초급",
        examples: 11,
        language: "Java",
        description:
            "LIFO 스택과 FIFO 큐의 동작을 구현하고 괄호 검사, 실행 취소, 고객 대기열 기능을 가진 작업 관리 프로그램을 만듭니다.",
        path: "algorithms/06-stack-and-queue.html"
    },
    {
        order: 7,
        id: "recursion-and-backtracking",
        title: "재귀 호출과 백트래킹",
        englishTitle: "Recursion and Backtracking",
        category: "탐색",
        difficulty: "중급",
        examples: 10,
        language: "Java",
        description:
            "종료 조건과 호출 스택을 이해하고 선택-진행-취소 구조의 백트래킹으로 조합 생성과 미로 탈출 프로그램을 구현합니다.",
        path: "algorithms/07-recursion-and-backtracking.html"
    },
    {
        order: 8,
        id: "tree-structures",
        title: "트리 구조와 트리 순회",
        englishTitle: "Tree Structures and Tree Traversal",
        category: "자료구조",
        difficulty: "중급",
        examples: 9,
        language: "Java",
        description:
            "루트, 부모·자식, 리프 개념부터 이진 탐색 트리의 삽입·검색과 전위·중위·후위·레벨 순회를 구현하고 조직도를 트리로 표현합니다.",
        path: "algorithms/08-tree-structures.html"
    },
    {
        order: 9,
        id: "graph-search",
        title: "그래프와 DFS·BFS 탐색",
        englishTitle: "Graphs, DFS, and BFS",
        category: "그래프",
        difficulty: "중급",
        examples: 9,
        language: "Java",
        description:
            "인접 리스트와 방문 배열로 그래프를 표현하고 DFS와 BFS로 친구 관계, 네트워크 연결 상태를 탐색합니다.",
        path: "algorithms/09-graph-search.html"
    },
    {
        order: 10,
        id: "greedy-algorithms",
        title: "그리디 알고리즘",
        englishTitle: "Greedy Algorithms",
        category: "설계 기법",
        difficulty: "중급",
        examples: 12,
        language: "Java",
        description:
            "매 순간 최선을 선택하는 그리디 전략으로 동전 교환과 회의실 배정을 해결하고 그리디가 실패하는 사례도 분석합니다.",
        path: "algorithms/10-greedy-algorithms.html"
    },
    {
        order: 11,
        id: "dynamic-programming",
        title: "동적 계획법",
        englishTitle: "Dynamic Programming",
        category: "설계 기법",
        difficulty: "중급",
        examples: 9,
        language: "Java",
        description:
            "중복 계산을 메모이제이션과 DP 테이블로 제거하고 점화식을 세워 계단 오르기와 배낭 문제 기초를 해결합니다.",
        path: "algorithms/11-dynamic-programming.html"
    },
    {
        order: 12,
        id: "shortest-path",
        title: "최단 경로 알고리즘",
        englishTitle: "Shortest Path Algorithms",
        category: "그래프",
        difficulty: "중급",
        examples: 10,
        language: "Java",
        description:
            "거리 배열과 우선순위 큐로 다익스트라 알고리즘을 구현하고 캠퍼스 건물 사이의 최소 이동 비용과 경로를 계산합니다.",
        path: "algorithms/12-shortest-path.html"
    },
    {
        order: 13,
        id: "algorithm-project",
        title: "알고리즘 종합 프로젝트",
        englishTitle: "Algorithm Integration Project",
        category: "프로젝트",
        difficulty: "중급",
        examples: 10,
        language: "Java",
        description:
            "요구사항 분석부터 자료구조 선택, 알고리즘 결합, 테스트까지 배운 내용 전체를 사용해 실전 프로그램을 설계하고 완성합니다.",
        path: "algorithms/13-algorithm-project.html"
    }
];

/*
 * 보충 자료 — 13강 커리큘럼 바깥의 "추가 정보".
 *
 * 강의가 아니라 사전 공부용 참고 문서다. 그래서 ALGORITHMS와 섞지 않는다.
 *  - 진행률·완료 표시를 만들지 않는다(13강 진도를 흐리지 않는다).
 *  - 20개 섹션 규격을 따르지 않는다. 필요한 만큼만 쓴다.
 *  - 랜딩에서도 "강의 계획"이 아니라 별도의 "추가 정보" 섹션에 놓인다.
 * 강의와 공유하는 것은 페이지 골격(lesson.css)과 코드 카드·퀴즈뿐이다.
 *
 * relatedLessons는 "이 문서를 읽고 나면 어느 강의 코드를 바꿔 볼 수 있는가"다.
 * 카드에 그대로 찍히므로 강의 order를 쓴다.
 */
window.SUPPLEMENTS = [
    {
        order: 1,
        id: "lambda-expressions",
        title: "람다식",
        englishTitle: "Lambda Expressions",
        category: "자바 문법",
        difficulty: "보충",
        examples: 5,
        language: "Java 17+",
        summary: "익명 클래스 6줄을 한 줄로",
        description:
            "메서드 하나를 값처럼 넘기는 문법입니다. 함수형 인터페이스와 메서드 참조까지 익혀 4강의 Comparator, 5강의 검색 조건을 익명 클래스 없이 다시 씁니다.",
        relatedLessons: [4, 5, 10],
        path: "supplements/lambda-expressions.html"
    },
    {
        order: 2,
        id: "java-streams",
        title: "자바 스트림",
        englishTitle: "Java Streams",
        category: "자바 문법",
        difficulty: "보충",
        examples: 6,
        language: "Java 17+",
        summary: "반복문 대신 파이프라인",
        description:
            "데이터를 거르고 바꾸고 모으는 과정을 한 줄기로 잇는 API입니다. 2강의 성적 집계, 3강의 단어 빈도 계산을 for 반복문 없이 다시 씁니다.",
        relatedLessons: [2, 3, 4],
        path: "supplements/java-streams.html"
    }
];
