package com.smartiq.pim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.shared.Application;
import com.smartiq.pim.config.ApplicationProperties;
import com.smartiq.pim.domain.Order;
import com.smartiq.pim.domain.enumeration.OrderStatus;
import com.smartiq.pim.service.dto.CreateOrderManagementDTO;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.okhttp3.MediaType;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.shaded.okhttp3.RequestBody;
import org.testcontainers.shaded.okhttp3.Response;

@Service
@Transactional
public class OmIntegrationService {

    private final Logger log = LoggerFactory.getLogger(OmIntegrationService.class);

    private final ObjectMapper objectMapper;

    private final ApplicationProperties applicationProperties;

    public OmIntegrationService(ObjectMapper objectMapper, ApplicationProperties applicationProperties) {
        super();
        this.objectMapper = objectMapper;
        this.applicationProperties = applicationProperties;
    }

    public void createOrder(Order order, String authTokenHeader) throws IOException {
        OkHttpClient client = new OkHttpClient();

        CreateOrderManagementDTO createOrderManagementDTO = new CreateOrderManagementDTO();
        createOrderManagementDTO.setAddress(order.getAddress());
        createOrderManagementDTO.setOrderId(order.getId());
        createOrderManagementDTO.setStatus(OrderStatus.NEW);

        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"),
            objectMapper.writeValueAsString(createOrderManagementDTO)
        );

        Request request = new Request.Builder()
            .url(applicationProperties.getOrderManagementAppLink() + "/api/orders")
            .header("Authorization", authTokenHeader)
            .post(body)
            .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();
        log.info(result);
        if (!response.isSuccessful()) throw new RuntimeException("Om service call error");
    }

    public void cancelOrder(Long orderId, String authTokenHeader) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
            .url(applicationProperties.getOrderManagementAppLink() + "/api/orders/cancel/" + orderId)
            .header("Authorization", authTokenHeader)
            .build();

        Response response = client.newCall(request).execute();
        String result = response.body().string();
        log.info(result);
        if (!response.isSuccessful()) throw new RuntimeException("Om service call error");
    }
}
