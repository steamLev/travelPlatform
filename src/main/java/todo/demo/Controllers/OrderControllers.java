package todo.demo.Controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import todo.demo.Models.OrderTask;
import todo.demo.Services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@RestController
public class OrderControllers {

ExecutorService service=   Executors.newFixedThreadPool(3);
@Autowired
TravelService travelService;

@PostMapping(value = "order/create/{from}/{to}/{data}/{transport}/{clientId}")
public void createTask(@PathVariable("from") String from,
                       @PathVariable("to") String to,
                       @PathVariable("data") String data,
                       @PathVariable("transport") Integer transport,
                       @PathVariable("clientId") Integer clientId){
Boolean result;
//создаем задание
    try{
        Callable<Boolean> callableTask = () -> {return  travelService.createOrder(new OrderTask(from,to, data,transport,clientId));};
        result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);

    }
    catch (Exception e){log.info("deleteTask error");e.printStackTrace();}

}

@GetMapping(value = "order/get/all")
public List<?> getTask(){
    List<OrderTask> list=new ArrayList<>();
    //выгружаем все  задания в многопоточном режиме
    try{
        Callable<List<OrderTask>> callableTask = () -> (List<OrderTask>) travelService.getOrderTask( new OrderTask());
           list=service.submit(callableTask).get(3000, TimeUnit.SECONDS);
    }
    catch (Exception e){log.info("getTask error");
         e.printStackTrace();}

    return list;
}

    @GetMapping(value = "order/get/{id}")
    public  OrderTask getTaskId(@PathVariable("id") Long id){
        OrderTask orderTask=new OrderTask();
        orderTask.setId(id);
        //выгружаем    задание по id
        try{
            OrderTask finalOrderTask = orderTask;
            Callable<OrderTask> callableTask = () -> ( OrderTask) travelService.getOrderTask(finalOrderTask);
            orderTask=service.submit(callableTask).get(3000, TimeUnit.SECONDS);
        }
        catch (Exception e){log.info("getTaskId error");e.printStackTrace();}
        return orderTask;
    }

@DeleteMapping(value = "order/delete/{id}")
public void deleteTask(@PathVariable("id") Long id){
    OrderTask orderTask=new OrderTask();
    Boolean result;
    //удаляем    задание по id
    try{
        orderTask.setId(id);
        Callable<Boolean> callableTask = () -> travelService.deleteOrder(orderTask);
        result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);
    }
    catch (Exception e){log.info("deleteTask error");e.printStackTrace();}
}

@PutMapping(value = "order/put/{id}/{from}/{to}/{date}/{transport}")
public void putTask(@PathVariable("id") Long id,
                    @PathVariable("from") String from,
                    @PathVariable("to") String to,
                    @PathVariable("date") String date,
                    @PathVariable("transport") Integer transport){
    OrderTask orderTask=new OrderTask(id,from,to,date,transport);
    Boolean result;
    //вносим изменения в     задание по id
    try{
        Callable<Boolean> callableTask = () -> travelService.updateOrder(orderTask);
        result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);
    }
    catch (Exception e){log.info("putTask error");e.printStackTrace();}


}
}
