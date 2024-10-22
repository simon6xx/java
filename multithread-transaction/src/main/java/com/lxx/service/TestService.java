package com.lxx.service;

import com.lxx.custom.SelfTransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final SelfTransactionManager selfTransactionManager;

    private final StudentService studentService;

    // 子线程提交成功与否标志符
    public static volatile boolean flag = true;

    public void batchHandle() {
        // 主线程等待所有子线程执行完成
        int threadCount = 5;
        CountDownLatch childMonitor = new CountDownLatch(threadCount);
        // 最后的子线程标识结果，不能用 ArrayList，因为它是不安全的
        List<Boolean> childResponse = Collections.synchronizedList(new ArrayList<>());
        // 子线程等待主线程通知
        CountDownLatch mainMonitor = new CountDownLatch(1);

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < threadCount; i++) {
            // int number = i;
            executor.execute(() -> {
                // 开启事务
                TransactionStatus transactionStatus = selfTransactionManager.begin();
                try {
                    // if (number == 1) {
                    //     throw new Exception("xxx");
                    // }
                    // 开始执行插入操作(一个线程插入10w数据)
                    studentService.insertStudentList();
                    childResponse.add(Boolean.TRUE);
                    childMonitor.countDown();
                    log.info("线程{}正常执行完成，等待其他线程执行结束，判断是否需要回滚...", Thread.currentThread().getName());
                    mainMonitor.await();

                    if (flag) {
                        log.info("所有线程均正常完成，线程{}事务提交", Thread.currentThread().getName());
                        selfTransactionManager.commit(transactionStatus);
                    } else {
                        log.info("有线程出现异常，线程{}事务回滚", Thread.currentThread().getName());
                        selfTransactionManager.rollback(transactionStatus);
                    }
                } catch (Exception e) {
                    // 如果有一个子线程出现了异常，就通知主线程全部进行回滚
                    TestService.flag = false;
                    childResponse.add(Boolean.FALSE);
                    childMonitor.countDown();
                    mainMonitor.countDown();
                    log.error("线程{}发生了异常，开始进行事务回滚", Thread.currentThread().getName());
                    selfTransactionManager.rollback(transactionStatus);
                }
            });
        }

        try {
            // 主线程等待所有子线程执行完成
            while (childMonitor.getCount() > 0) {
                childMonitor.await();
                if (!flag) {
                    // 如果有一个子线程失败了，必须要改变标识符，让所有子线程回滚
                    log.info("在{}线程中，标识符的值被改为了false", Thread.currentThread().getName());
                    mainMonitor.countDown();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
