package academy.learnprogramming.resource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.json.JsonArray;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class JaxRsClientTest {

    @Inject
    private JaxRsClient jaxRsClient;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(JaxRsClient.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void getBreaches() {

        JsonArray breaches = jaxRsClient.getBreaches("sinaisix@gmail.com");

        assertNotNull(breaches);

    }


}
