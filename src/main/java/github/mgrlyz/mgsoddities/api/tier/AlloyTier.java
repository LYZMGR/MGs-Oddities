package github.mgrlyz.mgsoddities.api.tier;

public enum AlloyTier implements ITier {
    PRIMORDIAL("primordial", BaseTier.PARAGON),
    AETHER("aether",BaseTier.APOTHEOSIS);

    private final BaseTier baseTier;
    private final String name;

    AlloyTier(String name, BaseTier base) {
        baseTier = base;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public BaseTier getBaseTier() { return baseTier;}
}