// API Controller
package com.tibafit.controller.task;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.task.TaskDTO;
import com.tibafit.service.task.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskApiController {
  private final TaskService taskSvc;
  public TaskApiController(TaskService taskSvc) { this.taskSvc = taskSvc; }

  @GetMapping
  public List<TaskDTO> list() {
    return taskSvc.getAll().stream()
        .map(TaskDTO::from)
        .toList(); // JDK8 用 .collect(Collectors.toList())
  }
}
