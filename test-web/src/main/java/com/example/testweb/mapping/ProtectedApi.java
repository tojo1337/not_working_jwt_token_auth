package com.example.testweb.mapping;

import com.example.testweb.data.mockdata.UserImp;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/secured/v1/")
public class ProtectedApi {
    private List<UserImp> list = new ArrayList<>();
    @GetMapping
    public UserImp greet(){
        UserImp l = new UserImp();
        l.setImportantData("Hello world here");
        return l;
    }
    @PostMapping("setdata")
    public List<UserImp> setUserImpData(@RequestBody UserImp imp){
        list.add(imp);
        return list;
    }
    @GetMapping("list")
    public List<UserImp> showSecuredList(){
        return list;
    }
}
