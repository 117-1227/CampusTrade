package com.campustrade.service;

import com.campustrade.common.BusinessException;
import com.campustrade.entity.Favorite;
import com.campustrade.entity.Product;
import com.campustrade.entity.User;
import com.campustrade.mapper.FavoriteMapper;
import com.campustrade.mapper.ProductMapper;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock private FavoriteMapper favoriteMapper;
    @Mock private ProductMapper productMapper;
    @Mock private UserMapper userMapper;
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteServiceImpl(favoriteMapper, productMapper, userMapper);
    }

    @Test
    void shouldAddFavorite() {
        var p = product(1L, "Java Book");
        when(productMapper.selectById(1L)).thenReturn(p);
        when(favoriteMapper.selectCount(any())).thenReturn(0L);

        favoriteService.toggle(1L, 1L);

        verify(favoriteMapper).insert(any(Favorite.class));
        verify(productMapper).updateById(p);
        assertEquals(1, p.getFavCount());
    }

    @Test
    void shouldRemoveExistingFavorite() {
        var p = product(1L, "Java Book");
        p.setFavCount(1);
        when(productMapper.selectById(1L)).thenReturn(p);
        when(favoriteMapper.selectCount(any())).thenReturn(1L);

        favoriteService.toggle(1L, 1L);

        verify(favoriteMapper).delete(any());
        verify(productMapper).updateById(p);
        assertEquals(0, p.getFavCount());
    }

    @Test
    void shouldNotFavoriteNonExistentProduct() {
        when(productMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class, () ->
            favoriteService.toggle(1L, 999L));
    }

    @Test
    void shouldListFavorites() {
        var fav = new Favorite();
        fav.setProductId(1L);

        var p = product(1L, "Java Book");

        when(favoriteMapper.selectList(any())).thenReturn(List.of(fav));
        when(productMapper.selectBatchIds(List.of(1L))).thenReturn(List.of(p));
        when(userMapper.selectBatchIds(any())).thenReturn(List.of());

        var list = favoriteService.listByUser(1L);

        assertEquals(1, list.size());
        assertEquals("Java Book", list.get(0).getTitle());
    }

    @Test
    void shouldReturnIsFavorited() {
        when(favoriteMapper.selectCount(any())).thenReturn(1L);
        assertTrue(favoriteService.isFavorited(1L, 1L));

        when(favoriteMapper.selectCount(any())).thenReturn(0L);
        assertFalse(favoriteService.isFavorited(2L, 1L));
    }

    private Product product(Long id, String title) {
        var p = new Product();
        p.setId(id);
        p.setTitle(title);
        p.setPrice(BigDecimal.TEN);
        p.setDescription("desc");
        p.setImages("[]");
        p.setFavCount(0);
        p.setSellerId(2L);
        return p;
    }
}
