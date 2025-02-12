package todo.demo.Controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import todo.demo.Models.OrderTask;
import todo.demo.Services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Controller
public class OrderControllers {

ExecutorService service=   Executors.newFixedThreadPool(3);
@Autowired
TravelService travelService;

    @Value("${processType.create}")
    String create;
    @Value("${processType.process}")
    String processed;
    @Value("${processType.done}")
    String done;
    @GetMapping("/order")
    public String indexOrder(){

        return "index";
    }
@PostMapping(value = "order/create/{from}/{to}/{data}/{transport}/{clientId}")
public void createTask(@PathVariable("from") String from,
                       @PathVariable("to") String to,
                       @PathVariable("data") String data,
                       @PathVariable("transport") Integer transport,
                       @PathVariable("clientId") Integer clientId,
                       @PathVariable("amount") Integer amount){
Boolean result;
//создаем задание
    try{
        Callable<Boolean> callableTask = () -> {
            return  travelService.createOrder(new OrderTask(from,to, data,transport,clientId,new BigDecimal(amount)));};
        result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);

    }
    catch (Exception e){log.info("deleteTask error");e.printStackTrace();}

}

    @PostMapping(value = "order/create")
    public String createTaskIndex(@ModelAttribute OrderTask orderForm/*@RequestParam (value = "from",required = false) String from,
                                @RequestParam (value = "to",required = false) String to,
                                @RequestParam (value = "phone",required = false) String phone,
                                  @RequestParam (value = "name",required = false) String name */){
        Boolean result;
//создаем задание
        log.info("from"+orderForm.getFkFrom());
        log.info("to"+orderForm.getFkTo());
        try{
            Callable<Boolean> callableTask = () -> {
                return  travelService.createOrder(orderForm);};
            result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);

        }
        catch (Exception e){log.info("deleteTask error");e.printStackTrace();}
        return "index";
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

@PutMapping(value = "order/put/{id}/{from}/{to}/{date}/{transport}/{process}")
public void putTask(@PathVariable("id") Long id,
                    @PathVariable("from") String from,
                    @PathVariable("to") String to,
                    @PathVariable("date") String date,
                    @PathVariable("transport") Integer transport,
                    @PathVariable("process") Integer process){
    OrderTask orderTask=new OrderTask(id,from,to,date,transport,process==1?
            create:process==2?processed:process==3?done:"empty");
    Boolean result;
    //вносим изменения в     задание по id
    try{
        Callable<Boolean> callableTask = () -> travelService.updateOrder(orderTask);
        result=service.submit(callableTask).get(3000, TimeUnit.SECONDS);
    }
    catch (Exception e){log.info("putTask error");e.printStackTrace();}


}
}
