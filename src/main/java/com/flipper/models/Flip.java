package com.flipper.models;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

/**
 * Represents a buy and sell (flip) of an item
 */
@Data
public class Flip {
    public UUID id;
    public UUID userId;
    public UUID buyId;
    public UUID sellId;
    public int itemId;
    public int quantity;
    public int buyPrice;
    public int sellPrice;
    private Timestamp updatedAt;
    private Timestamp createdAt;

    public Flip() {}

    public Flip(Transaction buy, Transaction sell) {
        this.buyId = buy.id;
        this.sellId = sell.id;
        this.itemId = sell.getItemId();
        this.quantity = sell.getQuantity(); 
        this.buyPrice = buy.getPricePer();
        this.sellPrice = sell.getPricePer();

        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * We know a flip is a margin check when only 1 is bought and it's bought for a
     * greater to or equal price than sold for
     */
    public boolean isMarginCheck() {
        return quantity == 1 && buyPrice >= sellPrice;
    }

    public String describeFlip(String itemName) {
        return String.valueOf(quantity) + " " + itemName + "(s)";
    }

    /**
     * We only concern ourselves with the amount sold (ignore extra bought and kept)
     * 
     * @return profit of flip
     */
    public int getTotalProfit() {
        return (sellPrice - buyPrice) * quantity;
    }
}
