package com.implemica.testtask2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class RouteCalculator {
    private static List<City> cities = new ArrayList<>(); // our list of cities
    private static int index; // used to calculate index of each new city
    private static Set<Integer> visitedCities = new LinkedHashSet<>(); // set of visited cities
    private static Map<Integer, Integer> vertexCosts = new TreeMap<>(); // map of all vertices belonging to graph
    // next 2 variables are used to hold neighbours of current city and their adjacent points
    private static Map<Integer, Integer> nearestNeighbours = new HashMap<>();
    private static Map<Integer, Integer> minCostNeighbours = new HashMap<>();
    private static boolean isVertexValueChanged; // indicated if vertex value was decreased
    private static int pathsSum; // holds sum of minimum pathes between pairs of cities on the route
    private static City prev; // holds link to the object of City type for which adjacent points are analyzed
    private static boolean endRiched = false; // indicates if end of the route is reached

    public static void main(String[] args) throws IOException{
        System.out.println("Number of test means how many times program will be executed.");
        System.out.println("Enter number of test: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int numberOfTests = Integer.parseInt(reader.readLine());
        for (int t = 0; t < numberOfTests; t++) {
            System.out.println("Enter number of cities: ");
            int numberOfCities = Integer.parseInt(reader.readLine()); // entering number of cities
            // fill in list of cities
            for (int i = 0; i < numberOfCities; i++) {
                System.out.println("Enter city name: ");
                String cityName = reader.readLine(); // reades name of the city
                System.out.println("Enter number of direct neighbours for submitted city: ");
                int numberOfNeighbours = Integer.parseInt(reader.readLine());
                cities.add(i, new City(++index, cityName, numberOfNeighbours));
                // fill in neighbours' parameters for each city
                for (int j = 0; j < cities.get(i).getNumberOfNeighbours(); j++) {
                    System.out.println("Enter neighbour's index and cost into one row dividing them by space: ");
                    String input = reader.readLine();
                    Scanner scanner = new Scanner(input);
                    while (scanner.hasNext()) {
                        String nr = scanner.next(); // contains index of neighbour
                        String cost = scanner.next(); // contains neighbour cost
                        cities.get(i).setNeighbours(Integer.parseInt(nr), Integer.parseInt(cost));
                    }
                    scanner.close();
                }
            }

//        for(City city: cities) {
//            System.out.println(city.toString());
//            System.out.println();
//        }

            System.out.println("How many routes do you want to pave?");
            int numberOfRoutes = Integer.parseInt(reader.readLine());
            for (int r = 0; r < numberOfRoutes; r++) {
                System.out.println("Enter two cities between which you want to find the cheapest path");
                String twoCities = reader.readLine();
                Scanner scanner2 = new Scanner(twoCities);
                String city1 = "";
                String city2 = "";
                while (scanner2.hasNext()) {
                    city1 = scanner2.next();
                    city2 = scanner2.next();
                }
                scanner2.close();
                // check if city1 and city2 are in the list of cities
                int includedToList = 0;
                for (City city : cities) {
                    if (city.getName().equals(city1) || city.getName().equals(city2)) {
                        ++includedToList;
                    }
                }
                if (includedToList < 2) {
                    System.out.println("Error: both cities should be included to the list");
                }
                // now we should convert strings with names of cities into two object of City class
                City routeBeginning = null;
                City routeEnd = null;
                for (City city : cities) {
                    if (city.getName().equals(city1)) {
                        routeBeginning = city;
                    }
                    if (city.getName().equals(city2)) {
                        routeEnd = city;
                    }
                }
                // and find the cheapest route between city1 and city2
                System.out.println("Sum of paths of minimum cost: " + findTheCheapestRoute(routeBeginning, routeEnd));
            }
        }
    }

    // Method searches paths of minimum cost between pairs of cities and summarizes them
    public static int findTheCheapestRoute(City beginning, City end) {
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
        nearestNeighbours = beginning.getNeighbours();
        // Now we'll find neighbours of the first city and update weights of their vertices
        for (Map.Entry<Integer, Integer> nbrOfStart: nearestNeighbours.entrySet()) {
            updateVertexValue(nbrOfStart);
        }
        prev = beginning;
        while(!endRiched) {
            visitedCities.add(prev.getCityNumber()); // city from which we continue our path should be marked as visited
            minCostNeighbours = neighboursWithLowestCost(prev);
            // ищем смежные точки с такими соседями и пробуем менять веса вершин
            // We'll iterate through map of neighbours that have lowest cost
            for (Map.Entry<Integer, Integer> cheapN : minCostNeighbours.entrySet()) {
                // there's no need to go back to the city that has already been visited
                if (visitedCities.contains(cheapN.getKey())) {
                    continue;
                }
                updateVertexValue(cheapN); // updating weight of the current neighbour if necessary
                if (cheapN.getKey().equals(end.getCityNumber())){ // check if the end of the route reached
                    visitedCities.add(cheapN.getKey());
                    endRiched = true;
                    break;
                }
                /* in case if end of the route was not reached earlier we have to find adjacent point of current
                   neighbour and iterate through them
                 */
                nearestNeighbours = neighboursWithLowestCost(findCityByIndex(cheapN.getKey()));
                for (Map.Entry<Integer, Integer> adjPoints : nearestNeighbours.entrySet()) {
                    updateVertexValue(adjPoints);
                    /* if vertex value of the adjacent point was decreased we should add it's parent to set
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

        // finally we count sum of weights of all visited cities that are on the route
        pathsSum = calculation();
        return pathsSum;
    }

    // Method checks if two cities connected directly
    private static boolean connectedDirectly(City beginning, City end) {
        Map<Integer, Integer> neighbours = beginning.getNeighbours();
        if (neighbours.containsKey(end.getCityNumber())){
            return true;
        }
        return false;
    }

    // Method allows to find city by the index
    private static City findCityByIndex(int index) {
        for (City city: cities) {
            if (city.getCityNumber() == index) {
                return city;
            }
        }
        return null;
    }

    // Method receives map of nearest neighbours and finds the lowest cost
    private static int lowestCostOfNeighbour(Map<Integer, Integer> neighbours){
        int min = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> n: neighbours.entrySet()) {
            if (n.getValue() < min) {
                min = n.getValue();
            }
        }
        return min;
    }

    // Method updates weights of graph's vertices
    private static void updateVertexValue(Map.Entry<Integer, Integer> neighbour) {
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
    private static Map<Integer,Integer> neighboursWithLowestCost(City city) {
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
    private static int calculation() {
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
