package com.apps.jlee.carcare.util;

import com.apps.jlee.carcare.Objects.Gas;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LinearRegression
{
    private List<Integer> x = new LinkedList<>();
    private List<Integer> y = new LinkedList<>();

    public LinearRegression(String type,List<Object> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            x.add((int)((((Gas)(list.get(i))).getDateRefilled())/(1000*60*60*24)));

            if(type == "cost")
                y.add((int)((Gas)(list.get(i))).getCost());
            else if(type == "miles")
                y.add((int)((Gas)(list.get(i))).getMiles());
            else
                y.add((int)((Gas)(list.get(i))).getAmount());
        }
    }
    public Double predictForValue(int predictForDependentVariable)
    {
        if (x.size() != y.size())
            throw new IllegalStateException("Must have equal X and Y data points");

        Integer numberOfDataValues = x.size();

        List<Double> xSquared = x
                .stream()
                .map(position -> Math.pow(position, 2))
                .collect(Collectors.toList());

        List<Integer> xMultipliedByY = IntStream.range(0, numberOfDataValues)
                .map(i -> x.get(i) * y.get(i))
                .boxed()
                .collect(Collectors.toList());

        Integer xSummed = x
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Integer ySummed = y
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Double sumOfXSquared = xSquared
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        Integer sumOfXMultipliedByY = xMultipliedByY
                .stream()
                .reduce((prev, next) -> prev + next)
                .get();

        int slopeNominator = numberOfDataValues * sumOfXMultipliedByY - ySummed * xSummed;
        Double slopeDenominator = numberOfDataValues * sumOfXSquared - Math.pow(xSummed, 2);
        Double slope = slopeNominator / slopeDenominator;

        double interceptNominator = ySummed - slope * xSummed;
        double interceptDenominator = numberOfDataValues;
        Double intercept = interceptNominator / interceptDenominator;

        return (slope * predictForDependentVariable) + intercept;
    }
}
