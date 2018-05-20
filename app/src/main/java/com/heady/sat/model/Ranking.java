package com.heady.sat.model;

import java.util.List;

public class Ranking {

    private String ranking;
    private List<ProductRank> products = null;

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public List<ProductRank> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRank> products) {
        this.products = products;
    }

}
