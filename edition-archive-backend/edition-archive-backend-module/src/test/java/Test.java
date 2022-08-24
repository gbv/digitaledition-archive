import de.gbv.metadata.CEIImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRTestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Test extends MCRTestCase {
    private static final Logger LOGGER = LogManager.getLogger();

    @org.junit.Test
    public void testTest() throws IOException, JDOMException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get("../../import/2022-08-18_regesten_gesamt_red_v-09-formatted.xml"));
        XMLOutputter xmlOutputter = new XMLOutputter();
        ceiImporter.runImport();
        ceiImporter.getPersons().forEach(LOGGER::info);
    }

    @Override
    protected Map<String, String> getTestProperties() {
        Map<String, String> testProperties = super.getTestProperties();

        testProperties.put("MCR.Metadata.JSON.Type.AllowedClasses","de.gbv.metadata.Regest");

        return testProperties;
    }
}
