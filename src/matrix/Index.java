package matrix;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class Index implements Comparable<Index>, Serializable{
    private int row;
    private int column;

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

    public void setRowAndCol(int row,int col)
    {
        this.row = row;
        this.column = col;
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

    @Override
    public String toString() {
        return "("+row +
                "," + column +
                ')';
    }

    @Override
    public int compareTo(@NotNull Index o) {
        return this.row == o.row ?
                Integer.compare(this.column,o.column) :
                Integer.compare(this.row,o.row);
    }

}
