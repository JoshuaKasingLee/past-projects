package dungeonmania;

import dungeonmania.util.Position;


public class SunStone extends Item {

    public SunStone(Position position, Dungeon dungeon) {
        super(position, dungeon);
    }

    /** 
     * @return String
     */
    @Override
    public String setType() {
        return "sun_stone";
    }
    
}
