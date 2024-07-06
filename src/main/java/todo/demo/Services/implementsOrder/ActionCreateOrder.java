package todo.demo.Services.implementsOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import todo.demo.Models.OrderTask;
import todo.demo.Repositories.OrderRepository;
import todo.demo.Services.Action;

import java.math.BigDecimal;

@Component(value = "create")
public class ActionCreateOrder implements Action {
    OrderTask order;
@Autowired
    OrderRepository orderRepository;

    @Override
    public Object act(Object object) {
        order=(OrderTask)object;
        order.setStatus(true);
        order.setAmount(new BigDecimal("233"));
        orderRepository.save(order);
        return  true;
    }

}
