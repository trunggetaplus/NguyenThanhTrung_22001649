import ilog.concert.*;
import ilog.cplex.*;

public class MasterSolver {
    private IloCplex cplex;
    private IloObjective obj;
    private IloRange[] visitCons;
    private int numNodes;

    public MasterSolver(int n) throws IloException {
        this.cplex = new IloCplex();
        this.numNodes = n;
        this.visitCons = new IloRange[n + 1];
        cplex.setOut(null);
        obj = cplex.addMinimize();
        for (int i = 1; i <= n; i++) {
            visitCons[i] = cplex.addEq(cplex.numExpr(), 1.0, "C" + i);
        }
    }

    public void addColumn(Route route) throws IloException {
        IloColumn col = cplex.column(obj, route.cost);
        for (int i = 1; i <= numNodes; i++) {
            if (route.served.get(i)) col = col.and(cplex.column(visitCons[i], 1.0));
        }
        cplex.numVar(col, 0.0, 1.0);
    }

    public double[] solveAndGetDuals() throws IloException {
        if (!cplex.solve()) return null;
        double[] duals = new double[numNodes + 1];
        for (int i = 1; i <= numNodes; i++) duals[i] = cplex.getDual(visitCons[i]);
        return duals;
    }

    public double getObjValue() throws IloException { return cplex.getObjValue(); }
}