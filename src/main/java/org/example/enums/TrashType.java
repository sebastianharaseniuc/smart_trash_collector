package org.example.enums;

public enum TrashType {
    PLASTIC,
    GLASS,
    PAPER,
    TOXIC;
    public enum PlasticTrashItem {
        WATER_BOTTLE(100),
        PLASTIC_CUP(33.7),
        STRAW(20.8),
        BAG(55.3),
        CUTLERY(70.4),
        BUCKET(400),
        USED_VACCINE(10.9);
        private final double weight;
        PlasticTrashItem(double weight) {
            this.weight = weight;
        }
        public double getWeight() {
            return weight;
        }
    }
    public enum GlassTrashItem {
        WINE_BOTTLE(150),
        BEER_BOTTLE(100),
        JAR(40.5),
        WINDOW(400),
        CHAMPAGNE_GLASS(23.8);
        private final double weight;
        GlassTrashItem(double weight) {
            this.weight = weight;
        }
        public double getWeight() {
            return weight;
        }
    }
    public enum PaperTrashItem {
        NEWSPAPER(30.9),
        BOX(400),
        MILK_BOX(100),
        PIZZA_BOX(55.6),
        A4_PAPER(5.4);
        private final double weight;
        PaperTrashItem(double weight) {
            this.weight = weight;
        }
        public double getWeight() {
            return weight;
        }
    }
}
