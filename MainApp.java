import java.util.*;

public class MainApp {
    public static void main(String[] args) {
        try {
            int n = 100; // Số lượng khách hàng
            List<Node> nodes = generateSampleData(n);
            double[][] dists = calculateDists(nodes);

            MasterSolver master = new MasterSolver(n);
            PricingSolver pricing = new PricingSolver(nodes, dists, 200.0, 30.0);

            // Khởi tạo cột ban đầu (Trivial)
            for (int i = 1; i <= n; i++) {
                master.addColumn(new Route(Arrays.asList(0, i, 0), new HashMap<>(), dists[0][i] * 2, n));
            }

            // Vòng lặp Sinh cột
            int iteration = 0;
            while (true) {
                double[] duals = master.solveAndGetDuals();
                if (duals == null) break;

                List<Route> newRoutes = pricing.runPricing(duals);
                if (newRoutes.isEmpty()) break;

                for (Route r : newRoutes) master.addColumn(r);

                iteration++;
                System.out.println("Iteration " + iteration + " | Lower Bound: " + master.getObjValue());
            }

            System.out.println("Giải xong! Giá trị tối ưu LP: " + master.getObjValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Node> generateSampleData(int n) {
        List<Node> list = new ArrayList<>();
        list.add(new Node(0, 50, 50, 0, 0, 1000, 0)); // Depot
        Random r = new Random(42);
        for (int i = 1; i <= n; i++) {
            list.add(new Node(i, r.nextDouble()*100, r.nextDouble()*100, 10, 0, 1000, 10));
        }
        return list;
    }

    private static double[][] calculateDists(List<Node> nodes) {
        int n = nodes.size();
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = Math.sqrt(Math.pow(nodes.get(i).x - nodes.get(j).x, 2) + Math.pow(nodes.get(i).y - nodes.get(j).y, 2));
            }
        }
        return d;
    }
}