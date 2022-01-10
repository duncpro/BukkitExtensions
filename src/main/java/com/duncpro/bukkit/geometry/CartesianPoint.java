package com.duncpro.bukkit.geometry;

import org.bukkit.Axis;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Set;

import static com.duncpro.bukkit.geometry.Vectors.set;

/**
 * A more specialized version of this class exists for concrete use cases across the x,z plane,
 * {@link HorizontalPosition}. In general that class should be preferred to this one. But this class
 * still remains useful for cases in which the plane is chosen dynamically.
 */
public class CartesianPoint {
    final double leftValue;
    final Axis leftAxis;

    final double rightValue;
    final Axis rightAxis;

    public CartesianPoint(double leftValue, Axis leftAxis, double rightValue, Axis rightAxis) {
        this.leftValue = leftValue;
        this.leftAxis = leftAxis;
        this.rightValue = rightValue;
        this.rightAxis = rightAxis;

        if (leftAxis == rightAxis) throw new IllegalArgumentException();
    }

    public Axis getLeftAxis() {
        return leftAxis;
    }

    public Axis getRightAxis() {
        return rightAxis;
    }

    public double getValue(Axis axis) {
        if (leftAxis == axis) return leftValue;
        if (rightAxis == axis) return rightValue;
        throw new IllegalArgumentException();
    }

    public int getBlockValue(Axis axis) {
        return (int) Math.floor(getValue(axis));
    }

    public boolean isCompatible(CartesianPoint other) {
        if (other.leftAxis != this.leftAxis) return false;
        if (other.rightAxis != this.rightAxis) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartesianPoint)) return false;
        CartesianPoint that = (CartesianPoint) o;
        return Double.compare(that.leftValue, leftValue) == 0 && Double.compare(that.rightValue, rightValue) == 0 && leftAxis == that.leftAxis && rightAxis == that.rightAxis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftValue, leftAxis, rightValue, rightAxis);
    }


    @Override
    public String toString() {
        return "(" + leftValue + ", " + rightValue + ")";
    }
}
