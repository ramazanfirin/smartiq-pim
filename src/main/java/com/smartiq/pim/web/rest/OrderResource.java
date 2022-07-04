package com.smartiq.pim.web.rest;

import com.smartiq.pim.domain.Address;
import com.smartiq.pim.domain.Basket;
import com.smartiq.pim.domain.Order;
import com.smartiq.pim.domain.User;
import com.smartiq.pim.domain.enumeration.OrderStatus;
import com.smartiq.pim.repository.AddressRepository;
import com.smartiq.pim.repository.BasketRepository;
import com.smartiq.pim.repository.OrderRepository;
import com.smartiq.pim.repository.UserRepository;
import com.smartiq.pim.security.SecurityUtils;
import com.smartiq.pim.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.smartiq.pim.domain.Order}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "order";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final BasketRepository basketRepository;

    private final AddressRepository addressRepository;

    public OrderResource(
        OrderRepository orderRepository,
        UserRepository userRepository,
        BasketRepository basketRepository,
        AddressRepository addressRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.basketRepository = basketRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param order the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (order.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if (order.getBasket() != null && order.getBasket().getId() == null) {
            throw new BadRequestAlertException("Invalid ID", "Basket", "idinvalid");
        }

        if (order.getAddress() != null && order.getAddress().getId() == null) {
            throw new BadRequestAlertException("Invalid ID", "Address", "idinvalid");
        }

        Basket basket = basketRepository.findById(order.getBasket().getId()).get();
        if (!basket.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get())) {
            throw new RuntimeException("No access to Basket");
        }

        Address address = addressRepository.findById(order.getAddress().getId()).get();
        if (!address.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get())) {
            throw new RuntimeException("No access to Address");
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setCreateDate(LocalDate.now());
        order.setBasket(basket);
        order.setAddress(address);
        Order result = orderRepository.save(order);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PutMapping("/orders/{id}")
    //    public ResponseEntity<Order> updateOrder(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Order order)
    //        throws URISyntaxException {
    //        log.debug("REST request to update Order : {}, {}", id, order);
    //        if (order.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, order.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!orderRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        Order result = orderRepository.save(order);
    //        return ResponseEntity
    //            .ok()
    //            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString()))
    //            .body(result);
    //    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 404 (Not Found)} if the order is not found,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    //    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    //    public ResponseEntity<Order> partialUpdateOrder(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @NotNull @RequestBody Order order
    //    ) throws URISyntaxException {
    //        log.debug("REST request to partial update Order partially : {}, {}", id, order);
    //        if (order.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, order.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!orderRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        Optional<Order> result = orderRepository
    //            .findById(order.getId())
    //            .map(existingOrder -> {
    //                if (order.getCreateDate() != null) {
    //                    existingOrder.setCreateDate(order.getCreateDate());
    //                }
    //                if (order.getStatus() != null) {
    //                    existingOrder.setStatus(order.getStatus());
    //                }
    //
    //                return existingOrder;
    //            })
    //            .map(orderRepository::save);
    //
    //        return ResponseUtil.wrapOrNotFound(
    //            result,
    //            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString())
    //        );
    //    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        log.debug("REST request to get all Orders");
        return orderRepository.findAll();
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Optional<Order> order = orderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(order);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the order to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.debug("REST request to delete Order : {}", id);
        orderRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/orders/updateAddress/{orderId}/{addressId}")
    public Order updateAddress(@PathVariable Long orderId, @PathVariable Long addressId) {
        Order order = orderRepository.findById(orderId).get();
        if (!order.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get())) {
            throw new RuntimeException("No access to Order");
        }

        Address address = addressRepository.findById(addressId).get();
        if (!address.getUser().getLogin().equals(SecurityUtils.getCurrentUserLogin().get())) {
            throw new RuntimeException("No access to Address");
        }

        order.setAddress(address);
        orderRepository.save(order);
        return order;
    }

    @GetMapping("/orders/cancel/{orderId}")
    public Order cancel(@PathVariable Long orderId) {
        Order order = orderRepository.getById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return order;
    }
}
