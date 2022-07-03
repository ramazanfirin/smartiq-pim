package com.smartiq.pim.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.smartiq.pim.IntegrationTest;
import com.smartiq.pim.domain.BasketItem;
import com.smartiq.pim.repository.BasketItemRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BasketItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BasketItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Integer DEFAULT_TOTAL_COST = 1;
    private static final Integer UPDATED_TOTAL_COST = 2;

    private static final String ENTITY_API_URL = "/api/basket-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBasketItemMockMvc;

    private BasketItem basketItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketItem createEntity(EntityManager em) {
        BasketItem basketItem = new BasketItem().quantity(DEFAULT_QUANTITY).totalCost(DEFAULT_TOTAL_COST);
        return basketItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BasketItem createUpdatedEntity(EntityManager em) {
        BasketItem basketItem = new BasketItem().quantity(UPDATED_QUANTITY).totalCost(UPDATED_TOTAL_COST);
        return basketItem;
    }

    @BeforeEach
    public void initTest() {
        basketItem = createEntity(em);
    }

    @Test
    @Transactional
    void createBasketItem() throws Exception {
        int databaseSizeBeforeCreate = basketItemRepository.findAll().size();
        // Create the BasketItem
        restBasketItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basketItem)))
            .andExpect(status().isCreated());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeCreate + 1);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testBasketItem.getTotalCost()).isEqualTo(DEFAULT_TOTAL_COST);
    }

    @Test
    @Transactional
    void createBasketItemWithExistingId() throws Exception {
        // Create the BasketItem with an existing ID
        basketItem.setId(1L);

        int databaseSizeBeforeCreate = basketItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBasketItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basketItem)))
            .andExpect(status().isBadRequest());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().size();
        // set the field null
        basketItem.setQuantity(null);

        // Create the BasketItem, which fails.

        restBasketItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basketItem)))
            .andExpect(status().isBadRequest());

        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = basketItemRepository.findAll().size();
        // set the field null
        basketItem.setTotalCost(null);

        // Create the BasketItem, which fails.

        restBasketItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basketItem)))
            .andExpect(status().isBadRequest());

        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBasketItems() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        // Get all the basketItemList
        restBasketItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(basketItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].totalCost").value(hasItem(DEFAULT_TOTAL_COST)));
    }

    @Test
    @Transactional
    void getBasketItem() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        // Get the basketItem
        restBasketItemMockMvc
            .perform(get(ENTITY_API_URL_ID, basketItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(basketItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.totalCost").value(DEFAULT_TOTAL_COST));
    }

    @Test
    @Transactional
    void getNonExistingBasketItem() throws Exception {
        // Get the basketItem
        restBasketItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBasketItem() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();

        // Update the basketItem
        BasketItem updatedBasketItem = basketItemRepository.findById(basketItem.getId()).get();
        // Disconnect from session so that the updates on updatedBasketItem are not directly saved in db
        em.detach(updatedBasketItem);
        updatedBasketItem.quantity(UPDATED_QUANTITY).totalCost(UPDATED_TOTAL_COST);

        restBasketItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBasketItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBasketItem))
            )
            .andExpect(status().isOk());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBasketItem.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void putNonExistingBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, basketItem.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(basketItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(basketItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(basketItem)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBasketItemWithPatch() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();

        // Update the basketItem using partial update
        BasketItem partialUpdatedBasketItem = new BasketItem();
        partialUpdatedBasketItem.setId(basketItem.getId());

        partialUpdatedBasketItem.quantity(UPDATED_QUANTITY);

        restBasketItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBasketItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketItem))
            )
            .andExpect(status().isOk());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBasketItem.getTotalCost()).isEqualTo(DEFAULT_TOTAL_COST);
    }

    @Test
    @Transactional
    void fullUpdateBasketItemWithPatch() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();

        // Update the basketItem using partial update
        BasketItem partialUpdatedBasketItem = new BasketItem();
        partialUpdatedBasketItem.setId(basketItem.getId());

        partialUpdatedBasketItem.quantity(UPDATED_QUANTITY).totalCost(UPDATED_TOTAL_COST);

        restBasketItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBasketItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBasketItem))
            )
            .andExpect(status().isOk());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
        BasketItem testBasketItem = basketItemList.get(basketItemList.size() - 1);
        assertThat(testBasketItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testBasketItem.getTotalCost()).isEqualTo(UPDATED_TOTAL_COST);
    }

    @Test
    @Transactional
    void patchNonExistingBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, basketItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(basketItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(basketItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBasketItem() throws Exception {
        int databaseSizeBeforeUpdate = basketItemRepository.findAll().size();
        basketItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBasketItemMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(basketItem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BasketItem in the database
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBasketItem() throws Exception {
        // Initialize the database
        basketItemRepository.saveAndFlush(basketItem);

        int databaseSizeBeforeDelete = basketItemRepository.findAll().size();

        // Delete the basketItem
        restBasketItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, basketItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BasketItem> basketItemList = basketItemRepository.findAll();
        assertThat(basketItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
