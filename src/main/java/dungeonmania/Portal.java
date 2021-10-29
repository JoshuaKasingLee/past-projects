package dungeonmania;

import dungeonmania.util.Position;
import dungeonmania.util.Direction;

public class Portal extends StaticEntity {
    private Portal pairedPortal;
    private String colour;

    public Portal(Position position, Dungeon dungeon, String colour) {
        super(position, dungeon);
        pairedPortal = findPairedPortal();
        this.colour = colour;
    }

    @Override
    public String setType() {
        return "Portal";
    }

    @Override
    public void update(Direction direction) {
        if (this.getPosition().equals(this.getPlayerPosition())) {
            teleportEntity(this.getDungeon().getPlayer(), direction);
        }
    }

    public void teleportEntity(Entity entity, Direction direction) {
        // for Entity in entities, if entity != this && == portal, teleport moveDirection + entityPosition
        Position currPos = this.getPosition();
        Position dir = direction.getOffset();
        Position newPos = new Position((currPos.getX() + dir.getX()), (currPos.getY() + dir.getY()));

        // Same check as Player

        for (Entity e : getDungeon().getEntities(newPos)) {
            if (!(e instanceof Wall) ||
                !(e instanceof Boulder) ||
                !(e instanceof Door) ||
                !(e instanceof ZombieToastSpawner)) {
                getDungeon().getPlayer().setPosition(currPos);
                break;
            } else {
                getDungeon().getPlayer().setPosition(newPos);
            }
        }
    }

    public Portal findPairedPortal() {
        for (Entity entity : this.getEntities()) {
            if (entity instanceof Portal && !entity.equals(this) && ((Portal)entity).getPortalColour().equals(getPortalColour())) {
                return (Portal)entity;
            }
        }
        // paired portal does not exist
        return null;
    }

    public String getPortalColour() {
        return this.colour;
    }


}
