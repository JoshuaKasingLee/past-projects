package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.eclipse.jetty.io.NetworkTrafficListener;
import org.json.JSONArray;


import dungeonmania.util.Position;


public class DungeonManiaController {
    private static int dungeonIdCounter = 0; 
    public static synchronized String newDungeonId() {
        return String.valueOf(dungeonIdCounter++);
    }

    private Dungeon activeGame = null;

    public DungeonManiaController() {
    }

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public List<String> getGameModes() {
        return Arrays.asList("Standard", "Peaceful", "Hard");
    }

    /**
     * /dungeons
     * 
     * Done for you.
     */
    public static List<String> dungeons() {
        try {
            return FileLoader.listFileNamesInResourceDirectory("/dungeons");
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


    public DungeonResponse newGame(String dungeonName, String gameMode) throws IllegalArgumentException {
    
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException("Invalid dungeonName");
        }
        if (!getGameModes().contains(gameMode)) {
            throw new IllegalArgumentException();
        }

        String dungeonId = newDungeonId();

        // TODO: Make the Dungeon Class
        activeGame = new Dungeon(dungeonName, gameMode, dungeonId);
        
        String fileContentsOutput = FileLoader.loadResourceFile("/dungeons/" + dungeonName + ".json");
        JSONObject dungeonObj = new JSONObject(fileContentsOutput);

        // Extract data from Json Object

        JSONArray entityList = dungeonObj.getJSONArray("entities");
        
        // Create a list of EntityResponse
        List<EntityResponse> entityResponses = new ArrayList<EntityResponse>();
        
        for (int i = 0; i < entityList.length(); i++) {
            String entityType = entityList.getJSONObject(i).getString("type");


            // TODO: Create entities based on type in JSON File using a bunch of if statements
            //create entity object and add it into activegame
            Entity currEntity;
            String key;
            String colour;
            switch (entityType) {
                case "wall":
                    currEntity = new Wall(new Position(xPosition, yPosition), activeGame);  
                    break;
                case "exit":
                    currEntity = new Exit(new Position(xPosition, yPosition), activeGame);
                    break;
                case "boulder":
                    currEntity = new Boulder(new Position(xPosition, yPosition), activeGame);
                    break;
                case "switch":
                    currEntity = new Switch(new Position(xPosition, yPosition), activeGame);
                    break;
                case "door":
                    key = entityList.getJSONObject(i).getString("key");
                    currEntity = new Door(new Position(xPosition, yPosition), activeGame, key);
                    break;
                case "portal":
                    colour = entityList.getJSONObject(i).getString("colour");
                    currEntity = new Portal(new Position(xPosition, yPosition), activeGame, colour);
                    break;
                case "zombie_toast_spawner":
                    currEntity = new ZombieToastSpawner(new Position(xPosition, yPosition), activeGame);
                    break;
                case "one_ring":
                    currEntity = new OneRing(new Position(xPosition, yPosition), activeGame);
                    break;
                case "spider":
                    currEntity = new Spider(new Position(xPosition, yPosition), activeGame);
                    break;
                case "zombie_toast":
                    currEntity = new ZombieToast(new Position(xPosition, yPosition), activeGame);
                    break;
                case "mercenary":
                    currEntity = new Mercenary(new Position(xPosition, yPosition), activeGame);
                    break;
                case "treasure":
                    currEntity = new Treasure(new Position(xPosition, yPosition), activeGame);
                    break;
                case "key":
                    key = entityList.getJSONObject(i).getString("key");
                    currEntity = new Key(new Position(xPosition, yPosition), activeGame, key);
                    break;
                case "health_position":
                    currEntity = new HealthPotion(new Position(xPosition, yPosition), activeGame);
                    break;
                case "invincibility_potion":
                    currEntity = new InvincibilityPotion(new Position(xPosition, yPosition), activeGame);
                    break;
                case "invisibility_potion":
                    currEntity = new InvisibilityPotion(new Position(xPosition, yPosition), activeGame);
                    break;
                case "wood":
                    currEntity = new Wood(new Position(xPosition, yPosition), activeGame);
                    break;
                case "arrow":
                    currEntity = new Arrow(new Position(xPosition, yPosition), activeGame); 
                    break;
                case "bomb":
                    currEntity = new Bomb(new Position(xPosition, yPosition), activeGame);
                    break;
                case "sword":
                    currEntity = new Sword(new Position(xPosition, yPosition), activeGame);
                    break;
                case "armour":
                    currEntity = new Armour(new Position(xPosition, yPosition), activeGame);
                    break;
            }
            entityResponses.add(new EntityResponse(currEntity.getId(), currEntity.getType(), currEntity.getPosition(), currEntity.isInteractable()));

            activeGame.addEntity(currEntity);
        }
        
        JSONObject goalCondition = dungeonObj.getJSONObject("goal-condition");

        String goalString = "";
        GoalComponent overallGoal = extractAllGoals(goalCondition, activeGame, goalString);
        activeGame.setOverallGoal(overallGoal); 
            
        return new DungeonResponse(dungeonId, dungeonName, entityResponses, new ArrayList<ItemResponse>(), new ArrayList<String>(), goalString);
    }
    
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {


        // do we even need to test for invalid save name? you can always save?
        // if (allGames().contains(name)) {
        //     throw new IllegalArgumentException("Invalid saveName");
        // }
        // TODO: save activeGame as a JSON and solve it

        
        List<Entity> entities = activeGame.getEntities();

        JSONArray entitiesJSON = new JSONArray();
        // Map<String, Object> entityJSON = new Hashmap<String, Object>();
        // entityJSON.put("x", currEntity.getXPosition);
        // entityJSON.put("y", currEntity.getYPosition);
        // entityJSON.put("type", currEntity.findType());
        // entitiesJSON.put(entityJSON);

        for (int i = 0; i < entities.size(), i++) {
            Entity currEntity = entities.get(i);
            String currType = currEntity.findType();
            Map<String, Object> entityData = new HashMap<String, Object>();
            entityData.put("x", currEntity.getXPosition());
            entityData.put("y", currEntity.getYPosition());
            entityData.put("type", currEntity.findType());

            switch (currType) {
                case "portal":
                    entityData.put("colour", currEntity.getColour());
                    break;
                case "door": case "key":
                    entityData.put("key", currEntity.getKey());
                    break;
                // MAYBE REMOVE LATER!!! COULD BE UNNECESSARY
                case "switch":
                    entityData.put("hasBoulder", currEntity.hasBoulder());
                    break;
                case "zombie": case "mercenary":
                    entityData.put("hasArmour", currEntity.hasArmour());
                    break;       
                case "player":
                    entityData.put("health", currEntity.getHealth());
                    break;
            }

            JSONObject entityJSON = new JSONObject(entityData);

            entitiesJSON.put(entityJSON);
        }


        
        
        List<Entity> items = activeGame.getInventory();
        JSONArray itemsJSON = new JSONArray();

        for (int i = 0; i < items.size(); i++) {
            Entity currItem = items.get(i);
            String currType = currItem.getType();
            Map<String, Object> itemData = new HashMap<String, Object>();
            itemData.put("type", currItem.findType());

            switch (currType) {
                case "key":
                    itemData.put("key", currItem.getKey());
                    break;
                case "sword": case "armour": case "bow": case "shield":
                    itemData.put("durability", currItem.getDurability());
                    break;                
            }
            JSONObject itemJSON = new JSONObject(itemData);
            itemsJSON.put(itemJSON);

        }


        GoalComponent overallGoal = activeGame.getOverallGoal();
        JSONObject goalsJSON = overallGoal.toJSON();

        Map<String, Object> dungeonMap = new HashMap<String, Object>();
        dungeonMap.put("entities", entitiesJSON);
        dungeonMap.put("items", itemsJSON);
        dungeonMap.put("goal-condition", goalsJSON);

        JSONObject dungeonJSON = new JSONObject(dungeonMap);

        String dungeonSave = dungeonJSON.toString();
        String dungeonId = activeGame.getDungeonId();
        PrintWriter fileLocation = new PrintWriter(new FileWriter("saveFiles\\" + dungeonId));

        String dungeonName = activeGame.getDungeonName();

        return new DungeonResponse(dungeonId, dungeonName, new ArrayList<EntityResponse>(), new ArrayList<ItemResponse>(), new ArrayList<String>(), "Temp");
    }

    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        if (!allGames().contains(name)) {
            throw new IllegalArgumentException("Invalid saveName");
        }

        
        // TODO: update ActiveGame
        return new DungeonResponse(dungeonId, dungeonName, new ArrayList<EntityResponse>(), new ArrayList<ItemResponse>(), new ArrayList<String>(), "Temp");
    }

    public List<String> allGames() {
        try {
            return FileLoader.listFileNamesInResourceDirectory("/saveFiles");
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public DungeonResponse tick(String itemUsed, Direction movementDirection) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return null;
    }

    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        return null;
    }


    public boolean isCompositeGoal(String goal) {
        return (goal == "AND" || goal == "OR");
    }


    public GoalComponent extractAllGoals(JSONObject goalCondition, Dungeon activeGame, String goalString) {
        GoalComponent overallGoal = null;
        switch (goalCondition.getString("goal")) {                
            case "AND":
                CompositeGoal newAndGoal = new AndGoal();

                JSONArray subgoalsAnd = goalCondition.getJSONArray("subgoals");
                for (int i = 0; i < subgoalsAnd.length(); i++) {
                    GoalComponent goal = extractAllGoals(subgoalsAnd.getJSONObject(i), activeGame, goalString);
                    newAndGoal.addSubgoal(goal);
                }

                overallGoal = newAndGoal;
                break;

            case "OR":
                CompositeGoal newOrGoal = new OrGoal();

                JSONArray subgoalsOr = goalCondition.getJSONArray("subgoals");
                for (int i = 0; i < subgoalsOr.length(); i++) {
                    GoalComponent goal = extractAllGoals(subgoalsOr.getJSONObject(i), activeGame, goalString);
                    newOrGoal.addSubgoal(goal);
                }

                overallGoal = newOrGoal;
                break;

            case "enemies":
                overallGoal = new EnemiesAndSpawnerGoal();
                activeGame.addSimpleGoals(overallGoal);
                if (!goalString.contains("enemies")) {
                    goalString += ":enemies ";
                }
                break;
            case "treasure":
                overallGoal = new CollectTreasureGoal();
                activeGame.addSimpleGoals(overallGoal);
                if (!goalString.contains("treasure")) {
                    goalString += ":treasure ";
                }
                break;
            case "boulder":
                overallGoal = new BoulderOnSwitchGoal();
                activeGame.addSimpleGoals(overallGoal);
                if (!goalString.contains("boulder")) {
                    goalString += ":boulders ";
                }
                break;
            case "exit":
                overallGoal = new ExitGoal();
                activeGame.addSimpleGoals(overallGoal);
                if (!goalString.contains("exit")) {
                    goalString += ":exit ";
                }
                break;  
        }
        return overallGoal;
    }

    // public String goalsToJSON(GoalComponent goal) {
    //     String goalJSONString = "";
    //     if (goal instanceof AndGoal) {

    //     }
    // }

    


}