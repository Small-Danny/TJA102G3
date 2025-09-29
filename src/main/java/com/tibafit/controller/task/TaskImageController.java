package com.tibafit.controller.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tibafit.service.task.TaskService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskImageController {

    @Autowired TaskService taskService;

    // 若是不同網域的純靜態站，開 CORS（改成你的網域）
    @CrossOrigin(origins = { "https://your-static-site.example", "http://localhost:5500" })
    @GetMapping("/{taskId}/icon")
    public ResponseEntity<byte[]> icon(@PathVariable Integer taskId, HttpServletRequest req) {
        var task = taskService.getOneTask(taskId);       // ↓ 第 3 步
        var bytes = task.getTaskIcon();
        if (bytes == null || bytes.length == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No icon");


        var h = new HttpHeaders();
        h.setContentType(MediaType.IMAGE_GIF);       // ★ 固定 GIF
        h.setCacheControl(CacheControl.maxAge(java.time.Duration.ofDays(7)).cachePublic());

        return new ResponseEntity<>(bytes, h, HttpStatus.OK);
    }
}
