package repository;

import domain.entities.Homework;
import domain.validator.ValidationException;
import domain.validator.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class HomeworkXMLRepo extends Repository<String, Homework> {
    
    private String filename;
    
    /**
     * @param validator the validator to be used in the repo
     */
    public HomeworkXMLRepo(Validator<Homework> validator, String f) {
        super(validator);
        filename=f;
        loadData();
    }

    private Homework createHomeworkFromElement(Element HomeworkElement) {
        Homework h=new Homework();
        String id=HomeworkElement.getAttribute("id");
        h.setID(id);
        String desc=HomeworkElement.getElementsByTagName("description").item(0).getTextContent();
        int deadline=Integer.parseInt(HomeworkElement.getElementsByTagName("deadline").item(0).getTextContent());
        h.setDescription(desc);
        h.setDeadlineWeek(deadline);
        return h;
    }

    private void loadData() {
        try {
            Document document= DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(filename);
            Element root=document.getDocumentElement();
            NodeList HomeworkElements=root.getChildNodes();
            for(int i=0; i<HomeworkElements.getLength();i++)
            {
                Node HomeworkElement=HomeworkElements.item(i);
                if (HomeworkElement.getNodeType() == Node.ELEMENT_NODE){
                    Homework h=createHomeworkFromElement((Element)HomeworkElement); //cast
                    super.save(h);
                }
            }
        } catch (Exception e) { }
    }

    private Element createElementFromHomework(Document document, Homework Homework) {
        Element element=document.createElement("homework");
        element.setAttribute("id",Homework.getID());
        Element deadline=document.createElement("deadline");
        deadline.setTextContent(Integer.toString(Homework.getDeadlineWeek()));
        element.appendChild(deadline);
        Element desc=document.createElement("description");
        desc.setTextContent(Homework.getDescription());
        element.appendChild(desc);
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
            Element root=document.createElement("homeworks");
            document.appendChild(root);
            findAll().forEach(Homework -> {
                Element hElement=createElementFromHomework(document, Homework);
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
    public Homework save(Homework s) throws ValidationException {
        Homework st = super.save(s);
        writeToFile();
        return st;
    }

    @Override
    public Homework update(Homework entity) {
        Homework st=super.update(entity);
        writeToFile();
        return st;
    }

    @Override
    public Homework delete(String s) {
        Homework st = super.delete(s);
        writeToFile();
        return st;
    }
    
}
