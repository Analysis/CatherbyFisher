import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.bank.BankType;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.SkillTracker;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;

@ScriptManifest(version = 1.0, name = "Catherby Fisher", description = "", category = Category.FISHING, author = "Chewy")
public class CatherbyFisher extends AbstractScript {
    private enum State{
        BANK, FISH
    }


    Area fishArea = new Area(2835, 3435, 2843, 3432);
    //Area midArea = new Area(2826, 3436, 2824, 3438);
    Area bankArea = new Area(2807, 3441, 2811, 3440);

    State state;

    @Override
    public void onStart() {
        state = State.FISH;
        getSkillTracker().start(Skill.FISHING);
    }

    private void runBank() {
        //Tile midTile = midArea.getRandomTile();
        Tile bankTile = bankArea.getRandomTile();
        // if (midTile.distance() < bankTile.distance()) {
        //    getWalking().walk(midTile);
        //    sleepWhile(() -> getLocalPlayer().isMoving(), Calculations.random(13000, 15000));
        //}
        getWalking().walk(bankTile);
        if (getLocalPlayer().getTile().distance(bankTile) < 15) {
            sleepUntil(() -> (getLocalPlayer().getTile().distance(bankTile) < Calculations.random(1,4)), Calculations.random(13000,15000));
        } else {
            sleep(Calculations.random(765, 3423));
        }

    }

    private void runFish() {
        //midTile = midArea.getRandomTile();
        Tile fishTile = fishArea.getRandomTile();
        // if (midTile.distance() < fishTile.distance()) {
        //    getWalking().walk(midTile);
        //    sleepWhile(() -> getLocalPlayer().isMoving(), Calculations.random(13000, 15000));
        // }
        getWalking().walk(fishTile);
        if (getLocalPlayer().getTile().distance(fishTile) < 15) {
            sleepUntil(() -> (getLocalPlayer().getTile().distance(fishTile) < Calculations.random(3,6)), Calculations.random(13000,15000));
        } else {
            sleep(Calculations.random(765, 3423));
        }
        //sleepUntil(() -> (getWalking().getDestinationDistance() == Calculations.random(1,5)), Calculations.random(13000,15000));
    }

    @Override
    public int onLoop() {
        switch(state) {
            case BANK:
                Entity bank = getBank().getClosestBank(BankType.NPC);
                if (bank == null || (getLocalPlayer().getTile().distance(bank) > 5)) {
                    runBank();
                    break;
                }
                bank.interact("Bank");
                sleepWhile(() -> !(getBank().isOpen()), 10000);
                getBank().depositAllExcept("Lobster pot");
                sleepWhile(() -> (getInventory().contains("Raw lobster")), Calculations.random(150,300));
                sleep(Calculations.random(160, 370));
                getBank().close();
                sleepUntil(() -> !getBank().isOpen(), Calculations.random(150,300));

                runFish();
                state = State.FISH;
                break;
            case FISH:

                if(getInventory().isFull()) {
                    state = State.BANK;
                    sleep(Calculations.random(1140,3730));
                    break;
                }
                if(!getInventory().contains("Lobster pot")) {
                    log("No lobster pot");
                    stop();
                }

                NPC closestPool = getNpcs().closest(1519);
                if (closestPool == null) {
                    runFish();
                    break;
                }
                closestPool.interact("Cage");
                sleepUntil(() -> getLocalPlayer().isAnimating(), Calculations.random(4300,7430));
                if(Calculations.random(0,5) <= 1) {
                    sleep(Calculations.random(130,345));
                    getMouse().moveMouseOutsideScreen();
                }
                sleepUntil(() -> !getLocalPlayer().isAnimating(), Calculations.random(50323,105050));
                if (!(getNpcs().closest(1519).equals(closestPool))) {
                    sleep(Calculations.random(2407,5633));
                    getWalking().walk(getNpcs().closest(1519).getSurroundingArea(5).getRandomTile());
                    sleep(500);
                    sleepUntil(() -> !getLocalPlayer().isMoving(), Calculations.random(4300,7430));
                    break;
                }
                if (Calculations.random(0,5) >= 4) {
                    sleep(Calculations.random(2241, 7543));
                } else {
                    sleep(Calculations.random(1400, 4703));
                }

                break;
        }
        return Calculations.random(11,24);
    }

    @Override
    public void onPaint(Graphics g) {
        g.setFont(new Font("Arial", 1, 11));
        g.setColor(Color.GREEN);

        g.drawString("XP Gained: " + getSkillTracker().getGainedExperience(Skill.FISHING) + " (" + getSkillTracker().getGainedExperiencePerHour(Skill.FISHING) + "/h)", 569, 412);
        g.drawString("Fished: " + getSkillTracker().getGainedExperience(Skill.FISHING)/90,569, 432);

    }
}
