package com.campustrade.service;

import com.campustrade.common.BusinessException;
import com.campustrade.entity.Order;
import com.campustrade.entity.Product;
import com.campustrade.mapper.OrderMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderMapper orderMapper;
    @Mock private ProductMapper productMapper;
    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderMapper, productMapper);
    }

    @Test
    void shouldCreateOrder() {
        var p = new Product(); p.setId(1L); p.setSellerId(2L); p.setStatus("active");
        when(productMapper.selectById(1L)).thenReturn(p);
        when(orderMapper.selectCount(any())).thenReturn(0L);

        service.create(3L, 1L);

        ArgumentCaptor<Product> pCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).updateById(pCaptor.capture());
        assertEquals("reserved", pCaptor.getValue().getStatus());

        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    void shouldNotCreateOrderForNonActiveProduct() {
        var p = new Product(); p.setId(1L); p.setStatus("sold");
        when(productMapper.selectById(1L)).thenReturn(p);

        assertThrows(BusinessException.class, () -> service.create(3L, 1L));
        verify(orderMapper, never()).insert(any());
    }

    @Test
    void shouldNotBuyOwnProduct() {
        var p = new Product(); p.setId(1L); p.setSellerId(1L); p.setStatus("active");
        when(productMapper.selectById(1L)).thenReturn(p);

        assertThrows(BusinessException.class, () -> service.create(1L, 1L));
    }

    @Test
    void shouldConfirmOrder() {
        var order = new Order(); order.setId(1L); order.setBuyerId(3L); order.setSellerId(2L);
        order.setProductId(1L); order.setStatus("pending");
        var p = new Product(); p.setId(1L); p.setStatus("reserved");
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(productMapper.selectById(1L)).thenReturn(p);

        service.updateStatus(1L, 2L, "completed"); // seller confirms

        assertEquals("completed", order.getStatus());
        assertEquals("sold", p.getStatus());
        verify(orderMapper).updateById(order);
        verify(productMapper).updateById(p);
    }

    @Test
    void shouldCancelOrderByBuyer() {
        var order = new Order(); order.setId(1L); order.setBuyerId(3L); order.setSellerId(2L);
        order.setProductId(1L); order.setStatus("pending");
        var p = new Product(); p.setId(1L); p.setStatus("reserved");
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(productMapper.selectById(1L)).thenReturn(p);

        service.updateStatus(1L, 3L, "cancelled"); // buyer cancels

        assertEquals("cancelled", order.getStatus());
        assertEquals("active", p.getStatus());
    }

    @Test
    void shouldNotConfirmByNonSeller() {
        var order = new Order(); order.setId(1L); order.setSellerId(2L);
        order.setStatus("pending");
        when(orderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class,
            () -> service.updateStatus(1L, 999L, "completed"));
    }

    @Test
    void shouldNotUpdateNonPendingOrder() {
        var order = new Order(); order.setId(1L); order.setStatus("completed");
        when(orderMapper.selectById(1L)).thenReturn(order);

        assertThrows(BusinessException.class,
            () -> service.updateStatus(1L, 2L, "cancelled"));
    }

    @Test
    void shouldListByBuyer() {
        when(orderMapper.selectList(any())).thenReturn(java.util.List.of());
        assertTrue(service.listByBuyer(1L).isEmpty());
    }

    @Test
    void shouldListBySeller() {
        when(orderMapper.selectList(any())).thenReturn(java.util.List.of());
        assertTrue(service.listBySeller(1L).isEmpty());
    }
}
