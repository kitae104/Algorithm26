import java.util.ArrayList;
import java.util.List;

public class OrgChartApplication {

    /** 조직도의 부서: 자식 수가 정해져 있지 않으므로 List로 관리하는 일반 트리 */
    static class Dept {
        String name;
        int headcount;                            // 이 부서에 직접 소속된 인원
        List<Dept> children = new ArrayList<>();  // 하위 부서 목록

        Dept(String name, int headcount) {
            this.name = name;
            this.headcount = headcount;
        }

        void add(Dept child) {
            children.add(child);
        }
    }

    /** 부서 구조를 들여쓰기로 출력한다 (전위 순회: 자신 먼저, 그다음 하위 부서) */
    static void printOrg(Dept dept, int depth) {
        System.out.println("  ".repeat(depth) + dept.name + " (" + dept.headcount + "명)");
        for (Dept child : dept.children) {
            printOrg(child, depth + 1);           // 각 하위 부서에 대해 재귀
        }
    }

    /** 이름으로 부서를 찾는다. 없으면 null */
    static Dept findDept(Dept dept, String name) {
        if (dept.name.equals(name)) {
            return dept;                          // 자기 자신이 찾는 부서다
        }
        for (Dept child : dept.children) {
            Dept found = findDept(child, name);   // 하위 부서에서 재귀로 찾는다
            if (found != null) {
                return found;                     // 찾았으면 즉시 위로 전달
            }
        }
        return null;                              // 이 서브트리에는 없다
    }

    /** 이 부서와 모든 하위 부서의 인원 합계 */
    static int totalHeadcount(Dept dept) {
        int total = dept.headcount;               // 자신의 인원부터
        for (Dept child : dept.children) {
            total += totalHeadcount(child);       // 하위 부서의 합을 더한다
        }
        return total;
    }

    public static void main(String[] args) {
        // 조직도 데이터 만들기
        Dept ceo = new Dept("대표이사실", 2);

        Dept support = new Dept("경영지원본부", 3);
        support.add(new Dept("인사팀", 4));
        support.add(new Dept("재무팀", 5));

        Dept product = new Dept("제품개발본부", 2);
        product.add(new Dept("상품기획팀", 6));
        product.add(new Dept("품질관리팀", 7));

        Dept sales = new Dept("영업마케팅본부", 3);
        sales.add(new Dept("온라인영업팀", 8));
        sales.add(new Dept("오프라인영업팀", 9));
        sales.add(new Dept("마케팅팀", 5));

        ceo.add(support);
        ceo.add(product);
        ceo.add(sales);

        System.out.println("== 조직도 ==");
        printOrg(ceo, 0);

        System.out.println();
        System.out.println("== 부서 검색 ==");
        Dept found = findDept(ceo, "품질관리팀");
        System.out.println("품질관리팀 → " + (found == null ? "없음" : "찾음 (소속 " + found.headcount + "명)"));
        Dept missing = findDept(ceo, "디자인팀");
        System.out.println("디자인팀 → " + (missing == null ? "없음" : "찾음"));

        System.out.println();
        System.out.println("== 인원 합산 ==");
        System.out.println("회사 전체 인원: " + totalHeadcount(ceo) + "명");
        System.out.println("제품개발본부 산하 인원(본부 포함): " + totalHeadcount(product) + "명");
    }
}
