package com.example.bmi.controller;

import com.example.bmi.dto.BmiRequest;
import com.example.bmi.dto.BmiResponse;
import com.example.bmi.model.BmiRecord;
import com.example.bmi.service.BmiService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class BmiWebController {

    private final BmiService bmiService;

    @Autowired
    public BmiWebController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @ModelAttribute("bmiRequest")
    public BmiRequest defaultBmiRequest() {
        return new BmiRequest();
    }

    @GetMapping
    public String home(@RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "unit", defaultValue = "metric") String unit,
                       @RequestParam(value = "deleted", required = false) Long deletedId,
                       @ModelAttribute("bmiRequest") BmiRequest request,
                       Model model) {
        populateModel(model, request, null, search, unit);
        if (deletedId != null) {
            model.addAttribute("flashMessage", "Record #" + deletedId + " deleted successfully.");
        }
        return "index";
    }

    @PostMapping("/calculate")
    public String calculate(@Valid @ModelAttribute("bmiRequest") BmiRequest request,
                            BindingResult bindingResult,
                            @RequestParam(value = "unit", defaultValue = "metric") String unit,
                            @RequestParam(value = "search", required = false) String search,
                            Model model) {
        if (bindingResult.hasErrors()) {
            populateModel(model, request, null, search, unit);
            return "index";
        }

        request.setHeightUnit("imperial".equalsIgnoreCase(unit) ? "ft_in" : "cm");
        request.setWeightUnit("imperial".equalsIgnoreCase(unit) ? "lbs" : "kg");

        BmiResponse result = bmiService.calculateAndSave(request);
        populateModel(model, request, result, search, unit);
        return "index";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         @RequestParam(value = "unit", defaultValue = "metric") String unit,
                         @RequestParam(value = "search", required = false) String search) {
        bmiService.deleteRecord(id);
        String redirectUrl = "redirect:/?unit=" + unit + "&deleted=" + id;
        if (search != null && !search.isBlank()) {
            redirectUrl += "&search=" + search;
        }
        return redirectUrl;
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=bmi_records.csv");

        List<BmiRecord> records = bmiService.getHistory();
        PrintWriter writer = response.getWriter();
        writer.println("ID,Name,Age,Height (cm),Weight (kg),BMI,Category,Calculated At");

        for (BmiRecord r : records) {
            writer.printf("%d,\"%s\",%d,%.1f,%.1f,%.2f,\"%s\",\"%s\"%n",
                    r.getId() != null ? r.getId() : 0,
                    r.getName() != null ? r.getName().replace("\"", "\"\"") : "",
                    r.getAge() != null ? r.getAge() : 0,
                    r.getHeightCm() != null ? r.getHeightCm() : 0.0,
                    r.getWeightKg() != null ? r.getWeightKg() : 0.0,
                    r.getBmi() != null ? r.getBmi() : 0.0,
                    r.getCategory() != null ? r.getCategory() : "",
                    r.getCalculatedAt() != null ? r.getCalculatedAt().toString() : ""
            );
        }
        writer.flush();
    }

    private void populateModel(Model model, BmiRequest request, BmiResponse result, String search, String unit) {
        if (request.getHeight() == null) {
            request.setHeight("imperial".equalsIgnoreCase(unit) ? 5.0 : 175.0);
            if ("imperial".equalsIgnoreCase(unit)) {
                request.setHeightInches(9.0);
            }
        }
        if (request.getWeight() == null) {
            request.setWeight("imperial".equalsIgnoreCase(unit) ? 154.0 : 70.0);
        }

        List<BmiRecord> history = bmiService.getHistory();
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim().toLowerCase();
            history = history.stream()
                    .filter(r -> (r.getName() != null && r.getName().toLowerCase().contains(query))
                            || (r.getCategory() != null && r.getCategory().toLowerCase().contains(query))
                            || String.valueOf(r.getAge()).contains(query))
                    .collect(Collectors.toList());
        }

        model.addAttribute("bmiRequest", request);
        model.addAttribute("result", result);
        model.addAttribute("history", history);
        model.addAttribute("historyCount", history.size());
        model.addAttribute("search", search);
        model.addAttribute("unit", unit);
        model.addAttribute("dbConnected", bmiService.isDatabaseConnected());
    }
}
