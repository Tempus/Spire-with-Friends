package chronoMods.coop;

import chronoMods.coop.courier.CoopCourier;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

// for downfall mod
public class CoopCourierRoom extends AbstractRoom {
    private chronoMods.coop.courier.CoopCourierRoom realCoopCourierRoom;

    public CoopCourierRoom() {
        this.realCoopCourierRoom = new chronoMods.coop.courier.CoopCourierRoom();
    }

    public void onPlayerEntry() {
        realCoopCourierRoom.onPlayerEntry();
    }

    public void update() {
        realCoopCourierRoom.update();
    }

    public void render(SpriteBatch sb) {
        realCoopCourierRoom.render(sb);
    }

    public void dispose() {
        realCoopCourierRoom.dispose();
    }
}
