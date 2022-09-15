package de.gbv.metadata.solr;

import de.gbv.metadata.Authenticity;
import de.gbv.metadata.model.PersonLink;
import de.gbv.metadata.model.PlaceLink;
import de.gbv.metadata.model.Regest;
import org.apache.solr.common.SolrInputDocument;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MCRMetaLink;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.niofs.MCRPath;

import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class Regest2Solr extends BasicSolrInputDocumentConverter<Regest> {

  private static void bodyXML(MCRObject obj, SolrInputDocument base) {
    String derivate = obj.getStructure().getDerivates().stream().map(MCRMetaLink::getXLinkHref).findFirst().get();
    MCRPath path = MCRPath.getPath(derivate, "/regest.xml");
    String xmlAsString = null;

    try {
      xmlAsString = Files.readString(path);
    } catch (IOException e) {
      throw new MCRException(e);
    }

    base.setField("regest.xml", xmlAsString);
    base.setField("allMeta", xmlAsString);
  }

  @Override
  public SolrInputDocument convertToDocument(MCRObject obj, Regest regest) {
    SolrInputDocument base = super.convertToDocument(obj, regest);

    Optional.ofNullable(regest.getIdno()).ifPresent(idno -> base.setField("idno", idno));

    Optional.ofNullable(regest.getIssuer()).ifPresent(issuer -> {
      base.setField("issuer", issuer.getLabel());
      base.setField("issuer.obj", issuer.getLabel());
    });

    Optional.ofNullable(regest.getInitium()).ifPresent(initium -> {
      base.setField("initium", initium);
    });

    Optional.ofNullable(regest.getRecipient()).ifPresent(recipient -> {
      base.setField("recipient", recipient.getLabel());
      base.setField("recipient.obj", recipient.getMycoreId());
    });

    Optional.ofNullable(regest.getIssuedPlace()).ifPresent(issuedPlace -> {
      base.setField("issuedPlace", issuedPlace.getLabel());
      base.setField("issuedPlace.obj", issuedPlace.getMycoreId());
    });

    for (PersonLink bodyPerson : regest.getBodyPersons()) {
      base.addField("person.obj", bodyPerson.getMycoreId());
      base.addField("person", bodyPerson.getLabel());
    }


    for (PlaceLink bodyPlace : regest.getBodyPlaces()) {
      base.addField("place.obj", bodyPlace.getMycoreId());
      base.addField("place", bodyPlace.getLabel());
    }

    regest.getDeliveryForm().getCategids().forEach(id -> {
      base.addField("deliveryForm", id);
    });

    Optional.ofNullable(regest.getPontifikatAEP()).ifPresent(pontifikatAEP -> {
      base.setField("pontifikatAEP", pontifikatAEP.getLabel());
      base.setField("pontifikatAEP.obj", pontifikatAEP.getMycoreId());
    });

    Optional.ofNullable(regest.getPontifikatPP()).ifPresent(pontifikatPP -> {
      base.setField("pontifikatPP", pontifikatPP.getLabel());
      base.setField("pontifikatPP.obj", pontifikatPP.getMycoreId());
    });

    Authenticity authenticityStatus = regest.getAuthenticityStatus();
    base.addField("fake", authenticityStatus.isFake());
    base.addField("lost", authenticityStatus.isLost());
    base.addField("certainly", authenticityStatus.isCertainly());


    issuedDate(regest, base);
    bodyXML(obj, base);


    return base;
  }

  private static void issuedDate(Regest regest, SolrInputDocument base) {
    Optional.ofNullable(regest.getIssued()).ifPresent(issued -> {
      String text = issued.getText();
      if (text != null) {
        base.setField("issued.text", text);
      }

      Date from = issued.getFrom();
      Date to = issued.getTo();
      String dateRange;
      if(from != null || to != null){
        if (from != null && to != null) {
          dateRange = MessageFormat.format("[{0} TO {1}]", DateTimeFormatter.ISO_INSTANT.format(from.toInstant()), DateTimeFormatter.ISO_INSTANT.format(to.toInstant()));
        } else if (from != null) {
          dateRange = MessageFormat.format("[{0} TO *]", DateTimeFormatter.ISO_INSTANT.format(from.toInstant()));
        } else {
          dateRange = MessageFormat.format("[* TO {0}]", DateTimeFormatter.ISO_INSTANT.format(to.toInstant()));
        }
        base.setField("issued.range", dateRange);
      }
    });
  }


}
