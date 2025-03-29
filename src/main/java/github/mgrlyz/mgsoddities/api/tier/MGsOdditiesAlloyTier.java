package github.mgrlyz.mgsoddities.api.tier;

public enum MGsOdditiesAlloyTier implements IAdvanceTier {
    PRIMORDIAL("primordial", AdvanceTier.PARAGON),
    AETHER("aether", AdvanceTier.APOTHEOSIS);

    public final String name;
    public final AdvanceTier advanceTier;

    private MGsOdditiesAlloyTier(String name, AdvanceTier tier) {
        this.advanceTier = tier;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public AdvanceTier getAdvanceTier() {
        return this.advanceTier;
    }
}