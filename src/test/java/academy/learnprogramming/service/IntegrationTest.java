package academy.learnprogramming.service;

import academy.learnprogramming.config.JAXRSConfiguration;
import academy.learnprogramming.entities.Employee;
import academy.learnprogramming.resource.EmployeeResource;
import com.ctc.wstx.sw.EncodingXmlWriter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceServiceTest {

    @EJB
    private PersistenceService persistenceService;
    @Inject
    private QueryService queryService;
    private Client client;
    private WebTarget webTarget;

    @ArquillianResource
    private URL base;


    @PersistenceContext
    EntityManager entityManager;


    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "payroll.war")
                .addPackage(PersistenceService.class.getPackage())
                .addPackage(Employee.class.getPackage())
                .addPackage(JAXRSConfiguration.class.getPackage())
                .addPackage(EmployeeResource.class.getPackage())
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }


//    @Before
//    public void init() {
//        persistenceService = new PersistenceService();
//        persistenceService.entityManager = entityManager;
//    }
//
//    @After
//    public void clean() {
//        entityManager.close();
//
//    }


    @Before
    public void init() throws MalformedURLException {
        client = ClientBuilder.newBuilder().connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS).build();
        webTarget = client.target(URI.create(new URL(base, "api/v1/employees/employees").toExternalForm()));

    }

    @After
    public void cleanUp() {
        client.close();

    }


    @Test
    public void test1() {

        Employee employee = new Employee();

        employee.setFullName("Donald Trump");
        employee.setSocialSecurityNumber("123495ufhdjkd");
        employee.setBasicSalary(new BigDecimal("350000"));
        employee.setHiredDate(LocalDate.now());
        employee.setDateOfBirth(LocalDate.of(1987, 10, 23));

        //Test persistence service
        persistenceService.saveEmployee(employee);


        //Test query service
        final List<Employee> employees = queryService.getEmployees();

        assertNotNull(employee.getId());
        assertNotNull(employees);
        assertTrue(employees.size() > 0);





    }

    @Test
    //    @RunAsClient
    public void test2() {

        Employee employee = new Employee();

        employee.setFullName("Lebron James");
        employee.setSocialSecurityNumber("123495ufhdjkd");
        employee.setBasicSalary(new BigDecimal("350000"));
        employee.setHiredDate(LocalDate.now());
        employee.setDateOfBirth(LocalDate.of(1987, 10, 23));

        Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(employee));
        assertNotNull(postResponse);

        //Test REST service
        JsonArray jsonArray = webTarget.request(MediaType.APPLICATION_JSON).get(JsonArray.class);

        assertNotNull(jsonArray);
        assertEquals(2, jsonArray.size());

    }
}
