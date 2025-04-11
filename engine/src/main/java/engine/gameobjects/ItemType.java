package engine.gameobjects;

@FunctionalInterface
interface ItemInteraction {
    boolean interact(GameObject user);
}

public enum ItemType {
    SWORD("sword.png", 1, (user) -> {
        System.out.println("sword used");
        return false;
    }),
    //
    KEY("key.png", 1, (user) -> {
        System.out.println("key used");
        return false;
    }),
    //
    FOOD("food.jpg", 10, (user) -> {
        System.out.println("food used");
        return false;
    });

    private final String spritePath;
    private final int stackSize;
    private final ItemInteraction interaction;

    ItemType(String spritePath, int stackSize, ItemInteraction interaction) {
        this.spritePath = spritePath;
        this.stackSize = stackSize;
        this.interaction = interaction;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public int getStackSize() {
        return stackSize;
    }

    public boolean use(GameObject user) {
        return interaction.interact(user);
    }
}
