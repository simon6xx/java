package com.lxx.controller;

import com.lxx.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "多线程事务的数据一致性测试")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping("/batchHandle")
    public void batchHandle() {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 开始执行
            testService.batchHandle();
            stopWatch.stop();
            log.info("执行耗时：{}ms", stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
