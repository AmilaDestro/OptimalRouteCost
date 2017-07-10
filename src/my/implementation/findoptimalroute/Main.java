package my.implementation.findoptimalroute;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static List<City> cities = new ArrayList<>(); // our list of cities
    private static int index; // used to calculate index of each new city

    public static void main(String[] args) {
        System.out.println("Number of tests means how many times program will be executed.");
        System.out.println("Enter number of tests: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int numberOfTests = 0;
        try{
            numberOfTests = Integer.parseInt(reader.readLine());
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for (int t = 0; t < numberOfTests; t++) {
            System.out.println("Running test#" + (t + 1));
            System.out.println("Enter number of cities: ");
            int numberOfCities = 0;
            try{
                numberOfCities = Integer.parseInt(reader.readLine()); // input of number of cities
            }catch (IOException e) {
                System.out.println(e.getMessage());
            }
            // fill in list of cities
            for (int i = 0; i < numberOfCities; i++) {
                System.out.println("Initialization of city#" + (i + 1));
                System.out.println("Enter city name: ");
                String cityName = "";
                try{
                    cityName = reader.readLine(); // reading name of the city
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("Enter number of direct neighbours for submitted city: ");
                int numberOfNeighbours = 0;
                try{
                    numberOfNeighbours = Integer.parseInt(reader.readLine());
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                cities.add(i, new City(++index, cityName, numberOfNeighbours));
                // filling in neighbours' parameters for each city
                for (int j = 0; j < cities.get(i).getNumberOfNeighbours(); j++) {
                    System.out.println("Enter index and cost of neighbour#" + (j + 1) +
                            " into one row dividing them by space: ");
                    String input = "";
                    try{
                        input = reader.readLine();
                    }catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    Scanner scanner = new Scanner(input);
                    while (scanner.hasNext()) {
                        String nr = scanner.next(); // contains index of neighbour
                        String cost = scanner.next(); // contains neighbour's cost
                        cities.get(i).setNeighbours(Integer.parseInt(nr), Integer.parseInt(cost));
                    }
                    scanner.close();
                }
            }
            System.out.println("How many routes do you want to pave?");
            int numberOfRoutes = 0;
            try{
                numberOfRoutes = Integer.parseInt(reader.readLine());
            }catch (IOException e) {
                System.out.println(e.getMessage());
            }
            // filling in routes' parameters (start city, end city)
            for (int r = 0; r < numberOfRoutes; r++) {
                System.out.println("Building route#" + (r + 1));
                System.out.println("Enter two cities between which you want to find the cheapest path.");
                System.out.println("Fill in them into one row dividing by space:");
                String twoCities = "";
                try{
                    twoCities = reader.readLine();
                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }
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
                // now we should convert strings with names of cities into two objects of City class
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
                RouteCalculator route = new RouteCalculator(cities);
                System.out.println("Sum of paths of minimum cost: " + route.findTheCheapestRoute(routeBeginning, routeEnd));
            }
        }
    }
}
