package com.smartiq.pim.web.rest;

import com.smartiq.pim.domain.Basket;
import com.smartiq.pim.domain.BasketItem;
import com.smartiq.pim.domain.Product;
import com.smartiq.pim.domain.User;
import com.smartiq.pim.domain.enumeration.BasketStatus;
import com.smartiq.pim.repository.BasketItemRepository;
import com.smartiq.pim.repository.BasketRepository;
import com.smartiq.pim.repository.ProductRepository;
import com.smartiq.pim.repository.UserRepository;
import com.smartiq.pim.security.SecurityUtils;
import com.smartiq.pim.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.smartiq.pim.domain.Basket}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BasketResource {

    private final Logger log = LoggerFactory.getLogger(BasketResource.class);

    private static final String ENTITY_NAME = "basket";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BasketRepository basketRepository;

    private final UserRepository userRepository;

    private final BasketItemRepository basketItemRepository;

    private final ProductRepository productRepository;

    public BasketResource(
        BasketRepository basketRepository,
        UserRepository userRepository,
        BasketItemRepository basketItemRepository,
        ProductRepository productRepository
    ) {
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;
        this.basketItemRepository = basketItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * {@code POST  /baskets} : Create a new basket.
     *
     * @param basket the basket to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new basket, or with status {@code 400 (Bad Request)} if the basket has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/baskets")
    public ResponseEntity<Basket> createBasket(@Valid @RequestBody Basket basket) throws URISyntaxException {
        log.debug("REST request to save Basket : {}", basket);
        if (basket.getId() != null) {
            throw new BadRequestAlertException("A new basket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Basket result = basketRepository.save(basket);
        return ResponseEntity
            .created(new URI("/api/baskets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /baskets/:id} : Updates an existing basket.
     *
     * @param id the id of the basket to save.
     * @param basket the basket to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basket,
     * or with status {@code 400 (Bad Request)} if the basket is not valid,
     * or with status {@code 500 (Internal Server Error)} if the basket couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/baskets/{id}")
    public ResponseEntity<Basket> updateBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Basket basket
    ) throws URISyntaxException {
        log.debug("REST request to update Basket : {}, {}", id, basket);
        if (basket.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basket.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!basketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Basket result = basketRepository.save(basket);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, basket.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /baskets/:id} : Partial updates given fields of an existing basket, field will ignore if it is null
     *
     * @param id the id of the basket to save.
     * @param basket the basket to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basket,
     * or with status {@code 400 (Bad Request)} if the basket is not valid,
     * or with status {@code 404 (Not Found)} if the basket is not found,
     * or with status {@code 500 (Internal Server Error)} if the basket couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/baskets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Basket> partialUpdateBasket(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Basket basket
    ) throws URISyntaxException {
        log.debug("REST request to partial update Basket partially : {}, {}", id, basket);
        if (basket.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basket.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!basketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Basket> result = basketRepository
            .findById(basket.getId())
            .map(existingBasket -> {
                if (basket.getCreateDate() != null) {
                    existingBasket.setCreateDate(basket.getCreateDate());
                }
                if (basket.getStatus() != null) {
                    existingBasket.setStatus(basket.getStatus());
                }
                if (basket.getTotalCost() != null) {
                    existingBasket.setTotalCost(basket.getTotalCost());
                }

                return existingBasket;
            })
            .map(basketRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, basket.getId().toString())
        );
    }

    /**
     * {@code GET  /baskets} : get all the baskets.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of baskets in body.
     */
    @GetMapping("/baskets")
    public ResponseEntity<List<Basket>> getAllBaskets(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Baskets");
        Page<Basket> page = basketRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /baskets/:id} : get the "id" basket.
     *
     * @param id the id of the basket to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the basket, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/baskets/{id}")
    public ResponseEntity<Basket> getBasket(@PathVariable Long id) {
        log.debug("REST request to get Basket : {}", id);
        Optional<Basket> basket = basketRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(basket);
    }

    /**
     * {@code DELETE  /baskets/:id} : delete the "id" basket.
     *
     * @param id the id of the basket to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/baskets/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable Long id) {
        log.debug("REST request to delete Basket : {}", id);
        basketRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/baskets/createOrGetActiveBasket")
    public ResponseEntity<Basket> createOrGetActiveBasket() {
        return ResponseEntity.ok().body(getCurrentBasket());
    }

    @PostMapping("/baskets/addItem/{productId}")
    public ResponseEntity<Basket> addItem(@PathVariable Long productId) {
        Basket basket = getCurrentBasket();

        BasketItem basketItem = new BasketItem();
        basketItem.setProduct(productRepository.getById(productId));
        basketItem.setBasket(basket);
        basketItem.setQuantity(1);
        basketItem.setTotalCost(basketItem.getProduct().getPrice().intValue());
        basketItemRepository.save(basketItem);
        basket.getBasketItems().add(basketItem);

        basket.setTotalCost(calculateTotalBasketCost(basket));
        basket = basketRepository.save(basket);

        return ResponseEntity.ok().body(basket);
    }

    @GetMapping("/baskets/deleteItem/{basketItemId}")
    public ResponseEntity<Basket> deleteItem(@PathVariable Long basketItemId, HttpServletRequest httpServletRequest) {
        Basket basket = getCurrentBasket();
        for (BasketItem basketItem : basket.getBasketItems()) {
            if (basketItem.getId().longValue() == basketItemId) {
                basket.getBasketItems().remove(basketItem);
                break;
            }
        }

        basket = basketRepository.save(basket);

        return ResponseEntity.ok().body(basket);
    }

    private Basket getCurrentBasket() {
        List<Basket> baskets = basketRepository.findActiveBasketOfCurrentUser();
        Basket currentBasket = null;
        SecurityUtils.getCurrentUserLogin().get();
        if (baskets.size() > 0) currentBasket = baskets.get(0); else {
            Basket basket = new Basket();
            basket.setCreateDate(LocalDate.now());
            basket.setStatus(BasketStatus.ACTIVE);
            basket.setTotalCost(0d);
            Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get());
            basket.setUser(user.get());
            currentBasket = basketRepository.save(basket);
        }
        return currentBasket;
    }

    private Double calculateTotalBasketCost(Basket basket) {
        Double total = Double.valueOf(0);
        for (BasketItem basketItem : basket.getBasketItems()) {
            total = total + basketItem.getProduct().getPrice();
        }
        return total;
    }
}
