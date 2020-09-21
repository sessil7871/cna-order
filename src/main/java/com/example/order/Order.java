package com.example.order;


import javax.persistence.*;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Entity
@Table(name = "ORDER_TABLE")
public class Order {
    @Id @GeneratedValue
    Long id;
    Long productId;
    String productName;
    int qty;
    String orderStatus = "orderPlaced";

    public void  onPostPersist(){

        //OrderPlaced orderPlaced = new OrderPlaced();
        OrderPlaced orderPlaced = new OrderPlaced();
        //orderPlaced.setOrderId(this.getId());
        orderPlaced.setProductId(this.getProductId());
        orderPlaced.setQty(this.getQty());
        orderPlaced.setProductName(this.getProductName());

        ObjectMapper objectMapper = new ObjectMapper();
            String json = null;
            try {
        	        json = objectMapper.writeValueAsString(orderPlaced);
         	    } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        System.out.println("order"+ json);

        Processor processor = OrderApplication.applicationContext.getBean(Processor.class);
        MessageChannel outputChannel = processor.output();
            outputChannel.send(MessageBuilder
           .withPayload(json)
          .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
	            .build());


    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
