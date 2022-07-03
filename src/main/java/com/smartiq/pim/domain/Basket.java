package com.smartiq.pim.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartiq.pim.domain.enumeration.BasketStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Basket.
 */
@Entity
@Table(name = "basket")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BasketStatus status;

    @NotNull
    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    @OneToMany(mappedBy = "basket", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "basket", "product" }, allowSetters = true)
    private Set<BasketItem> basketItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Basket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Basket createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public BasketStatus getStatus() {
        return this.status;
    }

    public Basket status(BasketStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BasketStatus status) {
        this.status = status;
    }

    public Double getTotalCost() {
        return this.totalCost;
    }

    public Basket totalCost(Double totalCost) {
        this.setTotalCost(totalCost);
        return this;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Set<BasketItem> getBasketItems() {
        return this.basketItems;
    }

    public void setBasketItems(Set<BasketItem> basketItems) {
        if (this.basketItems != null) {
            this.basketItems.forEach(i -> i.setBasket(null));
        }
        if (basketItems != null) {
            basketItems.forEach(i -> i.setBasket(this));
        }
        this.basketItems = basketItems;
    }

    public Basket basketItems(Set<BasketItem> basketItems) {
        this.setBasketItems(basketItems);
        return this;
    }

    public Basket addBasketItem(BasketItem basketItem) {
        this.basketItems.add(basketItem);
        basketItem.setBasket(this);
        return this;
    }

    public Basket removeBasketItem(BasketItem basketItem) {
        this.basketItems.remove(basketItem);
        basketItem.setBasket(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Basket)) {
            return false;
        }
        return id != null && id.equals(((Basket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Basket{" +
            "id=" + getId() +
            ", createDate='" + getCreateDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalCost=" + getTotalCost() +
            "}";
    }
}
