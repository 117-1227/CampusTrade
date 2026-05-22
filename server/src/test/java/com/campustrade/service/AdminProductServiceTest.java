package com.campustrade.service;

import com.campustrade.common.BusinessException;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock private ProductMapper productMapper;
    @Mock private UserMapper userMapper;
    private AdminProductService service;

    @BeforeEach
    void setUp() {
        service = new AdminProductServiceImpl(productMapper, userMapper);
    }

    @Test
    void shouldListPendingProducts() {
        var p1 = product(1L, "A", "pending");
        var p3 = product(3L, "C", "pending");
        when(productMapper.selectList(any())).thenReturn(Arrays.asList(p1, p3));
        when(userMapper.selectBatchIds(any())).thenReturn(List.of());

        List<AdminProductService.ProductAuditVO> list = service.listPending(1, 20);

        assertEquals(2, list.size());
        verify(productMapper).selectList(any());
    }

    @Test
    void shouldApproveProduct() {
        var p = product(1L, "A", "pending");
        when(productMapper.selectById(1L)).thenReturn(p);

        service.audit(1L, true, null);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).updateById(captor.capture());
        assertEquals("approved", captor.getValue().getAuditStatus());
        assertNull(captor.getValue().getAuditReason());
    }

    @Test
    void shouldRejectProduct() {
        var p = product(1L, "A", "pending");
        when(productMapper.selectById(1L)).thenReturn(p);

        service.audit(1L, false, "图片不清晰");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).updateById(captor.capture());
        assertEquals("rejected", captor.getValue().getAuditStatus());
        assertEquals("图片不清晰", captor.getValue().getAuditReason());
    }

    @Test
    void shouldNotAuditNonPendingProduct() {
        var p = product(1L, "A", "approved");
        when(productMapper.selectById(1L)).thenReturn(p);

        assertThrows(BusinessException.class, () ->
            service.audit(1L, true, null));
        verify(productMapper, never()).updateById(any());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class, () ->
            service.audit(999L, true, null));
    }

    private Product product(Long id, String title, String auditStatus) {
        var p = new Product();
        p.setId(id);
        p.setTitle(title);
        p.setAuditStatus(auditStatus);
        return p;
    }
}
