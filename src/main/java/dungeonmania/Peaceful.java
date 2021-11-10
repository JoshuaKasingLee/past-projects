package dungeonmania;


public class Peaceful extends Gamemode {

    public Peaceful() {
        super(false, 0, Player.ORIGINAL_HEALTH_STANDARD, true, 1);
    }


    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return "Peaceful";
    }

}