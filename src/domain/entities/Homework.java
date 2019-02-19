package domain.entities;

import domain.entities.HasID;

public class Homework implements HasID<String> {

    private int deadlineWeek;
    private String description, id;

    /**
     * @param id id of the homework, not null, int
     * @param deadlineWeek deadline of the homework, int
     * @param description string
     */
    public Homework(String id, int deadlineWeek, String description){
        this.id=id;
        this.deadlineWeek=deadlineWeek;
        this.description=description;
    }

    public Homework(){}

    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return homework deadline
     */
    public int getDeadlineWeek() {
        return deadlineWeek;
    }

    /**
     * @param deadlineWeek deadline to be set; must be int, higher than the current value
     */
    public void setDeadlineWeek(int deadlineWeek) {
        this.deadlineWeek = deadlineWeek;
    }

    /**
     * @return id f the homework
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * @param id id to be given to the homework
     */
    @Override
    public void setID(String id) {
        this.id=id;
    }

    @Override
    public String toString() {
        return "Id: "+id+" |Deadline: "+Integer.toString(deadlineWeek)+" |Description: "+description+"\n";
    }
}
