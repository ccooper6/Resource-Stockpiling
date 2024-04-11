package seng201.team0.models;
/**
 * Class for towers that will be used to fill carts with resources.
 * @author Quinn Le Lievre
 */
public class Tower {
    private String resourceType;
    private int resourceAmount;
    private double reloadSpeed;
    private int level;
    private int cost;
    public Tower() {
        setResourceType("None");
        setResourceAmount(0);
        setReloadSpeed(0);
        setLevel(0);
        setCost(0);
    }
    public Tower(String resourceType, int resourceAmount, double reloadSpeed, int level, int cost) {
        setResourceType(resourceType);
        setResourceAmount(resourceAmount);
        setReloadSpeed(reloadSpeed);
        setLevel(level);
        setCost(cost);
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getResourceType() {
        return resourceType;
    }
    public void setResourceAmount(int resourceAmount) {
        this.resourceAmount = resourceAmount;
    }
    public int getResourceAmount() {
        return resourceAmount;
    }
    public void setReloadSpeed(double reloadSpeed) {
        this.reloadSpeed = reloadSpeed;
    }
    public double getReloadSpeed() {
        return reloadSpeed;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public int getCost() {
        return cost;
    }

}