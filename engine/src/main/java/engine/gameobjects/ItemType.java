package engine.gameobjects;

import java.util.function.Consumer;

public enum ItemType {
    SWORD("sword.png", 1, false, (user) -> {
        System.out.println("sword used");
    }),
    //
    KEY("key.png", 1, false, (user) -> {
        System.out.println("key used");
    }),
    //
    FOOD("food.jpg", 10, true, (user) -> {
        user.heal(1);
    });

    private final String spritePath;
    private final int stackSize;
    private final boolean isUsableOnce;
    private final Consumer<Entity> use;

    ItemType(String spritePath, int stackSize, boolean isUsableOnce, Consumer<Entity> use) {
        this.spritePath = spritePath;
        this.stackSize = stackSize;
        this.isUsableOnce = isUsableOnce;
        this.use = use;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public int getStackSize() {
        return stackSize;
    }

    public boolean use(Entity user) {
        use.accept(user);
        return isUsableOnce;
    }
}
