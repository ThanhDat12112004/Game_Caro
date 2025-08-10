package com.example.carogame.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "piece_skins")
public class PieceSkin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(name = "x_symbol", nullable = false)
    private String xSymbol;

    @Column(name = "o_symbol", nullable = false)
    private String oSymbol;

    @Column(name = "x_color")
    private String xColor;

    @Column(name = "o_color")
    private String oColor;

    @Column(name = "x_background_color")
    private String xBackgroundColor;

    @Column(name = "o_background_color")
    private String oBackgroundColor;

    @Column(name = "css_class")
    private String cssClass;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "animation_class")
    private String animationClass;

    // Constructors
    public PieceSkin() {}

    public PieceSkin(String name, String displayName, String description, Long price, String xSymbol, String oSymbol) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.price = price;
        this.xSymbol = xSymbol;
        this.oSymbol = oSymbol;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getXSymbol() {
        return xSymbol;
    }

    public void setXSymbol(String xSymbol) {
        this.xSymbol = xSymbol;
    }

    public String getOSymbol() {
        return oSymbol;
    }

    public void setOSymbol(String oSymbol) {
        this.oSymbol = oSymbol;
    }

    public String getXColor() {
        return xColor;
    }

    public void setXColor(String xColor) {
        this.xColor = xColor;
    }

    public String getOColor() {
        return oColor;
    }

    public void setOColor(String oColor) {
        this.oColor = oColor;
    }

    public String getXBackgroundColor() {
        return xBackgroundColor;
    }

    public void setXBackgroundColor(String xBackgroundColor) {
        this.xBackgroundColor = xBackgroundColor;
    }

    public String getOBackgroundColor() {
        return oBackgroundColor;
    }

    public void setOBackgroundColor(String oBackgroundColor) {
        this.oBackgroundColor = oBackgroundColor;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getAnimationClass() {
        return animationClass;
    }

    public void setAnimationClass(String animationClass) {
        this.animationClass = animationClass;
    }
}
