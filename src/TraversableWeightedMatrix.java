import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TraversableWeightedMatrix implements Traversable<Index>{

    protected final Matrix matrix;
    protected Index startIndex;
    protected int[][] costMatrix;
    protected boolean[][] visited;

    public TraversableWeightedMatrix(Matrix matrix) {
        this.matrix = matrix;
        this.costMatrix = new int[matrix.getPrimitiveMatrix().length][matrix.getPrimitiveMatrix()[0].length];
        this.visited = new boolean[matrix.getPrimitiveMatrix().length][matrix.getPrimitiveMatrix()[0].length];

        initializeCostMatrix();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Index getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        Node<Index> node = new Node<>(startIndex);
        node.setCost(0);
        return node;
    }

    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
//            if (!visited[index.getRow()][index.getColumn()]) {
                Node<Index> node = new Node<>(index, someNode);
                int alt = someNode.getCost() + getMatrix().getValue(index);
                if (alt < getCellCostMatrix(index))
                    node.setCost(alt);
                reachableIndex.add(node);
//            }
        }
//        visited[someNode.getData().getRow()][someNode.getData().getColumn()] = true;
        return reachableIndex;
    }

    public boolean IsNeedRelaxation(Node<Index> source,Node<Index> neighbor)
    {
        if(source.getCost() + matrix.getValue(neighbor.getData()) < neighbor.getCost())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }

    private void printCostMatrix()
    {
        for(int[] row : costMatrix)
        {
            System.out.println(Arrays.toString(row));
        }
    }
    private void setCellCostMatrix(Index index , int val)
    {
        costMatrix[index.getRow()][index.getColumn()] = val;
    }
    private int getCellCostMatrix(Index index)
    {
        return costMatrix[index.getRow()][index.getColumn()];
    }
    private void initializeCostMatrix()
    {
        int rowSize = matrix.primitiveMatrix.length;
        int colSize = matrix.primitiveMatrix[0].length;

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize ; j++) {
                this.costMatrix[i][j] = Integer.MAX_VALUE;

                this.visited[i][j] = false;
            }

        }
    }

}