package repository;

import domain.entities.Grade;
import domain.validator.ValidationException;
import domain.validator.Validator;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GradesXMLRepo extends Repository<Pair<String, String>, Grade> {
    
    private String filename;
    
    /**
     * @param validator the validator to be used in the repo
     */
    public GradesXMLRepo(Validator<Grade> validator, String f) {
        super(validator);
        filename=f;
        loadData();
    }

    private Grade createGradeFromElement(Element GradeElement) {
        Grade g=new Grade();
        String sid=GradeElement.getAttribute("sid");
        String hid=GradeElement.getAttribute("hid");
        g.setID(new Pair<>(sid, hid));
        float grade=Float.parseFloat(GradeElement.getElementsByTagName("grade").item(0).getTextContent());
        int week=Integer.parseInt(GradeElement.getElementsByTagName("week").item(0).getTextContent());
        int deadline=Integer.parseInt(GradeElement.getElementsByTagName("deadline").item(0).getTextContent());
        String feedback=GradeElement.getElementsByTagName("feedback").item(0).getTextContent();
        String studentName=GradeElement.getElementsByTagName("studentName").item(0).getTextContent();
        String prof=GradeElement.getElementsByTagName("prof").item(0).getTextContent();
        g.setGrade(grade);
        g.setWeek(week);
        g.setDeadline(deadline);
        g.setFeedback(feedback);
        g.setStudentName(studentName);
        g.setProf(prof);
        return g;
    }

    private void loadData() {
        try {
            Document document= DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(filename);
            Element root=document.getDocumentElement();
            NodeList GradeElements=root.getChildNodes();
            for(int i=0; i<GradeElements.getLength();i++)
            {
                Node GradeElement=GradeElements.item(i);
                if (GradeElement.getNodeType() == Node.ELEMENT_NODE){
                    Grade g=createGradeFromElement((Element)GradeElement); //cast
                    super.save(g);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Element createElementFromGrade(Document document, Grade Grade) {
        Element element=document.createElement("Grade");
        element.setAttribute("sid",Grade.getSid());
        element.setAttribute("hid",Grade.getHid());
        Element grade=document.createElement("grade");
        grade.setTextContent(Float.toString(Grade.getGrade()));
        element.appendChild(grade);
        Element week=document.createElement("week");
        week.setTextContent(Integer.toString(Grade.getWeek()));
        element.appendChild(week);
        Element deadline=document.createElement("deadline");
        deadline.setTextContent(Integer.toString(Grade.getDeadline()));
        element.appendChild(deadline);
        Element feedback=document.createElement("feedback");
        feedback.setTextContent(Grade.getFeedback());
        element.appendChild(feedback);
        Element stName=document.createElement("studentName");
        stName.setTextContent(Grade.getStudentName());
        element.appendChild(stName);
        Element prof=document.createElement("prof");
        prof.setTextContent(Grade.getProf());
        element.appendChild(prof);
        return element;
    }

    private void writeToFile(){
        try {
//create an empty Document
            Document document = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .newDocument();
//add the list elements to Document
            Element root=document.createElement("grades");
            document.appendChild(root);
            findAll().forEach(Grade -> {
                Element hElement=createElementFromGrade(document, Grade);
                root.appendChild(hElement);
            });
//write Document to file
            Transformer transformer = TransformerFactory.
                    newInstance().newTransformer();
            transformer.transform(new DOMSource(document),
                    new StreamResult(filename));
        }catch (Exception ex){}
    }

    @Override
    public Grade save(Grade s) throws ValidationException {
        Grade st = super.save(s);
        writeToFile();
        return st;
    }

    @Override
    public Grade update(Grade entity) {
        Grade st=super.update(entity);
        writeToFile();
        return st;
    }

    @Override
    public Grade delete(Pair<String, String> id) {
        Grade st = super.delete(id);
        writeToFile();
        return st;
    }
}
