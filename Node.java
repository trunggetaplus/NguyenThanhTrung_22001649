public class Node {
    int id;
    double x, y, demand, ready, due, svc;
    public Node(int id, double x, double y, double d, double r, double du, double s) {
        this.id = id; this.x = x; this.y = y; this.demand = d;
        this.ready = r; this.due = du; this.svc = s;
    }
}