package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "TrackRequest")
public class TrackRequest implements RequestBody, RequestBuilder {

    @Element(name = Request.VERSION + ":WebAuthenticationDetail")
    private WebAuthenticationDetail webAuthenticationDetail;

    @Element(name = Request.VERSION + ":ClientDetail")
    private ClientDetail clientDetail;

    @Element(name = Request.VERSION + ":Version")
    private Version version;

    @Element(name = Request.VERSION + ":SelectionDetail")
    private SelectionDetail selectionDetail;

    public TrackRequest() {
        Version version = new Version();
        version.setServiceId("trck");
        version.setMajor("14");
        version.setIntermediate("0");
        version.setMinor("0");
        this.version = version;
    }

    public SelectionDetail getSelectionDetail() {
        return selectionDetail;
    }

    public void setSelectionDetail(SelectionDetail selectionDetail) {
        this.selectionDetail = selectionDetail;
    }

    public WebAuthenticationDetail getWebAuthenticationDetail() {
        return webAuthenticationDetail;
    }

    public void setWebAuthenticationDetail(WebAuthenticationDetail webAuthenticationDetail) {
        this.webAuthenticationDetail = webAuthenticationDetail;
    }

    public ClientDetail getClientDetail() {
        return clientDetail;
    }

    public void setClientDetail(ClientDetail clientDetail) {
        this.clientDetail = clientDetail;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    /**
     *
     * @param trackingNumber
     * @param key
     * @param password
     * @param accountNumber
     * @param meterNumber
     * @return
     */
    public static Request buildRequest(String trackingNumber, String key, String password, String accountNumber, String meterNumber) {
        TrackRequest trackRequest = new TrackRequest();

        PackageIdentifier identifier = new PackageIdentifier();
        identifier.setType(PackageIdentifier.TRACKING_NUMBER_OR_DOORTAG);
        identifier.setValue(trackingNumber);

        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setProcessingOptions(SelectionDetail.PROCESSING_OPTIONS_INCLUDE_DETAILED_SCANS);
        selectionDetail.setPackageIdentifier(identifier);

        trackRequest.setSelectionDetail(selectionDetail);

        WebAuthenticationDetail webAuthenticationDetail = new WebAuthenticationDetail();

        UserCredential userCredential = new UserCredential();
        userCredential.setKey(key);
        userCredential.setPassword(password);
        webAuthenticationDetail.setUserCredential(userCredential);

        trackRequest.setWebAuthenticationDetail(webAuthenticationDetail);

        ClientDetail clientDetail = new ClientDetail();
        clientDetail.setAccountNumber(accountNumber);
        clientDetail.setMeterNumber(meterNumber);

        trackRequest.setClientDetail(clientDetail);

        return Request.buildRequest(trackRequest);
    }

}
