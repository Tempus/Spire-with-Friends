package chronoMods.coop.drawable;

import chronoMods.TogetherManager;
import chronoMods.network.NetworkHelper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import java.util.ArrayList;

public class MapCanvas implements Disposable {

	public final Color clearColor = new Color(0.0F, 0.0F, 0.0F, 0.0F);
	public Vector2 curr = null;
	public Vector2 last = null;

	public ArrayList<Vector2[]> pointQueue = new ArrayList();

	// public static Pixmap brush = new Pixmap(Gdx.files.internal("chrono/images/circle-16-2.png"));
	public float initialBrushSize = 4f;
	public float brushSize = 4f;

	public Color drawColour = Color.RED;

	public float stepVal = 16f;

	public Pixmap pixmap;
	public Texture texture;
	public boolean dirty;

	public boolean hidden = false;

	public MapCanvas(Pixmap pixmap) {
		this.pixmap = pixmap;
		this.pixmap.setColor(drawColour);

		// brush.setFilter(Pixmap.Filter.BiLinear);

		this.dirty = false;
		clear();
	}

	public void update() {
		if (InputHelper.isMouseDown_R) {
			curr = new Vector2(InputHelper.mX, InputHelper.mY);
			
			if (InputHelper.isMouseDown_R && last == null) {
				pointQueue.add(new Vector2[] {new Vector2(curr.x, curr.y), null});
				draw(curr, DungeonMapScreen.offsetY);

				last = curr; 
			} else if (InputHelper.isMouseDown_R) {
				pointQueue.add(new Vector2[] {new Vector2(curr.x, curr.y), new Vector2(last.x, last.y)});
				drawLerped(last, curr, DungeonMapScreen.offsetY);

				last = curr;
			}
		} else if (InputHelper.justReleasedClickRight) {
			curr = new Vector2(InputHelper.mX, InputHelper.mY);
			pointQueue.add(new Vector2[] {new Vector2(curr.x, curr.y), null});

			draw(curr, DungeonMapScreen.offsetY);	
			last = null;
		}

		if (pointQueue.size() > 0) 
			NetworkHelper.sendData(NetworkHelper.dataType.DrawMap);
	}

	public void render(SpriteBatch sb, float alpha) {
		// Don't show if we're told to not show
		if (hidden) { return; }

		//sb.setColor(this.baseMapColor);
		Color c = new Color(1.0f, 1.0f, 1.0f, alpha);
		sb.setColor(c);

		if (dirty) {
			if (texture == null){
				this.texture = new Texture(pixmap);
			}

			TogetherManager.log("Updating pixmap for " + this.drawColour);
			texture.draw(pixmap, 0, 0);
			dirty = false;
		}
		
		if (texture != null)
			sb.draw(this.texture, 0, DungeonMapScreen.offsetY);
	}

	public void clear() {
		pixmap.setColor(clearColor);
		pixmap.fill();
		pixmap.setColor(drawColour);
		dirty = true;
	}

	private void drawDot(Vector2 spot, float offset) {
		pixmap.fillCircle((int)spot.x, pixmap.getHeight() + (int)offset - (int)spot.y, (int)brushSize);
		// pixmap.drawPixmap(brush, 0, 0, 16, 16, (int) spot.x, Settings.HEIGHT-(int) spot.y, (int)brushSize, (int)brushSize);
	}

	public void draw(Vector2 spot, float offset) {
		drawDot(spot, offset);
		dirty = true;
	}

	public void drawLerped(Vector2 from, Vector2 to, float offset) {
		float dist = to.dst(from);
		float alphaStep = brushSize / (stepVal * dist);

		for (float a = 0; a < 1f; a += alphaStep) {
			Vector2 lerped = from.lerp(to, a);
			drawDot(lerped, offset);
		}

		drawDot(to, offset);
		dirty = true;
	}

	@Override
	public void dispose() {
		texture.dispose();
		pixmap.dispose();
	}
}
