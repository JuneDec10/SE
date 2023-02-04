package com.example.springboot.controller;

import com.example.springboot.pojo.Vki;
import com.example.springboot.service.VikServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class vikController {

    @PostMapping("/queryList")
    @ResponseBody
    public Map<String,Object> queryList(@RequestBody Map<String,Object> map) throws Exception {
        String search;
        //判断数据是否为空，空就会赋null值，非空则需要将数据转化为对应的类型后才能进行查询
        search=(map.get("search")==null)? null:map.get("search").toString();
        Integer pageNo,pageSize;
        pageNo =(map.get("pageNo")==null)?null:Integer.valueOf(map.get("pageNo").toString());
        pageSize =(map.get("pageSize")==null)?null:Integer.valueOf(map.get("pageSize").toString());
        VikServiceImpl vikService = new VikServiceImpl();
        return vikService.search(search,pageNo,pageSize);
    }

}











