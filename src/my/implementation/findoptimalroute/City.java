package my.implementation.findoptimalroute;


import java.util.Map;
import java.util.TreeMap;

/*
  Class City allows to describe each city included to the list according to task.
 */
public class City {

    private int cityNumber; // unique index of a separate city
    private String name; // name of city
    private int numberOfNeighbours; // number of other cities to which current city connected directly
    Map<Integer, Integer> neighbours = new TreeMap<>(); // map of city neighbours. Neighbour's index is a key, cost is a value

    public City(int cityNumber, String name, int numberOfNeighbours) {
        this.cityNumber = cityNumber;
        this.name = name;
        this.numberOfNeighbours = numberOfNeighbours;
    }

    public int getCityNumber() {
        return cityNumber;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    public Map<Integer, Integer> getNeighbours() {
        return neighbours;
    }

    void setNeighbours(int cityIndex, int cost) {
        neighbours.put(cityIndex, cost);
    }

}
