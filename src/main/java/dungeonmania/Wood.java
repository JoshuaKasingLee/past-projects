package dungeonmania;

import dungeonmania.util.Position;

public class Wood extends Item {

    public Wood (Position position, Dungeon dungeon) {
        super(position, dungeon);
        dungeon.addEntity(this);
    }
    
    @Override
    public String setType() {
        return "Wood";
    }
}
