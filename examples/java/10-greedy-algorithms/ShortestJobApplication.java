import java.util.Comparator;
import java.util.PriorityQueue;

public class ShortestJobApplication {

    /** 작업 정보를 담는 작은 기록용 클래스 (이름, 소요 시간) */
    static class Job {
        String name;
        int minutes;

        Job(String name, int minutes) {
            this.name = name;
            this.minutes = minutes;
        }
    }

    /** 주어진 순서대로 처리했을 때 각 작업의 대기 시간과 총 대기 시간을 출력한다. */
    static int runInOrder(Job[] jobs) {
        int currentTime = 0;   // 지금까지 흐른 시간 = 다음 작업의 대기 시간
        int totalWait = 0;

        for (Job job : jobs) {
            System.out.println("  " + job.name + " (" + job.minutes
                    + "분) — 대기 " + currentTime + "분");
            totalWait += currentTime;
            currentTime += job.minutes;
        }
        System.out.println("  총 대기 시간: " + totalWait + "분");
        return totalWait;
    }

    public static void main(String[] args) {
        Job[] jobs = {
            new Job("문서 인쇄", 5),
            new Job("로고 시안", 2),
            new Job("영상 렌더링", 8),
            new Job("명함 수정", 1)
        };

        System.out.println("== 방법 1: 접수 순서대로 처리 ==");
        int waitInOrder = runInOrder(jobs);

        // 그리디: "가장 짧은 작업 먼저" — 6강의 우선순위 큐가 매 순간 최솟값을 꺼내 준다
        System.out.println();
        System.out.println("== 방법 2: 그리디 — 가장 짧은 작업 먼저 (PriorityQueue) ==");
        PriorityQueue<Job> queue =
                new PriorityQueue<>(Comparator.comparingInt(job -> job.minutes));
        for (Job job : jobs) {
            queue.offer(job);
        }

        int currentTime = 0;
        int totalWait = 0;
        while (!queue.isEmpty()) {
            Job job = queue.poll();   // 남은 작업 중 소요 시간이 가장 짧은 작업
            System.out.println("  " + job.name + " (" + job.minutes
                    + "분) — 대기 " + currentTime + "분");
            totalWait += currentTime;
            currentTime += job.minutes;
        }
        System.out.println("  총 대기 시간: " + totalWait + "분");

        System.out.println();
        System.out.println("접수 순서: 총 대기 " + waitInOrder + "분 / 짧은 작업 먼저: 총 대기 "
                + totalWait + "분");
        System.out.println("짧은 작업을 먼저 끝내면 뒤에서 기다리는 모든 작업의 대기가 줄어든다.");
    }
}
