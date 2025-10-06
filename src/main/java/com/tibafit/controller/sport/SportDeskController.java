package com.tibafit.controller.sport;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/dMain")
public class SportDeskController {
	
	// fd
	@GetMapping("/fd/workoutPlan_main")
	public String workoutPlanMain() {
		return "redirect:/frontend-template/workoutPlan_main.html";
	}
	
	@GetMapping("/fd/workoutPlan_add")
	public String workoutPlanAdd() {
		return "redirect:/frontend-template/workoutPlan_addPlan.html";
	}
	
	
	// bd
	@GetMapping("/bd/sportList")
	public String sportList(ModelMap model) {
		return "admin/sport/sportList";
	}
	@GetMapping("/bd/sportTypeList")
	public String sportTypeList(ModelMap model) {
		return "admin/sport/sportTypeList";
	}
	
	
    // test use
	@GetMapping("/bd/sport")
	public String sportMainTest(ModelMap model) {
		return "redirect:/adminlte/sport_test/sport.html";
	}
	
	@GetMapping("/bd/customSport")
	public String customSportMainTest(ModelMap model) {
		return "redirect:/adminlte/customSport_test/customSport.html";
	}
	
	@GetMapping("/bd/sportType")
	public String sportTypeMainTest(ModelMap model) {
		return "redirect:/adminlte/sportType_test/sportType.html";
	}
	
	@GetMapping("/bd/sportTypeItem")
	public String sportTypeItemMainTest(ModelMap model) {
		return "redirect:/adminlte/sportTypeItem_test/sportTypeItem.html";
	}
	
	@GetMapping("/bd/workoutPlan")
	public String workoutPlanMainTest(ModelMap model) {
		return "redirect:/adminlte/workoutPlan_test/workoutPlan.html";
	}
	
	@GetMapping("/bd/workoutPlanRecord")
	public String workoutPlanRecordMainTest(ModelMap model) {
		return "redirect:/adminlte/workoutPlanRecord_test/workoutPlanRecord.html";
	}
	
	@GetMapping("/bd/testUploadFile")
	public String testUploadFileMain(ModelMap model) {
		return "redirect:/adminlte/sport_test/testUploadFile.html";
	}
}
