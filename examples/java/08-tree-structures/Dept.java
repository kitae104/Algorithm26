import java.util.ArrayList;
import java.util.List;

/** 조직도의 부서: 자식 수가 정해져 있지 않으므로 List로 관리하는 일반 트리 */
public class Dept {
    String name;
    int headcount;                            // 이 부서에 직접 소속된 인원
    List<Dept> children = new ArrayList<>();  // 하위 부서 목록

    /** 인원 정보 없이 이름만으로 만드는 부서 (headcount는 0) */
    Dept(String name) {
        this.name = name;
    }

    Dept(String name, int headcount) {
        this.name = name;
        this.headcount = headcount;
    }

    void add(Dept child) {
        children.add(child);
    }
}
