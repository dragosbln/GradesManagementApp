package service;

import domain.entities.Grade;
import domain.entities.Homework;
import domain.entities.Student;
import javafx.util.Pair;
import repository.*;
import util.ChangeType;
import util.GoogleMail;

import javax.mail.MessagingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Service extends Observable {

    private HomeworkXMLRepo homeworkXMLRepo;
    private StudentXMLRepo studentXMLRepo;
    private GradesXMLRepo gradesXMLRepo;

    public Service(HomeworkXMLRepo homeworkXMLRepo, StudentXMLRepo studentXMLRepo, GradesXMLRepo gradesXMLRepo){
        this.homeworkXMLRepo=homeworkXMLRepo;
        this.studentXMLRepo=studentXMLRepo;
        this.gradesXMLRepo=gradesXMLRepo;
    }


    public Iterable<Homework> getAllHomeworks(){
        return homeworkXMLRepo.findAll();
    }

    public void addHomework(String id, int deadline, String description){
        Homework h=new Homework(id, deadline, description);
        homeworkXMLRepo.save(h);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for(Student s: studentXMLRepo.findAll()){
            executorService.execute(() -> {
                try {
                    GoogleMail.Send("thecooleststappever", "lalele123", s.getEmail(),"New homework notification", "New homework added! Details:\nID: "+id+"\nDeadline week: "+Integer.toString(deadline)+"\nDescription: "+description+"\nGood luck!");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        setChanged();
        notifyObservers(ChangeType.HOMEWORK);
        clearChanged();
    }

    public void extendDeadline(String hid, int deadline){
        Homework h=homeworkXMLRepo.findOne(hid);
        int old=h.getDeadlineWeek();
        if(h==null){
            throw new RepositoryException("Homework doesn't exist!!!");
        }
        if(h.getDeadlineWeek()>deadline){
            throw new RepositoryException("neneneee.... extending the deadline, not makin it more strict ;)");
        }
        h.setDeadlineWeek(deadline);
        homeworkXMLRepo.update(h);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for(Student s: studentXMLRepo.findAll()){
            executorService.execute(() -> {
                try {
                    GoogleMail.Send("thecooleststappever", "lalele123", s.getEmail(),"Homework deadline extended!", "\nID: "+hid+"\n Old Deadline week: "+Integer.toString(old)+"\nNew deadline week: "+h.getDeadlineWeek()+"\nDescription: "+h.getDescription()+"\nGood luck!");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        setChanged();
        notifyObservers(ChangeType.HOMEWORK);
        clearChanged();
    }

    public void deleteHomework(String id) {
        ArrayList<Pair<String, String>> keys=new ArrayList<>();
        for(Grade g:gradesXMLRepo.findAll()){
            if(g.getHid().equals(id)){
                keys.add(new Pair<>(g.getSid(),g.getHid()));
            }
        }
        for (Pair<String, String> key: keys){
            gradesXMLRepo.delete(key);
        }

        setChanged();
        notifyObservers(ChangeType.GRADE);
        clearChanged();

        homeworkXMLRepo.delete(id);
        setChanged();
        notifyObservers(ChangeType.HOMEWORK);
        clearChanged();    }

    public void addStudent(String id, String name, String group, String email){
        Student s=new Student(id, name, group, email);
        studentXMLRepo.save(s);
        setChanged();
        notifyObservers(ChangeType.STUDENT);
        clearChanged();
    }

    public Student getStudent(String id){
        return studentXMLRepo.findOne(id);
    }

    public Iterable<Student> getAllStudents(){
        return studentXMLRepo.findAll();
    }

    public void modifyStudent(String id, String name, String group, String email){
        Student s=new Student(id, name, group, email);
        studentXMLRepo.update(s);
        setChanged();
        notifyObservers(ChangeType.STUDENT);
        clearChanged();
    }

    public ArrayList<String> getStudentNames(){
        ArrayList<String> list=new ArrayList<>();
        for (Student s:getAllStudents()){
            list.add(s.getName());
            list.add(s.getID());
        }
        return list;
    }

    public void deleteStudent(String id){
        ArrayList<Pair<String, String>> keys=new ArrayList<>();
        for(Grade g:gradesXMLRepo.findAll()){
            if(g.getSid().equals(id)){
                keys.add(new Pair<>(g.getSid(),g.getHid()));
            }
        }
        for (Pair<String, String> key: keys){
            gradesXMLRepo.delete(key);
        }

        setChanged();
        notifyObservers(ChangeType.GRADE);
        clearChanged();

        studentXMLRepo.delete(id);
        setChanged();
        notifyObservers(ChangeType.STUDENT);
        clearChanged();
    }
    public Student getStudentByName(String name){
        for(Student s: studentXMLRepo.findAll()){
            if(s.getName().equals(name))
                return s;
        }
        throw new RepositoryException("This student doesn't wxist!");
    }

    public Iterable<Grade> getAllGrades(){
        return gradesXMLRepo.findAll();
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void addGrade(String sid, String hid, String prof, float gr, String f, boolean motivated) throws ParseException {
        Homework h=homeworkXMLRepo.findOne(hid);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date baseDate=sdf.parse("10/01/2018");
        String nowStr=sdf.format(new Date());
        Date now=sdf.parse(nowStr);

        int week=(int)getDateDiff(baseDate, now, TimeUnit.DAYS)/7;

        int dif=h.getDeadlineWeek()-week;

        if(!motivated){
            if(dif<-2){
                gr=1;
                f+="; INTARZIERE CU MAI MULT DE 2 SAPTAMANI!";
            } else if(dif<0){
                gr+=2.5*dif;
                f+="; INTARZIERE DE "+dif+" SAPTAMANI; NOTA DIMINUATA CU "+2.5*dif+"!!!";
            }
        } else {
            f+="; Intarziat, dar motivat";
        }

        if (gr<0)
            gr=1;

        gradesXMLRepo.save(new Grade(sid, hid, getStudent(sid).getName(), prof, gr, week, h.getDeadlineWeek(), f));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                GoogleMail.Send("thecooleststappever", "lalele123", getStudent(sid).getEmail(),"Grade notification", getGrade(sid, hid).toString());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });

        setChanged();
        notifyObservers(ChangeType.GRADE);
        clearChanged();
    }

    public ArrayList<Grade> getHomeworkGrades( String hid){
        ArrayList<Grade> grades = new ArrayList<>();
        for(Grade g:getAllGrades()){
            if(g.getHid().equals(hid))
                grades.add(g);
        }
        return grades;
    }

    public ArrayList<Grade> getGroupGrades( String group){
        ArrayList<Grade> grades = new ArrayList<>();
        for(Grade g:getAllGrades()){
            Student s=getStudent(g.getSid());
            if(s.getGroup().equals(group))
                grades.add(g);
        }
        return grades;
    }

    public ArrayList<Grade> getGradesByStudentName(String name){
        ArrayList<Grade> grades = new ArrayList<>();
        for(Grade g:getAllGrades()){
            Student s=getStudent(g.getSid());
            if(s.getName().contains(name))
                grades.add(g);
        }
        return grades;
    }
//TODO getGradesByAttribute
    public ArrayList<Grade> getGradesByEmail(String email){
        ArrayList<Grade> list=new ArrayList<>();

        Student s=null;
        for(Student s1:getAllStudents()){
            if(s1.getEmail().equals(email)){
                s=s1;
                break;
            }
        }
        if(s==null){
            return list;
        }else {
            for(Grade g:getAllGrades()){
                if(g.getSid().equals(s.getID()))
                    list.add(g);
            }
            return list;
        }
    }


    public ArrayList<Grade> getGradesByDate(Date d1, Date d2) throws ParseException {
        if (d1==null||d2==null){
            throw new RuntimeException("Pick both dates!");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date baseDate=sdf.parse("10/01/2018");

        int week1 = (int)getDateDiff(baseDate, d1,TimeUnit.DAYS)/7;
        int week2 = (int)getDateDiff(baseDate,d2,TimeUnit.DAYS)/7;
        ArrayList<Grade> grades=new ArrayList<>();
        for(Grade g:getAllGrades()){
            if(g.getWeek()>=week1&&g.getWeek()<=week2){
                grades.add(g);
            }
        }
        return grades;
    }

    public Grade getGrade(String sid, String hid){
        return gradesXMLRepo.findOne(new Pair<>(sid, hid));
    }
    
}
