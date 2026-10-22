import java.util.*;

public class PricingSolver {
    private double[][] dists;
    private List<Node> nodes;
    private double truckCap, droneRange;

    public PricingSolver(List<Node> nodes, double[][] dists, double cap, double range) {
        this.nodes = nodes; this.dists = dists;
        this.truckCap = cap; this.droneRange = range;
    }

    public List<Route> runPricing(double[] duals) {
        int n = nodes.size() - 1;
        PriorityQueue<Label> queue = new PriorityQueue<>();
        Map<Integer, List<Label>> processed = new HashMap<>();
        List<Route> foundRoutes = new ArrayList<>();

        // Khởi tạo nhãn tại Depot (Node 0)
        queue.add(new Label(0, 0, 0, 0, new BitSet(n + 1), new BitSet(n + 1), null));

        while (!queue.isEmpty()) {
            Label curr = queue.poll();

            for (int next = 1; next <= n; next++) {
                if (curr.visited.get(next)) continue;

                double arrTime = Math.max(curr.time + dists[curr.node][next], nodes.get(next).ready);
                if (arrTime > nodes.get(next).due) continue;

                // Tính Unreachable nodes (Dành cho 100 khách hàng)
                BitSet nextUnreach = new BitSet(n + 1);
                for (int k = 1; k <= n; k++) {
                    if (arrTime + dists[next][k] > nodes.get(k).due) nextUnreach.set(k);
                }

                // Xử lý phối hợp Drone
                BitSet nextVisited = (BitSet) curr.visited.clone();
                nextVisited.set(next);
                Map<Integer, List<Integer>> dronesAtNext = new HashMap<>();
                double droneRC = 0;
                double droneWait = 0;

                for (int k = 1; k <= n; k++) {
                    if (!nextVisited.get(k) && !nextUnreach.get(k) && dists[next][k] * 2 <= droneRange) {
                        if (duals[k] > dists[next][k] * 0.2) { // Ngưỡng kinh tế
                            nextVisited.set(k);
                            droneRC -= duals[k];
                            droneWait = Math.max(droneWait, dists[next][k] * 2 + nodes.get(k).svc);
                            dronesAtNext.computeIfAbsent(next, x -> new ArrayList<>()).add(k);
                        }
                    }
                }

                double nextRC = curr.reducedCost + dists[curr.node][next] - duals[next] + droneRC;
                double depTime = arrTime + Math.max(nodes.get(next).svc, droneWait);

                if (curr.load + nodes.get(next).demand <= truckCap) {
                    Label nextL = new Label(next, depTime, curr.load + nodes.get(next).demand, nextRC, nextVisited, nextUnreach, curr);
                    if (checkDominance(nextL, processed)) {
                        queue.add(nextL);
                        if (nextRC + dists[next][0] < -1e-4) {
                            foundRoutes.add(createRoute(nextL, dronesAtNext, n));
                        }
                    }
                }
            }
        }
        return foundRoutes;
    }

    private boolean checkDominance(Label nl, Map<Integer, List<Label>> processed) {
        List<Label> list = processed.computeIfAbsent(nl.node, k -> new ArrayList<>());
        Iterator<Label> it = list.iterator();
        while (it.hasNext()) {
            Label el = it.next();
            if (el.dominates(nl)) return false;
            if (nl.dominates(el)) it.remove();
        }
        list.add(nl);
        return true;
    }

    private Route createRoute(Label l, Map<Integer, List<Integer>> d, int n) {
        List<Integer> p = new ArrayList<>();
        Label t = l; double cost = 0;
        while (t.parent != null) {
            cost += dists[t.parent.node][t.node];
            p.add(0, t.node); t = t.parent;
        }
        p.add(0, 0); p.add(0); cost += dists[l.node][0];
        return new Route(p, d, cost, n);
    }
}

class Label implements Comparable<Label> {
    int node;
    double time, load, reducedCost;
    BitSet visited, unreachable;
    Label parent;

    public Label(int n, double t, double l, double rc, BitSet v, BitSet un, Label p) {
        this.node = n; this.time = t; this.load = l; this.reducedCost = rc;
        this.visited = v; this.unreachable = un; this.parent = p;
    }

    public boolean dominates(Label o) {
        if (this.reducedCost > o.reducedCost + 1e-6) return false;
        if (this.time > o.time + 1e-6) return false;
        if (this.load > o.load + 1e-6) return false;
        BitSet v = (BitSet) this.visited.clone(); v.andNot(o.visited);
        if (!v.isEmpty()) return false;
        BitSet u = (BitSet) this.unreachable.clone(); u.andNot(o.unreachable);
        if (!u.isEmpty()) return false;
        return true;
    }

    @Override
    public int compareTo(Label o) { return Double.compare(this.reducedCost, o.reducedCost); }
}