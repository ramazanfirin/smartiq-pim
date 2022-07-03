package com.smartiq.pim.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smartiq.pim.IntegrationTest;
import com.smartiq.pim.domain.Basket;
import com.smartiq.pim.domain.BasketItem;
import com.smartiq.pim.domain.Product;
import com.smartiq.pim.domain.User;
import com.smartiq.pim.domain.enumeration.BasketStatus;
import com.smartiq.pim.repository.BasketItemRepository;
import com.smartiq.pim.repository.BasketRepository;
import com.smartiq.pim.repository.ProductRepository;
import com.smartiq.pim.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BasketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BasketResourceIT {

    private static final LocalDate DEFAULT_CREATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BasketStatus DEFAULT_STATUS = BasketStatus.ACTIVE;
    private static final BasketStatus UPDATED_STATUS = BasketStatus.EXPIRED;

    private static final Double DEFAULT_TOTAL_COST = 1D;
    private static final Double UPDATED_TOTAL_COST = 2D;

    private static final String ENTITY_API_URL = "/api/baskets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BasketItemRepository basketItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBasketMockMvc;

    private Basket basket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Basket createEntity(EntityManager em) {
        Basket basket = new Basket().createDate(DEFAULT_CREATE_DATE).status(DEFAULT_STATUS).totalCost(DEFAULT_TOTAL_COST);
        return basket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Basket createUpdatedEntity(EntityManager em) {
        Basket basket = new Basket().createDate(UPDATED_CREATE_DATE).status(UPDATED_STATUS).totalCost(UPDATED_TOTAL_COST);
        return basket;
    }

    @BeforeEach
    public void initTest() {
        basket = createEntity(em);
    }

    @Test
    @Transactional
    void createBasket() throws Exception {
        int databaseSizeBeforeCreate = basketRepository.findAll().size();
        // Create the Basket
        restBasketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isCreated());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeCreate + 1);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testBasket.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBasket.getTotalCost()).isEqualTo(DEFAULT_TOTAL_COST);
    }

    @Test
    @Transactional
    void createBasketWithExistingId() throws Exception {
        // Create the Basket with an existing ID
        basket.setId(1L);

        int databaseSizeBeforeCreate = basketRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBasketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isBadRequest());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreateDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketRepository.findAll().size();
        // set the field null
        basket.setCreateDate(null);

        // Create the Basket, which fails.

        restBasketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isBadRequest());

        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketRepository.findAll().size();
        // set the field null
        basket.setStatus(null);

        // Create the Basket, which fails.

        restBasketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isBadRequest());

        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketRepository.findAll().size();
        // set the field null
        basket.setTotalCost(null);

        // Create the Basket, which fails.

        restBasketMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isBadRequest());

        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBaskets() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        // Get all the basketList
        restBasketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(basket.getId().intValue())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalCost").value(hasItem(DEFAULT_TOTAL_COST.doubleValue())));
    }

    @Test
    @Transactional
    void getBasket() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        // Get the basket
        restBasketMockMvc
            .perform(get(ENTITY_API_URL_ID, basket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(basket.getId().intValue()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalCost").value(DEFAULT_TOTAL_COST.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingBasket() throws Exception {
        // Get the basket
        restBasketMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBasket() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        int databaseSizeBeforeUpdate = basketRepository.findAll().size();

        // Update the basket
        Basket updatedBasket = basketRepository.findById(basket.getId()).get();
        // Disconnect from session so that the updates on updatedBasket are not directly saved in db
        em.detach(updatedBasket);
        updatedBasket.createDate(UPDATED_CREATE_DATE).status(UPDATED_STATUS).totalCost(UPDATED_TOTAL_COST);

        restBasketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBasket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBasket))
            )
            .andExpect(status().isOk());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testBasket.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBasket.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void putNonExistingBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, basket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(basket))
            )
            .andExpect(status().isBadRequest());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(basket))
            )
            .andExpect(status().isBadRequest());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBasketWithPatch() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        int databaseSizeBeforeUpdate = basketRepository.findAll().size();

        // Update the basket using partial update
        Basket partialUpdatedBasket = new Basket();
        partialUpdatedBasket.setId(basket.getId());

        partialUpdatedBasket.status(UPDATED_STATUS).totalCost(UPDATED_TOTAL_COST);

        restBasketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBasket.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBasket))
            )
            .andExpect(status().isOk());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testBasket.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBasket.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void fullUpdateBasketWithPatch() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        int databaseSizeBeforeUpdate = basketRepository.findAll().size();

        // Update the basket using partial update
        Basket partialUpdatedBasket = new Basket();
        partialUpdatedBasket.setId(basket.getId());

        partialUpdatedBasket.createDate(UPDATED_CREATE_DATE).status(UPDATED_STATUS).totalCost(UPDATED_TOTAL_COST);

        restBasketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBasket.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBasket))
            )
            .andExpect(status().isOk());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
        Basket testBasket = basketList.get(basketList.size() - 1);
        assertThat(testBasket.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testBasket.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBasket.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void patchNonExistingBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, basket.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(basket))
            )
            .andExpect(status().isBadRequest());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(basket))
            )
            .andExpect(status().isBadRequest());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBasket() throws Exception {
        int databaseSizeBeforeUpdate = basketRepository.findAll().size();
        basket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(basket)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Basket in the database
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBasket() throws Exception {
        // Initialize the database
        basketRepository.saveAndFlush(basket);

        int databaseSizeBeforeDelete = basketRepository.findAll().size();

        // Delete the basket
        restBasketMockMvc
            .perform(delete(ENTITY_API_URL_ID, basket.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void createOrGetActiveBasket() throws Exception {
        int databaseSizeBeforeDelete = basketRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isEqualTo(0);

        User user = new User();
        user.setLogin("DEFAULT_LOGIN");
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + "johndoe@localhost");
        user.setFirstName("DEFAULT_FIRSTNAME");
        user.setLastName("DEFAULT_LASTNAME");
        user.setImageUrl("DEFAULT_IMAGEURL");
        user.setLangKey("TR");
        userRepository.save(user);

        restBasketMockMvc
            .perform(
                get(ENTITY_API_URL + "/createOrGetActiveBasket")
                    .with(request -> {
                        request.setRemoteUser("DEFAULT_LOGIN");
                        return request;
                    })
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());

        // Validate the database contains one less item
        List<Basket> basketList = basketRepository.findAll();
        assertThat(basketList.size()).isEqualTo(1);
        assertThat(basketList.get(0).getStatus()).isEqualTo(BasketStatus.ACTIVE);
    }

    @Test
    @Transactional
    void addItem() throws Exception {
        int databaseSizeBeforeDelete = basketRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isEqualTo(0);

        Product product = new Product();
        product.setName("name");
        product.setPrice(Double.valueOf(100));
        product.setStock(100);
        productRepository.save(product);

        restBasketMockMvc
            .perform(
                post(ENTITY_API_URL + "/addItem/" + product.getId())
                    .with(request -> {
                        request.setRemoteUser("DEFAULT_LOGIN");
                        return request;
                    })
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
        // Validate the database contains one less item
        List<Basket> basketList = basketRepository.findAll();
        //basketRepository.getById(basketList.get(0).getId());
        assertThat(basketList.size()).isEqualTo(1);
        assertThat(basketList.get(0).getStatus()).isEqualTo(BasketStatus.ACTIVE);
        assertThat(basketList.get(0).getBasketItems().size()).isEqualTo(1);
        assertThat(basketList.get(0).getBasketItems().size()).isEqualTo(1);
    }

    @Test
    @Transactional
    void deleteItem() throws Exception {
        int databaseSizeBeforeDelete = basketRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isEqualTo(0);

        //        User user = new User();
        //        user.setLogin("user");
        //        user.setPassword(RandomStringUtils.random(60));
        //        user.setActivated(true);
        //        user.setEmail(RandomStringUtils.randomAlphabetic(5) + "johndoe@localhost");
        //        user.setFirstName("DEFAULT_FIRSTNAME");
        //        user.setLastName("DEFAULT_LASTNAME");
        //        user.setImageUrl("DEFAULT_IMAGEURL");
        //        user.setLangKey("TR");
        //        userRepository.save(user);

        User user = userRepository.findOneByLogin("user").get();

        Product product = new Product();
        product.setName("name");
        product.setPrice(Double.valueOf(100));
        product.setStock(100);
        productRepository.save(product);

        BasketItem basketItem = new BasketItem();
        basketItem.setProduct(product);
        basketItem.setBasket(basket);
        basketItem.setQuantity(1);
        basketItem.setTotalCost(basketItem.getProduct().getPrice().intValue());
        basketItemRepository.save(basketItem);
        basket.getBasketItems().add(basketItem);
        basket.setUser(user);
        basketRepository.save(basket);

        restBasketMockMvc
            .perform(
                get(ENTITY_API_URL + "/deleteItem/" + basketItem.getId())
                    .with(request -> {
                        request.setRemoteUser("user");
                        return request;
                    })
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
        // Validate the database contains one less item
        List<Basket> basketList = basketRepository.findAll();
        //basketRepository.getById(basketList.get(0).getId());
        assertThat(basketList.size()).isEqualTo(1);
        assertThat(basketList.get(0).getStatus()).isEqualTo(BasketStatus.ACTIVE);
        assertThat(basketList.get(0).getBasketItems().size()).isEqualTo(0);
    }
}
