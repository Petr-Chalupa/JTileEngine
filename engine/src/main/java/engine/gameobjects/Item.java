package engine.gameobjects;

public class Item extends GameObject {
    private ItemType type;

    public Item(double posX, double posY, int layer, double size, ItemType type) {
        super(posX, posY, 1, size);
        this.type = type;

        setSprite(type.getSpritePath());
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public void update(double deltaTime) {
    }

    public void use(Entity user) {
        type.use(user);
    }
}
