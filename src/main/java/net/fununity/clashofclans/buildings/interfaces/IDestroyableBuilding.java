package net.fununity.clashofclans.buildings.interfaces;

public interface IDestroyableBuilding extends IBuilding {

    /**
     * Random amount how much gems a player gets from destroying the building
     * @return int - the amount of gems received.
     * @since 0.0.1
     */
    int getGems();

    /**
     * When the building is destroyed the player gets back the full money.
     * @return boolean - receive full money back.
     * @since 0.0.1
     */
    boolean receiveFullPayPrice();

}
