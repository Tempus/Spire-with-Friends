package chronoMods.coop.relics;

// public class Dimensioneel extends AbstractBlight {
//     public static final String ID = "Dimensioneel";
//     private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
//     public static final String NAME = blightStrings.NAME;
//     public static final String[] DESCRIPTIONS = blightStrings.DESCRIPTION;

//     public static String relicID;
//     public static RemotePlayer sendPlayer;

//     private boolean relicSelected = true;
//     private RelicSelectScreen relicSelectScreen;
//     private boolean screenOpen = false;

//     public Dimensioneel() {
//         super(ID, NAME, "", "spear.png", true);
//         this.blightID = ID;
//         this.name = NAME;
//         updateDescription();
//         this.unique = true;
//         this.img = ImageMaster.loadImage("chrono/images/blights/" + ID + ".png");
//         this.outlineImg = ImageMaster.loadImage("chrono/images/blights/outline/" + ID + ".png");
//         this.increment = 0;
//         this.tips.clear();
//         this.tips.add(new PowerTip(name, description));
//     }

//     // @Override
//     // public void onEquip() {
//     //     ArrayList<String> ids = new ArrayList();
//     //     for (AbstractRelic r : AbstractDungeon.player.relics)
//     //         if (r.tier != AbstractRelic.RelicTier.BOSS && r.tier != AbstractRelic.RelicTier.STARTER)
//     //             ids.add(r.relicId);

//     //     for (String rid : ids) {
//     //         relicID = rid;
//     //         sendPlayer = TogetherManager.players.get(AbstractDungeon.miscRng.random(0,TogetherManager.players.size()-1));
//     //         NetworkHelper.sendData(NetworkHelper.dataType.SendRelic);
//     //     }
//     // }

//     @Override
//     public void updateDescription() {
//         this.description = this.DESCRIPTIONS[0];
//     }

//     @Override
//     public void onEquip()
//     {
//         if (AbstractDungeon.isScreenUp) {
//             AbstractDungeon.dynamicBanner.hide();
//             AbstractDungeon.overlayMenu.cancelButton.hide();
//             AbstractDungeon.previousScreen = AbstractDungeon.screen;
//         }
//         AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;

//         openRelicSelect();
//     }

//     private void openRelicSelect()
//     {
//         relicSelected = false;

//         ArrayList<AbstractRelic> relics = new ArrayList();
//         for (AbstractRelic r : AbstractDungeon.player.relics) {
//             AbstractRelic rcopy = r.makeCopy();
//             rcopy.isSeen = true;
//             relics.add(rcopy);
//         }

//         relics.removeIf(r -> r.tier == AbstractRelic.RelicTier.BOSS);

//         relicSelectScreen = new RelicSelectScreen();
//         // relicSelectScreen.selectCount = Math.min(TogetherManager.players.size()-1, relics.size());
//         relicSelectScreen.selectCount = Math.min(3, relics.size());
//         relicSelectScreen.open(relics);

//         screenOpen = true;
//     }

//     public String getRandomRelicID(AbstractRelic.RelicTier tier) {
//         switch (tier) {
//           case STARTER:
//             return RelicLibrary.starterList.get(MathUtils.random(0,RelicLibrary.starterList.size()-1)).relicId;
//           case COMMON:
//             return RelicLibrary.commonList.get(MathUtils.random(0,RelicLibrary.commonList.size()-1)).relicId;
//           case UNCOMMON:
//             return RelicLibrary.uncommonList.get(MathUtils.random(0,RelicLibrary.uncommonList.size()-1)).relicId;
//           case RARE:
//             return RelicLibrary.rareList.get(MathUtils.random(0,RelicLibrary.rareList.size()-1)).relicId;
//           case SHOP:
//             return RelicLibrary.shopList.get(MathUtils.random(0,RelicLibrary.shopList.size()-1)).relicId;
//           case SPECIAL:
//             return RelicLibrary.specialList.get(MathUtils.random(0,RelicLibrary.specialList.size()-1)).relicId;
//           default:
//             return RelicLibrary.commonList.get(MathUtils.random(0,RelicLibrary.commonList.size()-1)).relicId;
//         }
//     }

//     @Override
//     public void update()
//     {
//         super.update();

//         if (!relicSelected) {
//             if (relicSelectScreen.selectCount == 0) {
//                 relicSelectScreen.close();
//                 screenOpen = false;
//             }

//             if (relicSelectScreen.doneSelecting()) {
//                 relicSelected = true;

//                 ArrayList<RemotePlayer> players = new ArrayList();
//                 players.addAll(TogetherManager.players);
//                 players.remove(TogetherManager.getCurrentUser());

//                 for (AbstractRelic r : relicSelectScreen.getSelectedRelics()) {
//                     sendPlayer = players.stream().min((x, y) -> x.relics - y.relics).get();
//                     sendPlayer.relics++;

//                     relicID = getRandomRelicID(r.tier);
//                     AbstractDungeon.player.loseRelic(r.relicId);
//                     NetworkHelper.sendData(NetworkHelper.dataType.SendRelic);
//                 }
                       
//                 AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
//                 screenOpen = false;
//             } else {
//                 relicSelectScreen.update();
//             }
//         }
//     }

//     @Override
//     public void renderTip(SpriteBatch sb)
//     {
//         if (screenOpen) {
//             TogetherManager.log("ScreenOpen");
//             relicSelectScreen.render(sb);
//         } else {
//             super.renderTip(sb);
//         }
//     }

//     @Override
//     public void renderInTopPanel(SpriteBatch sb)
//     {
//         super.renderInTopPanel(sb);

//         if (screenOpen) {
//             relicSelectScreen.render(sb);
//         }
//     }
// }