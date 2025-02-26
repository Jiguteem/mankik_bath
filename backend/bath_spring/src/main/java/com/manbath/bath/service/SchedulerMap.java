package com.manbath.bath.service;


import com.manbath.bath.entitiy.Schedule;
import com.manbath.bath.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class SchedulerMap {

    @Autowired
    private ScheduleRepository scheduleRepository;

    private static final Map<String, Map<String, Schedule>> schedulerMap = new HashMap<>();


    //사용자 id, <사용자 id + 예약 시간 ,스케쥴>
    public synchronized Map<String, Map<String, Schedule>> getSchedulerMap() {
        return schedulerMap;
    }

    public synchronized Map<String, Map<String, Schedule>> addSchedulerMap(Schedule schedule) {
        System.out.println("에드 실행_맵");
        //들어온 걸로 id 찾고
        if (schedulerMap.containsKey(schedule.getUserid().getUserid())) {
            System.out.println("에드 맵 있음");
            //있으면 추가
            schedulerMap.get(schedule.getUserid().getUserid()).put(schedule.getUserid().getUserid() + schedule.getTime(), schedule);
            System.out.println("에드 맵 끝");
        } else {
            System.out.println("에드 실행_맵_없음");
            //없으면 만들어서 추가
            Map<String, Schedule> map = new HashMap<>();
            map.put(schedule.getUserid().getUserid() + schedule.getTime(), schedule);
            schedulerMap.put(schedule.getUserid().getUserid(), map);
            System.out.println("에드 맵 끝");
        }
        return schedulerMap;
    }

    public synchronized String deleteSchedulerMap(String key) {
        //받은키 자르기
        //뒤에서 부터 자르기
        // 2018-06-17T22:46:17.348
        String key_tmp;

        try {
            key_tmp = key.substring(0, key.length() - 26);
        } catch (Exception e) {
            System.out.println("에러 : " + e.toString());
            return "false";
        }

        System.out.println("key_tmp :" + key_tmp);
        System.out.println("key : " + key);

        try {
            Schedule schedule = schedulerMap.get(key_tmp).get(key);
            schedulerMap.get(key_tmp).remove(key);
            schedule.setDelete(1);
            scheduleRepository.save(schedule);
            return schedule.toString();
        } catch (Exception e) {
            System.out.println("에러 : " + e.toString());
            return "false";
        }

    }

    public synchronized Map<String, Schedule> findScheduleUserId(String key) {
        //받은키 자르기
        //뒤에서 부터 자르기
        // 2018-06-17T22:46:17.348
        //받은키 자르기
        //뒤에서 부터 자르기
        // 2018-06-17T22:46:17.348

        try {
            return schedulerMap.get(key);
        } catch (Exception e) {
            return new HashMap<>();
        }

    }

    public synchronized Map<String, Schedule> findScheduleBathId(String key) {
        //받은키 자르기
        //뒤에서 부터 자르기
        // 2018-06-17T22:46:17.348
        //받은키 자르기
        //뒤에서 부터 자르기
        // 2018-06-17T22:46:17.348
        //사용자 id, <사용자 id + 예약 시간 ,스케쥴>
        //schedulerMap.keySet();

        Map<String, Schedule> tmp = new HashMap<>();
        System.out.println("이건 에러 테스트");
        System.out.println(key);

        for(Map.Entry<String, Map<String, Schedule>> elem : schedulerMap.entrySet()){
            for(Map.Entry<String, Schedule> en : elem.getValue().entrySet()){
                System.out.println("바스 id");
                System.out.println(en.getValue().getBathid().getBathid());
                if(en.getValue().getBathid().getBathid().equals(key)){
                    tmp.put(en.getKey(),en.getValue());
                }
            }
        }


        try {
            return tmp;
        } catch (Exception e) {
            return new HashMap<>();
        }

    }

    public void putAll(Map<String,Map<String,Schedule>> s){
        schedulerMap.clear();
        schedulerMap.putAll(s);
    }

}
