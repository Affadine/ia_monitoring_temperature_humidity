package iot.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import iot.webservice.model.SensorMeasure;
  
@Controller
public class MainController {
  
    @RequestMapping("/")
    public String welcome() {
        return "index";
    }
    
    /**/
    @RequestMapping(value = { "/history" }, method = RequestMethod.GET)
    public String history(Model model) {
    	SensorMeasure persons = new SensorMeasure();
        model.addAttribute("persons", persons);
 
        return "history";
    }
    
    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {
 
        SensorMeasure personForm = new SensorMeasure();
        model.addAttribute("personForm", personForm);
 
        return "addPerson";
    }
}