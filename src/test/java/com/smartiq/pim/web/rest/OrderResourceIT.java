package com.smartiq.pim.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smartiq.pim.IntegrationTest;
import com.smartiq.pim.domain.Address;
import com.smartiq.pim.domain.Basket;
import com.smartiq.pim.domain.Order;
import com.smartiq.pim.domain.User;
import com.smartiq.pim.domain.enumeration.BasketStatus;
import com.smartiq.pim.domain.enumeration.OrderStatus;
import com.smartiq.pim.repository.AddressRepository;
import com.smartiq.pim.repository.BasketRepository;
import com.smartiq.pim.repository.OrderRepository;
import com.smartiq.pim.repository.UserRepository;
import com.smartiq.pim.security.SecurityUtils;
import com.smartiq.pim.service.OmIntegrationService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderResourceIT {

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.NEW;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.COMPLETED;

    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    BasketRepository basketRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @MockBean
    OmIntegrationService omIntegrationService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order().createDate(DEFAULT_CREATE_DATE).status(DEFAULT_STATUS);
        return order;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order().createDate(UPDATED_CREATE_DATE).status(UPDATED_STATUS);
        return order;
    }

    @BeforeEach
    public void initTest() throws IOException {
        order = createEntity(em);
        MockitoAnnotations.openMocks(this);
        doNothing().when(omIntegrationService).createOrder(any(Order.class), anyString());
        doNothing().when(omIntegrationService).cancelOrder(anyLong(), anyString());
    }

    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        Basket basket = new Basket();
        basket.setStatus(BasketStatus.ACTIVE);
        basket.setCreateDate(LocalDate.now());
        basket.setTotalCost(Double.valueOf(100));
        basket.setUser(user);
        basketRepository.save(basket);
        order.setBasket(basket);

        Address address = new Address();
        address.setCity("city");
        address.setDistrict("district");
        address.setDetails("ENTITY_API_URL");
        address.setName("ENTITY_API_URL");
        address.setUser(user);
        addressRepository.save(address);
        order.setAddress(address);
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();

        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(testOrder.getBasket().getId()).isEqualTo(basket.getId());
        assertThat(testOrder.getAddress().getId()).isEqualTo(address.getId());
        assertThat(testOrder.getUser().getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @Transactional
    void createOrderWithExistingId() throws Exception {
        // Create the Order with an existing ID
        order.setId(1L);

        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setCreateDate(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setStatus(null);

        // Create the Order, which fails.

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();
        order.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(order)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void cancelOrder() throws Exception {
        // Initialize the database
        order.setStatus(OrderStatus.NEW);
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/cancel/" + order.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();

        assertThat(orderList.get(0).getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @Transactional
    void updateAddress() throws Exception {
        // Initialize the database

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();

        Address address = new Address();
        address.setCity("city");
        address.setDistrict("district");
        address.setDetails("ENTITY_API_URL");
        address.setName("ENTITY_API_URL");
        address.setUser(user);
        addressRepository.save(address);
        order.setAddress(address);
        Address address2 = new Address();

        address2.setCity("city");
        address2.setDistrict("district");
        address2.setDetails("ENTITY_API_URL");
        address2.setName("ENTITY_API_URL");
        address2.setUser(user);
        addressRepository.save(address2);
        order.setUser(user);

        orderRepository.saveAndFlush(order);
        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL + "/updateAddress/" + order.getId() + "/" + address2.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();

        assertThat(orderList.get(0).getAddress().getId()).isEqualTo(address2.getId());
    }
}
