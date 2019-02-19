package service;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import domain.entities.Grade;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.groupingBy;

public class ReportService {

    private Service service;

    public ReportService(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    private void printToPdf(String title, String content,File fl, String f) {
        try {
            String stream = "<h1 style=\"text-align:center; font-weight:bold; color:blue; padding-bottom:20px;\">" + title + "</h1>" +
                    "<div>" + content + "</div>";
            String path=fl.toString().replace("\\","\\\\");
            OutputStream file = new FileOutputStream(new File(path+"\\\\" + f));
            Document document = new Document();
            PdfWriter.getInstance(document, file);
            document.open();
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(stream));
            document.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public HashMap<Integer, Integer> getAllAvgsReport(File file) {
        HashMap<Integer, Integer> map = new HashMap<>();
        List<Grade> list = new ArrayList<>();
        service.getAllGrades().forEach(list::add);
        Map<String, Double> avgs = list.stream().collect(groupingBy(Grade::getSid, averagingInt(grade -> (int) grade.getGrade())));
        for (Double x : avgs.values()) {
            int round = (int) Math.floor(x);
            if (map.containsKey(round)) {
                map.put(round, map.get(round) + 1);
            } else {
                map.put(round, 1);
            }
        }
        String content = "";

        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            content += "<p>Number of students averaging " + (e.getKey() == 10 ? "10" : " between " + e.getKey() + " and " + Integer.toString(e.getKey() + 1)) + " is: " + e.getValue() + "</p>";
        }


        printToPdf("Averages report", content, file,  "Averages.pdf");


        return map;
    }

    public HashMap<Float, Integer> getGradesPerHomeworkReport(String hid, File file) {
        HashMap<Float, Integer> map = new HashMap<>();
        for (Grade g : service.getAllGrades()) {
            if (g.getHid().equals(hid)) {
                if (map.containsKey(g.getGrade())) {
                    map.put(g.getGrade(), map.get(g.getGrade()) + 1);
                } else {
                    map.put(g.getGrade(), 1);
                }
            }
        }

        String content = "";

        for (Map.Entry<Float, Integer> e : map.entrySet()) {
            content += "<p>Number of grades of " + e.getKey() + " is: " + e.getValue() + "</p>";
        }


        printToPdf("Grades per homework " + hid, content, file, "GPH.pdf");

        return map;
    }

    public HashMap<String, Integer> getPassedFailedReport(File file) {
        List<Grade> list = new ArrayList<>();
        service.getAllGrades().forEach(list::add);
        Map<String, Double> avgs = list.stream().collect(groupingBy(Grade::getSid, averagingInt(grade -> (int) grade.getGrade())));
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Passed", 0);
        map.put("Failed", 0);
        for (Double x : avgs.values()) {
            if (x < 5)
                map.put("Failed", map.get("Failed") + 1);
            else
                map.put("Passed", map.get("Passed") + 1);
        }

        String content = "";

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            content += "<p>Number of students that " + e.getKey() + " is: " + e.getValue() + "</p>";
        }


        printToPdf("Passed/Failed Report", content, file,  "Passed-failed.pdf");

        return map;
    }

    public HashMap<String, Integer> getLateHomeworksReport(String hid, File file) {
        ArrayList<Grade> list = new ArrayList<>();
        service.getAllGrades().forEach(grade -> {
            if (grade.getHid().equals(hid))
                list.add(grade);
        });

        HashMap<String, Integer> map = new HashMap<>();
        for (Grade g : list) {
            if (g.getDeadline() - g.getWeek() >= 0) {
                map.merge("On time", 1, (a, b) -> a + b);
            } else if (g.getDeadline() - g.getWeek() >= -2) {
                map.merge("1-2 week late", 1, (a, b) -> a + b);
            } else {
                map.merge(">2 weeks late", 1, (a, b) -> a + b);
            }
        }

        String content = "";

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            content += "<p>Number of students that submitted the homework " + e.getKey() + " is: " + e.getValue() + "</p>";
        }


        printToPdf("Submit Lagging - homework "+hid, content, file, "Lagging.pdf");

        return map;
    }

}
