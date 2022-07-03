package com.smartiq.pim.web.rest;

import com.smartiq.pim.domain.BasketItem;
import com.smartiq.pim.repository.BasketItemRepository;
import com.smartiq.pim.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.smartiq.pim.domain.BasketItem}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BasketItemResource {

    private final Logger log = LoggerFactory.getLogger(BasketItemResource.class);

    private static final String ENTITY_NAME = "basketItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BasketItemRepository basketItemRepository;

    public BasketItemResource(BasketItemRepository basketItemRepository) {
        this.basketItemRepository = basketItemRepository;
    }

    /**
     * {@code POST  /basket-items} : Create a new basketItem.
     *
     * @param basketItem the basketItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new basketItem, or with status {@code 400 (Bad Request)} if the basketItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/basket-items")
    public ResponseEntity<BasketItem> createBasketItem(@Valid @RequestBody BasketItem basketItem) throws URISyntaxException {
        log.debug("REST request to save BasketItem : {}", basketItem);
        if (basketItem.getId() != null) {
            throw new BadRequestAlertException("A new basketItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BasketItem result = basketItemRepository.save(basketItem);
        return ResponseEntity
            .created(new URI("/api/basket-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /basket-items/:id} : Updates an existing basketItem.
     *
     * @param id the id of the basketItem to save.
     * @param basketItem the basketItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketItem,
     * or with status {@code 400 (Bad Request)} if the basketItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the basketItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/basket-items/{id}")
    public ResponseEntity<BasketItem> updateBasketItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BasketItem basketItem
    ) throws URISyntaxException {
        log.debug("REST request to update BasketItem : {}, {}", id, basketItem);
        if (basketItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!basketItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BasketItem result = basketItemRepository.save(basketItem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, basketItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /basket-items/:id} : Partial updates given fields of an existing basketItem, field will ignore if it is null
     *
     * @param id the id of the basketItem to save.
     * @param basketItem the basketItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated basketItem,
     * or with status {@code 400 (Bad Request)} if the basketItem is not valid,
     * or with status {@code 404 (Not Found)} if the basketItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the basketItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/basket-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BasketItem> partialUpdateBasketItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BasketItem basketItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update BasketItem partially : {}, {}", id, basketItem);
        if (basketItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, basketItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!basketItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BasketItem> result = basketItemRepository
            .findById(basketItem.getId())
            .map(existingBasketItem -> {
                if (basketItem.getQuantity() != null) {
                    existingBasketItem.setQuantity(basketItem.getQuantity());
                }
                if (basketItem.getTotalCost() != null) {
                    existingBasketItem.setTotalCost(basketItem.getTotalCost());
                }

                return existingBasketItem;
            })
            .map(basketItemRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, basketItem.getId().toString())
        );
    }

    /**
     * {@code GET  /basket-items} : get all the basketItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of basketItems in body.
     */
    @GetMapping("/basket-items")
    public ResponseEntity<List<BasketItem>> getAllBasketItems(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of BasketItems");
        Page<BasketItem> page = basketItemRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /basket-items/:id} : get the "id" basketItem.
     *
     * @param id the id of the basketItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the basketItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/basket-items/{id}")
    public ResponseEntity<BasketItem> getBasketItem(@PathVariable Long id) {
        log.debug("REST request to get BasketItem : {}", id);
        Optional<BasketItem> basketItem = basketItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(basketItem);
    }

    /**
     * {@code DELETE  /basket-items/:id} : delete the "id" basketItem.
     *
     * @param id the id of the basketItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/basket-items/{id}")
    public ResponseEntity<Void> deleteBasketItem(@PathVariable Long id) {
        log.debug("REST request to delete BasketItem : {}", id);
        basketItemRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
