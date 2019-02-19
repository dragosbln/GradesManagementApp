package domain.entities;

import javafx.util.Pair;

public class Grade implements HasID<Pair<String, String>> {

    private int week, deadline;
    private float grade;
    private String feedback, studentName, prof;
    private Pair<String, String> id;

    /**
     * @param sid id of the student that receives the grade
     * @param hid id of the homework that is evaluated
     * @param grade value of the grade
     * @param week week in which the grade is received
     * @param deadline deadline of the current homework
     * @param feedback feedback for the current grade
     */
    public Grade(String sid, String hid, String studentName, String prof, float grade, int week, int deadline, String feedback){
        this.grade=grade;
        this.week=week;
        this.feedback=feedback;
        this.deadline=deadline;
        this.studentName=studentName;
        this.prof=prof;
        this.id=new Pair<>(sid, hid);
    }

    public Grade(){}

    /**
     * @return week in which the grade was received
     */
    public int getWeek() {
        return week;
    }

    /**
     * @return grade - the value
     */
    public float getGrade() {
        return grade;
    }

    /**
     * @return ferdback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * @return deadline - of the current homrwork
     */
    public int getDeadline() {
        return deadline;
    }

    /**
     * @param deadline
     */
    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    /**
     * @param feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * @param grade
     */
    public void setGrade(float grade) {
        this.grade = grade;
    }

    /**
     * @param week
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * @return id - of the current element
     */

    public String getSid(){
        return id.getKey();
    }

    public String getHid(){
        return id.getValue();
    }


    @Override
    public Pair<String, String> getID() {
        return id;
    }

    @Override
    public void setID(Pair<String, String> stringStringPair) {
        this.id=stringStringPair;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Grade))
            return false;
        Grade ot=(Grade) obj;
        return this.getID().getKey().equals(ot.getID().getKey()) && this.getID().getValue().equals(ot.getID().getValue());
    }

    @Override
    public String toString() {
        return "Homework: "+getHid()+"\nProfessor: "+getProf()+"\nGrade: "+getGrade()+"\nSubmitted in week: "+getWeek()+"\nDeadline Week: "+getDeadline()+"\nFeedback: "+getFeedback();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }
}
