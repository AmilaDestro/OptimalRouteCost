package my.implementation.findoptimalroute;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/*
  Class RouteCalculator allows to calculate cost of optimal route between pair of cities which is
  sum of minimal paths between start point and destination point.
 */
public class RouteCalculator {
    private List<City> cities; // incoming list of cities
    private Set<Integer> visitedCities = new LinkedHashSet<>(); // set of visited cities
    private Map<Integer, Integer> vertexCosts = new TreeMap<>(); // map of all vertices belonging to graph
    // next 2 variables are used to hold neighbours of current city and their adjacent points
    private Map<Integer, Integer> minCostNeighbours = new HashMap<>();
    private Map<Integer, Integer> adjPoints = new HashMap<>();
    private boolean isVertexValueChanged; // indicates if vertex value was decreased
    private int pathsSum; // holds sum of minimum paths between pairs of cities on the route
    private City prev; // holds link to the object of City type for which adjacent points are analyzed
    private boolean endRiched = false; // indicates if end of the route is reached

    public RouteCalculator(List<City> cities) {
        this.cities = cities;
    }

    // Method searches paths of minimum cost between pairs of cities and summarizes them
    public int findTheCheapestRoute(City beginning, City end) {
        pathsSum = 0;
        endRiched = false;
        visitedCities.clear();
        vertexCosts.clear();
        vertexCosts.put(beginning.getCityNumber(), 0); // Starting vertex, it's weight will always be 0
        // Initial weight of the rest vertices is infinity
        for (City city : cities) {
            if (city.getCityNumber() != beginning.getCityNumber()) {
                vertexCosts.put(city.getCityNumber(), Integer.MAX_VALUE);
            }
        }
        visitedCities.add(beginning.getCityNumber()); // Beginning of the route is added to set of visited cities

        // Check if two cities transmitted as method parameters are connected directly
        if (connectedDirectly(beginning, end)) {
            Map<Integer, Integer> neighboursOfStart = beginning.getNeighbours();
            int min = lowestCostOfNeighbour(neighboursOfStart);
            for (Map.Entry<Integer, Integer> neighbour: neighboursOfStart.entrySet()) {
                // Check if direct connection has minimum cost
                if (neighbour.getKey().equals(end.getCityNumber()) && neighbour.getValue() == min) {
                    pathsSum += neighbour.getValue();
                    return pathsSum;
                }
            }
        }
        adjPoints = beginning.getNeighbours();
        // Now we'll find neighbours of the first city and update weights of their vertices
        for (Map.Entry<Integer, Integer> nbrOfStart: adjPoints.entrySet()) {
            updateVertexValue(nbrOfStart);
        }
        prev = beginning;
        while(!endRiched) {
            visitedCities.add(prev.getCityNumber()); // city from which we continue our path should be marked as visited
            minCostNeighbours = neighboursWithLowestCost(prev);
            // We'll iterate through map of neighbours that have lowest cost.
            for (Map.Entry<Integer, Integer> cheapN : minCostNeighbours.entrySet()) {
                // There's no need to go back to the city that has already been visited.
                if (visitedCities.contains(cheapN.getKey())) {
                    continue;
                }
                updateVertexValue(cheapN); // updating weight of the current neighbour if necessary
                if (cheapN.getKey().equals(end.getCityNumber())){ // check if the end of the route reached
                    visitedCities.add(cheapN.getKey());
                    endRiched = true;
                    break;
                }
                /* In case if end of the route was not reached earlier we have to find adjacent point of current
                   neighbour and iterate through them.
                 */
                adjPoints = neighboursWithLowestCost(findCityByIndex(cheapN.getKey()));
                for (Map.Entry<Integer, Integer> adjPoints : adjPoints.entrySet()) {
                    updateVertexValue(adjPoints);
                    /* If vertex value of the adjacent point was decreased we should add it's parent to set
                       of visited cities and look for it's further neighbours if end of the route is not reached yet.
                     */
                    if (isVertexValueChanged) {
                        visitedCities.add(cheapN.getKey());
                        prev = findCityByIndex(adjPoints.getKey());

                        if (adjPoints.getKey().equals(end.getCityNumber())) {
                            visitedCities.add(adjPoints.getKey());
                            endRiched = true;
                            break;
                        }
                    }
                }
            }
        }

        // Finally we count sum of weights of all visited cities that are on the route.
        pathsSum = calculation();
        return pathsSum;
    }

    // Method checks if two cities connected directly
    private boolean connectedDirectly(City beginning, City end) {
        Map<Integer, Integer> neighbours = beginning.getNeighbours();
        if (neighbours.containsKey(end.getCityNumber())){
            return true;
        }
        return false;
    }

    // Method allows to find city by the index
    private City findCityByIndex(int index) {
        for (City city: cities) {
            if (city.getCityNumber() == index) {
                return city;
            }
        }
        return null;
    }

    // Method receives map of nearest neighbours and finds the lowest cost
    private int lowestCostOfNeighbour(Map<Integer, Integer> neighbours){
        int min = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> n: neighbours.entrySet()) {
            if (n.getValue() < min) {
                min = n.getValue();
            }
        }
        return min;
    }

    // Method updates weights of graph's vertices
    private void updateVertexValue(Map.Entry<Integer, Integer> neighbour) {
        isVertexValueChanged = false;
        for (Map.Entry<Integer, Integer> vertex: vertexCosts.entrySet()) {
            if (vertex.getKey().equals(neighbour.getKey())){
                if (neighbour.getValue() < vertex.getValue()) {
                    vertexCosts.replace(vertex.getKey(), neighbour.getValue());
                    isVertexValueChanged = true;
                }
            }
        }
    }


    //Method returns map of neighbours with minimum cost
    private Map<Integer,Integer> neighboursWithLowestCost(City city) {
        Map<Integer, Integer> neighbours = city.getNeighbours();
        Map<Integer, Integer> map = new HashMap<>();
        int min = lowestCostOfNeighbour(neighbours);
        for (Map.Entry<Integer, Integer> entry: neighbours.entrySet()) {
            if (entry.getValue() == min) {
                map.put(entry.getKey(),entry.getValue());
            }
        }
        return map;
    }

    // Method sums weights of the vertices that are included to the list of visited cities (belonging to the route)
    private int calculation() {
        int sum = 0;
        for (Integer visit: visitedCities) {
            for(Map.Entry<Integer, Integer> v: vertexCosts.entrySet()) {
                if (visit == v.getKey()) {
                    sum += v.getValue();
                }
            }
        }
        return sum;
    }
}
