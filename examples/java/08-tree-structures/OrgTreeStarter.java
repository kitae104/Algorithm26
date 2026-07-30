import java.util.ArrayList;
import java.util.List;

public class OrgTreeStarter {

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
        // TODO 1: 자기 자신을 먼저 출력하고("  ".repeat(depth) + 이름),
        //         각 하위 부서에 대해 printOrgChart(child, depth + 1)를 호출하세요.
    }

    /** 요구사항 2: 이름이 일치하는 부서를 찾아 반환한다. 없으면 null. */
    static Dept findDept(Dept dept, String name) {
        // TODO 2: 자기 이름이 name과 같으면 자신을 반환하고,
        //         아니면 하위 부서들을 재귀로 검색하세요. 찾으면 즉시 반환!
        return null;
    }

    /** 요구사항 3: 트리의 높이 = 루트에서 가장 먼 부서까지의 간선 수. 리프는 0. */
    static int height(Dept dept) {
        // TODO 3: 하위 부서가 없으면 0을 반환하고,
        //         있으면 (자식들의 높이 중 최댓값) + 1을 반환하세요.
        return 0;
    }

    /** 요구사항 4: 이 부서를 포함한 전체 부서 수를 반환한다. */
    static int countDepts(Dept dept) {
        // TODO 4: 자기 자신(1) + 모든 하위 부서의 countDepts 합을 반환하세요.
        return 0;
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
