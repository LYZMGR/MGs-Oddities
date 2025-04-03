package github.mgrlyz.mgsoddities.api.tier;

public enum MGsOdditiesAlloyTier implements IAdvanceTier {
    PRIMORDIAL("primordial", AdvanceTier.PARAGON),
    AETHER("aether", AdvanceTier.APOTHEOSIS);

    public final String name;
    public final AdvanceTier advanceTier;

    MGsOdditiesAlloyTier(String name, AdvanceTier tier) {
        this.advanceTier = tier;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public AdvanceTier getAdvanceTier() {
        return advanceTier;
    }
}