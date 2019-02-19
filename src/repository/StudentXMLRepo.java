package repository;

import domain.entities.Student;
import domain.validator.ValidationException;
import domain.validator.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import repository.Repository;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class StudentXMLRepo extends Repository<String, Student> {

    private String file;


    private Student createStudentFromElement(Element studentElement) {
        Student s=new Student();
        String id=studentElement.getAttribute("id");
        s.setID(id);
        String name=studentElement.getElementsByTagName("name").item(0).getTextContent();
        String email=studentElement.getElementsByTagName("email").item(0).getTextContent();
        String group=studentElement.getElementsByTagName("group").item(0).getTextContent();
        s.setName(name);
        s.setEmail(email);
        s.setGroup(group);
        return s;
    }
    
    private void loadData() {
        try {
            Document document= DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(file);
            Element root=document.getDocumentElement();
            NodeList studentElements=root.getChildNodes();
            for(int i=0; i<studentElements.getLength();i++)
            {
                Node studentElement=studentElements.item(i);
                if (studentElement.getNodeType() == Node.ELEMENT_NODE){
                    Student s=createStudentFromElement((Element)studentElement); //cast
                    super.save(s);
                }
            }
        } catch (Exception e) { }
    }

    private Element createElementFromStudent(Document document, Student student) {
        Element element=document.createElement("student");
        element.setAttribute("id",student.getID());
        Element name=document.createElement("name");
        name.setTextContent(student.getName());
        element.appendChild(name);
        Element email=document.createElement("email");
        email.setTextContent(student.getEmail());
        element.appendChild(email);
        Element group=document.createElement("group");
        group.setTextContent(student.getGroup());
        element.appendChild(group);
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
            Element root=document.createElement("students");
            document.appendChild(root);
            findAll().forEach(student -> {
                Element bookElement=createElementFromStudent(document, student);
                root.appendChild(bookElement);
            });
//write Document to file
            Transformer transformer = TransformerFactory.
                    newInstance().newTransformer();
            transformer.transform(new DOMSource(document),
                    new StreamResult(file));
        }catch (Exception ex){}
    }

    /**
     * @param validator the validator to be used in the repo
     */
    public StudentXMLRepo(Validator<Student> validator, String file) {
        super(validator);
        this.file=file;
        loadData();
    }

    @Override
    public Student save(Student s) throws ValidationException {
        Student st = super.save(s);
        writeToFile();
        return st;
    }

    @Override
    public Student update(Student entity) {
        Student st=super.update(entity);
        writeToFile();
        return st;
    }

    @Override
    public Student delete(String s) {
        Student st = super.delete(s);
        writeToFile();
        return st;
    }


}
