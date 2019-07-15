package com.xms.autostudy.queue;

import com.xms.autostudy.analysis.Login;
import com.xms.autostudy.analysis.StudyStatus;
import com.xms.autostudy.configuration.ScoreConfiguration;
import com.xms.autostudy.process.StudyProcess;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by xumengsi on 2019-07-10 15:51
 */
@Order(Integer.MAX_VALUE)
@Component
public class StudyQueue implements CommandLineRunner {

    private ConcurrentHashMap<String,QueueInfo> storage = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,DriverInfo> executeStorage = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(StudyQueue.class);

    @Autowired
    private QueueRule queueRule;

    public void setUserStudyStatus(String key, QueueInfo queueInfo){
        storage.put(key, queueInfo);
    }

    public QueueInfo getUserStudyStatus(String key){
        return storage.get(key);
    }

    public void setUserStudyDriverInfo(String key, DriverInfo driverInfo){
        executeStorage.put(key, driverInfo);
    }

    public void deleteUserStudyDriverInfo(String key){
        executeStorage.remove(key);
    }

    public DriverInfo getUserStudyDriverInfo(String key){
        return executeStorage.get(key);
    }

    public List<DriverInfo> getUserStudyDriverInfo(String... keys){
        List<DriverInfo> driverInfoList = null;
        for (String key : keys){
            driverInfoList.add(executeStorage.get(key));
        }
        return driverInfoList;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        int number = 0;
        Login.FutureRunnable runnable = new Login.FutureRunnable() {

            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                int queueNumber = 0;
                String newDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if(CollectionUtils.isEmpty(storage)){
                    return ;
                }
                for(Map.Entry<String, QueueInfo> map : storage.entrySet()) {
                    if (Pattern.matches(newDate, map.getKey())) {
                        if (map.getValue().getStatus().equals(StudyStatus.QUEUESTUDY.name())) {
                            queueNumber++;
                        }
                    }else{
                        storage.remove(map.getKey(), map.getValue());
                    }
                }
                log.info("当前队列数量：{}", queueNumber);
                if(queueNumber > queueRule.getMaxNumber()){
                    int isExecuteNumber = queueNumber - queueRule.getMaxNumber();
                    List<String> isExecuteQiangguoIdList = storage.values().stream().sorted().limit(isExecuteNumber).map(QueueInfo::getQiangguoId).collect(Collectors.toList());
                    List<String> isNotExecuteQiangguoIdList = storage.values().stream().sorted().skip(isExecuteNumber).map(QueueInfo::getQiangguoId).collect(Collectors.toList());

                    List<DriverInfo> isExecuteDriverInfoList = getUserStudyDriverInfo(isExecuteQiangguoIdList.toArray(new String[isExecuteQiangguoIdList.size()]));
                    List<DriverInfo> isNotExecuteDriverInfoList = getUserStudyDriverInfo(isNotExecuteQiangguoIdList.toArray(new String[isNotExecuteQiangguoIdList.size()]));
                    execute(isExecuteDriverInfoList);
                    isNotExecuteDriverInfoList.stream().forEach(x -> {
                        x.getDriver().navigate().refresh();
                    });
                }else if(queueNumber != 0 &&  queueNumber <= queueRule.getMaxNumber() ) {
                    List<String> isExecuteQiangguoIdList = storage.values().stream().map(QueueInfo::getQiangguoId).collect(Collectors.toList());
                    List<DriverInfo> isExecuteDriverInfoList = getUserStudyDriverInfo(isExecuteQiangguoIdList.toArray(new String[isExecuteQiangguoIdList.size()]));
                    execute(isExecuteDriverInfoList);
                }
            }
        };
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Future<?> future = executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
        runnable.setFuture(future);
    }

    private void execute(List<DriverInfo> isExecuteDriverInfoList){
        isExecuteDriverInfoList.forEach(x -> {
            StudyProcess process = new StudyProcess();
            WebDriver webDriver = x.getDriver();
            JavascriptExecutor javascriptExecutor = x.getJsExecutor();
            Map<String, ScoreConfiguration> rules = x.getRules();
            String token = x.getToken();
            String qiangguoId = x.getQiangguoId();
            String username = x.getUsername();
            process.autoAnalysis(webDriver, javascriptExecutor, rules, token, qiangguoId, username);
        });
    }

}
