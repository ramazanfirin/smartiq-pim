package com.smartiq.pim.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BasketItem.
 */
@Entity
@Table(name = "basket_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BasketItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "total_cost", nullable = false)
    private Integer totalCost;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "basketItems" }, allowSetters = true)
    private Basket basket;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category", "basketItems" }, allowSetters = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BasketItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public BasketItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTotalCost() {
        return this.totalCost;
    }

    public BasketItem totalCost(Integer totalCost) {
        this.setTotalCost(totalCost);
        return this;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Basket getBasket() {
        return this.basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    public BasketItem basket(Basket basket) {
        this.setBasket(basket);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BasketItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasketItem)) {
            return false;
        }
        return id != null && id.equals(((BasketItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BasketItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalCost=" + getTotalCost() +
            "}";
    }
}
