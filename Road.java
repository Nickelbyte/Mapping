// Data record to represent an edge in the graph or a road between two cities
public record Road(int start, int destination) {
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (!(o instanceof Road))
            return false;
        
        Road r = (Road) o;
        return this.start == r.start() && this.destination == r.destination();
    }
}
