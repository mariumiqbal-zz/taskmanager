package it.fds.taskmanager;

import java.sql.Date;
import java.sql.Time;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.fds.taskmanager.dto.TaskDTO;

/**
 * Basic test suite to test the service layer, it uses an in-memory H2 database. 
 * 
 * TODO Add more and meaningful tests! :)
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 10 Jan. 2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TaskServiceJPATest extends Assert{

    @Autowired
    TaskService taskService;

    /* The case would fail because it will execute after some of the cases so list size will not be
       equal to 1 e.g Following error will be shown AssertionError: Expected :1 Actual:5
        If it will execute individually then it will be passed*/

    @Test
    public void writeAndReadOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Test task1");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO t1 = taskService.saveTask(t);
        TaskDTO tOut = taskService.findOne(t1.getUuid());
        assertEquals("Test task1", tOut.getTitle());
        List<TaskDTO> list = taskService.showList();
        assertEquals(1, list.size());
    }

    /* adding and comparing 3 records and then call showList and verify the added data
    verifying Title, status and description fields
    successful Case */
    /*  If the case is executed individually then it will pass but if all cases would run
    then it would fail due to the previous test record */
    @Test
    public void showList() {

        List <TaskDTO> addedDtos = saveRecord();
        List <TaskDTO> dtos = taskService.showList();
        for (int i=0; i< addedDtos.size();i++) {
            assertEquals(3,addedDtos.size());
            assertEquals(dtos.get(i).getTitle(), addedDtos.get(i).getTitle());
            assertEquals(dtos.get(i).getStatus(), addedDtos.get(i).getStatus());
            assertEquals(dtos.get(i).getCreatedat(),addedDtos.get(i).getCreatedat());
        }
    }

    /* adding a record and verifying the added UUID */
    @Test
    public void findOne() {

        TaskDTO t = new TaskDTO();
        t.setTitle("Task1");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO tWrite = taskService.saveTask(t);
        TaskDTO taskDtO = taskService.findOne(tWrite.getUuid());
        assertEquals(tWrite.getUuid(),taskDtO.getUuid());
    }

    /* adding a record and verifying the added UUID */
    @Test
    public void updateTaskRecord() {

        TaskDTO t = new TaskDTO();
        t.setTitle("Test task1");
        t.setDescription("It's never enough");
        t.setStatus(TaskState.POSTPONED.toString().toUpperCase());
        TaskDTO tWrite = taskService.saveTask(t);
        tWrite.setTitle("Visiting Clinic");
        Calendar cal = Calendar.getInstance();
        tWrite.setUpdatedat(cal);
        TaskDTO updatedTask = taskService.updateTask(tWrite);
        assertEquals("Visiting Clinic",updatedTask.getTitle());
    }

    @Test
    public void resolveTaskRecord() {

        TaskDTO t = new TaskDTO();
        t.setTitle("Test task2");
        t.setDescription("It's never enough");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO tWrite = taskService.saveTask(t);
        Boolean resolvedTask = taskService.resolveTask(tWrite.getUuid());
        assertEquals(true,resolvedTask);
    }

    @Test
    public void postponedRecord() {

        TaskDTO t = new TaskDTO();
        t.setTitle("Test task3");
        t.setDescription("never say never");
        TaskDTO tWrite = taskService.saveTask(t);
        Boolean postponedTask = taskService.postponeTask(tWrite.getUuid(), 10);
        assertEquals(true, postponedTask);
    }
// Failed case, umarkpostponed case is not working as expected
    @Test
    public void unMarkPostoned() {
        List <TaskDTO> addedDtos = saveRecord();
        taskService.unmarkPostoned();
        List <TaskDTO> updatedDto = taskService.showList();
        for (int i=0; i< addedDtos.size();i++) {
            assertEquals("RESTORED", updatedDto.get(i).getStatus());
        }
    }

    // saving 3 records
    private List<TaskDTO> saveRecord(){

        List<TaskDTO> list = new ArrayList<>();
        for (int i = 1; i<= 3; i++) {
            TaskDTO t = new TaskDTO();
            t.setTitle("Pay bills: " + i);
            t.setStatus(TaskState.NEW.toString().toUpperCase());
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(1458564270));
            t.setCreatedat(cal);
            list.add(taskService.saveTask(t));
        }
        return list;
    }

    @EnableJpaRepositories
    @Configuration
    @SpringBootApplication
    public static class EndpointsMain{}
}
