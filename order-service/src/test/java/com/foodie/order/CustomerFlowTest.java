package com.foodie.order;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class CustomerFlowTest {
    @Test void orderCustomerComesFromSessionAndRejectsOtherOwners() {
        var service=mock(OrderManager.class);var auth=mock(CustomerAuthorization.class);
        when(auth.authenticate("Bearer customer")).thenReturn(7L);
        var controller=new OrderController(service,auth);
        var lines=List.of(new OrderManager.LineRequest(1L,2));
        controller.create("Bearer customer",new OrderManager.CreateRequest(null,1L,lines));
        verify(service).create(new OrderManager.CreateRequest(7L,1L,lines));
        assertEquals(403,assertThrows(ResponseStatusException.class,()->controller.create("Bearer customer",new OrderManager.CreateRequest(6L,1L,lines))).getStatusCode().value());
        when(service.get(1L)).thenReturn(new OrderManager.OrderView(1L,6L,1L,null,null,null,List.of()));
        assertThrows(ResponseStatusException.class,()->controller.get("Bearer customer",1L));
    }
    @Test void emptyCartCannotCheckoutOrBeDeleted() {
        var carts=mock(CartRepository.class);
        when(carts.findByCustomerId(7L)).thenReturn(List.of());
        var service=new OrderManager(mock(OrderRepository.class),carts,mock(RemoteClients.class));
        assertEquals(400,assertThrows(ResponseStatusException.class,()->service.checkout(7L)).getStatusCode().value());
        verify(carts,never()).deleteByCustomerId(any());
    }
}
