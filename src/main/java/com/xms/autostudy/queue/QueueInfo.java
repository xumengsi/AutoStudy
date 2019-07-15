package com.xms.autostudy.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by xumengsi on 2019-07-15 10:14
 */
@Getter
@Setter
@AllArgsConstructor
public class QueueInfo implements Comparable<QueueInfo>{

    private String username;

    private String qiangguoId;

    private String status;

    private Date dateTime;

    @Override
    public int compareTo(QueueInfo queueInfo) {
        if(this.dateTime.getTime() - queueInfo.getDateTime().getTime() > 0){
            return -1;
        }
        return 1;
    }
}
