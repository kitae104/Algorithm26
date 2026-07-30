import java.util.ArrayList;
import java.util.List;

public class OrgTreeSolution {

    /** 조직도의 부서 노드: 하위 부서가 몇 개든 담을 수 있는 일반 트리 */
    static class Dept {
        String name;
        List<Dept> children = new ArrayList<>();

        Dept(String name) {
            this.name = name;
        }

        void add(Dept child) {
            children.add(child);
        }
    }

    /** 요구사항 1: 전위 순회로 전체 부서를 들여쓰기와 함께 출력한다. */
    static void printOrgChart(Dept dept, int depth) {
        System.out.println("  ".repeat(depth) + dept.name);   // 1. 자기 자신 먼저 (전위)
        for (Dept child : dept.children) {
            printOrgChart(child, depth + 1);                  // 2. 하위 부서는 한 칸 더 들여쓰기
        }
    }

    /** 요구사항 2: 이름이 일치하는 부서를 찾아 반환한다. 없으면 null. */
    static Dept findDept(Dept dept, String name) {
        if (dept.name.equals(name)) {
            return dept;                       // 자기 자신이 정답
        }
        for (Dept child : dept.children) {
            Dept found = findDept(child, name);
            if (found != null) {
                return found;                  // 하위에서 찾았으면 즉시 위로 전달
            }
        }
        return null;                           // 이 서브트리에는 없다
    }

    /** 요구사항 3: 트리의 높이 = 루트에서 가장 먼 부서까지의 간선 수. 리프는 0. */
    static int height(Dept dept) {
        if (dept.children.isEmpty()) {
            return 0;                          // 리프 노드의 높이는 0
        }
        int maxChildHeight = 0;
        for (Dept child : dept.children) {
            int childHeight = height(child);
            if (childHeight > maxChildHeight) {
                maxChildHeight = childHeight;  // 가장 높은 자식을 기억한다
            }
        }
        return maxChildHeight + 1;             // 자식까지 내려가는 간선 1개를 더한다
    }

    /** 요구사항 4: 이 부서를 포함한 전체 부서 수를 반환한다. */
    static int countDepts(Dept dept) {
        int count = 1;                         // 자기 자신
        for (Dept child : dept.children) {
            count += countDepts(child);        // 하위 부서 수를 모두 더한다
        }
        return count;
    }

    public static void main(String[] args) {
        // 회사 조직도 데이터 (수정하지 마세요)
        Dept company = new Dept("민들레소프트");

        Dept management = new Dept("경영지원본부");
        management.add(new Dept("인사팀"));
        management.add(new Dept("재무팀"));

        Dept dev = new Dept("개발본부");
        dev.add(new Dept("프론트엔드팀"));
        dev.add(new Dept("백엔드팀"));
        dev.add(new Dept("데이터팀"));

        Dept sales = new Dept("영업본부");
        sales.add(new Dept("국내영업팀"));
        sales.add(new Dept("해외영업팀"));

        company.add(management);
        company.add(dev);
        company.add(sales);

        System.out.println("== 1) 전체 부서 구조 (전위 순회) ==");
        printOrgChart(company, 0);

        System.out.println();
        System.out.println("== 2) 부서 검색 ==");
        Dept found = findDept(company, "백엔드팀");
        System.out.println("\"백엔드팀\" 검색 → " + (found == null ? "없음" : "찾음"));
        Dept missing = findDept(company, "디자인팀");
        System.out.println("\"디자인팀\" 검색 → " + (missing == null ? "없음" : "찾음"));

        System.out.println();
        System.out.println("== 3) 트리 높이 ==");
        System.out.println("높이(루트에서 가장 먼 부서까지의 간선 수): " + height(company));

        System.out.println();
        System.out.println("== 4) 부서 수 ==");
        System.out.println("전체 부서 수: " + countDepts(company));
    }
}
