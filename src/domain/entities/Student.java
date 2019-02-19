package domain.entities;

/**
 * Class that defines a stundent
 */
public class Student implements HasID<String> {

    private String id, name, email, group;

    public Student(){}

    /**
     * @param id integer, student's unique id
     * @param name string, student's name
     * @param group integer, student's group
     * @param email string, student's email
     */
    public Student(String id, String name, String group, String email){
        this.id=id;
        this.name=name;
        this.group=group;
        this.email=email;
    }

    /**
     * @return student's group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return student's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return student's name
     */
    public String getName() {
        return name;
    }

    /**
     * set student email
     * @param email student's mail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * set student group
     * @param group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * set student name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the id of the student
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * @param id the id to be set, integer, not null
     */
    @Override
    public void setID(String id) {
        this.id=id;
    }

    /**
     * @return string representation of the student
     */
    @Override
    public String toString() {
        return "Id: "+id+" |Name: "+name+" |Group: "+group+" |Email: "+email;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Grade))
            return false;
        Student ot=(Student) obj;
        return this.getID().equals(ot.getID());
    }
}
