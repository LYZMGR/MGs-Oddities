package github.mgrlyz.mgsoddities.api.tier;

public enum AlloyAdvanceTier implements IAdvanceTier {
    PRIMORDIAL("Primordial", AdvanceTier.PARAGON),
    AETHER("Aether", AdvanceTier.APOTHEOSIS);
    public final String name;
    public final AdvanceTier advanceTier;

    private AlloyAdvanceTier(String name, AdvanceTier tier) {
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