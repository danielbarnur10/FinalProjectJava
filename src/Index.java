import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Index implements Serializable ,Comparable<Index>{
    int row, column;
    int dist;

    public Index(final int row, final int column) {
        this.row=row;
        this.column=column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
    //for task 2 daniel added this getters and setter of dist
    public void setdist2source(int dist){
        this.dist=dist;
    }
    public int getdist2source(){
        return this.dist;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return row == index.row &&
                column == index.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    public static void main(String[] args) {
        List<Index> indexes = new ArrayList<>();
        indexes.add(new Index(1,1));
        indexes.add(new Index(2,2));
        indexes.add(new Index(3,3));

        System.out.println(indexes);

    }

    @Override
    public String toString() {
        return "("+row +
                "," + column +
                ')';
    }

    @Override
    public int compareTo(@NotNull Index o) {
        return Integer.compare(this.row,o.row) == 0 ?
                Integer.compare(this.column,o.column) :
                Integer.compare(this.row,o.row);
    }

    private static final long serialVersionUID = 1L;
}
