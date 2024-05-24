package seng201.team15.models.upgradetypes;
import seng201.team15.models.Upgrade;

/**
 * Class containing information for an upgrade that is available in the pool to be purchased in the shop
 */
public class Upgrade9 extends Upgrade {
    /**
     * Constructor
     */
    public Upgrade9() {
        super("Increase selling price by 1.25x", "Price", 35, 1.25);
    }
}