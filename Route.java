import java.util.*;

public class Route {
    List<Integer> path; // Lộ trình xe tải
    Map<Integer, List<Integer>> droneTrips; // Drone tại mỗi điểm dừng
    double cost;
    BitSet served;

    public Route(List<Integer> path, Map<Integer, List<Integer>> drones, double cost, int n) {
        this.path = new ArrayList<>(path);
        this.droneTrips = drones;
        this.cost = cost;
        this.served = new BitSet(n + 1);
        for (int i : path) if (i > 0) served.set(i);
        for (List<Integer> targets : drones.values()) {
            for (int t : targets) served.set(t);
        }
    }
}