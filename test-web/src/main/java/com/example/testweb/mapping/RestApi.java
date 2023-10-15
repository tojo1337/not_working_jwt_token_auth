package com.example.testweb.mapping;
import com.example.testweb.data.mockdata.SampleData;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/rest/v1/")
public class RestApi {
    private List<SampleData> list = new ArrayList<>();
    @GetMapping
    public SampleData greet(){
        SampleData l = new SampleData();
        l.setData("Hello world here");
        return l;
    }
    @PostMapping("store")
    public List<SampleData> store(@RequestBody SampleData data){
        list.add(data);
        return list;
    }
    @GetMapping("list")
    public List<SampleData> listDisplay(){
        return list;
    }
}

