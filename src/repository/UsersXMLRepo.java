package repository;

import domain.entities.User;
import domain.validator.ValidationException;
import domain.validator.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import repository.Repository;
import util.Password;
import util.UserType;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class UsersXMLRepo extends Repository<String, User> {

    private String filename;

    /**
     * @param validator the validator to be used in the repo
     */
    public UsersXMLRepo(Validator<User> validator, String filename) {
        super(validator);
        this.filename=filename;
        loadData();
    }


    private User createUserFromElement(Element userElement) {
        User u=new User();
        String email=userElement.getElementsByTagName("email").item(0).getTextContent();
        String password=userElement.getElementsByTagName("password").item(0).getTextContent();
        String type=userElement.getElementsByTagName("type").item(0).getTextContent();
        u.setEmail(email);
        u.setType(UserType.valueOf(type));
        u.setPassword(password);
        return u;
    }

    private void loadData() {
        try {
            Document document= DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(filename);
            Element root=document.getDocumentElement();
            NodeList userElements=root.getChildNodes();
            for(int i=0; i<userElements.getLength();i++)
            {
                Node userElement=userElements.item(i);
                if (userElement.getNodeType() == Node.ELEMENT_NODE){
                    User u=createUserFromElement((Element)userElement); //cast
                    super.save(u);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Element createElementFromUser(Document document, User user) {
        Element element=document.createElement("user");
        
        Element email=document.createElement("email");
        email.setTextContent(user.getEmail());
        element.appendChild(email);
        
        Element password=document.createElement("password");
        password.setTextContent(user.getPassword());
        element.appendChild(password);

        Element type=document.createElement("type");
        type.setTextContent(user.getType().toString());
        element.appendChild(type);
        
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
            Element root=document.createElement("users");
            document.appendChild(root);
            findAll().forEach(User -> {
                Element uElement=createElementFromUser(document, User);
                root.appendChild(uElement);
            });
//write Document to file
            Transformer transformer = TransformerFactory.
                    newInstance().newTransformer();
            transformer.transform(new DOMSource(document),
                    new StreamResult(filename));
        }catch (Exception ex){}
    }

    @Override
    public User save(User s) throws ValidationException {
        User st = super.save(s);
        writeToFile();
        return st;
    }

    @Override
    public User update(User entity) {
        User st=super.update(entity);
        writeToFile();
        return st;
    }

    @Override
    public User delete(String id) {
        User st = super.delete(id);
        writeToFile();
        return st;
    }

}
